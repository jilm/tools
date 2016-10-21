/*
 *  Copyright 2016 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.lidinsky.tools.dispatch;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

/**
 * The dispatcher is a server application which listens on the given port. Each
 * client which connects may send and message which is resent to all of the
 * clients.
 */
public class Dispatcher {

    private final Queue<Object> buffer;
    private final Set<Client> clients;
    private final Logger logger;

    /**
     * The port number on which the server listens.
     *
     * @param port
     *            the port number
     */
    public Dispatcher(final int port) {
        this.logger = Logger.getLogger(getClass().getName());
        this.port = port;
        this.buffer = new Queue();
        this.clients = new HashSet<>();
        this.closed = false;
    }
    public void add(Object object) {
        if (object != null) {
            buffer.queue(object);
        }
    }

    private synchronized void addClient(Client client) {
      clients.add(client);
      logger.log(Level.INFO, "New client added; number of clients: {0}", Integer.toString(clients.size()));
    }

    /**
     * Removes the given client object from the internal collection.
     * @param client
     */
    private synchronized void remove(Client client) {
      clients.remove(client);
      logger.log(Level.INFO, "Client removed; number of clients: {0}", Integer.toString(clients.size()));
    }

    /**
     * Sends the given object to all of the connected clients.
     *
     * @param object
     */
    private synchronized void send(Object object) {
      clients.stream().forEach(p -> p.send(object));
    }

    private boolean closed = false;

    /**
     * A loop
     */
    private void run() {
        while (!closed) {
            Object object = buffer.blockingDequeue();
            logger.log(Level.FINEST, "Going to send a message.\n{0}", object.toString());
            send(object);
        }
    }

    /**
     * Starts the server thread.
     */
    public void start() {
        new Thread(this::run).start();
    }

    private class Client {

        private final OutputStreamWriter streamWriter;
        private final JSONTokener reader;
        private final JSONWriter writer;
        private final Queue<Object> buffer;
        private boolean closed;

      public Client(Socket socket) throws IOException {
        reader = new JSONTokener(socket.getInputStream());
        streamWriter = new OutputStreamWriter(socket.getOutputStream());
        writer = new JSONWriter(streamWriter);
        buffer = new Queue<>();
        closed = false;
        new Thread(this::inputLoop).start();
        new Thread(this::outputLoop).start();
      }

      public void send(Object object) {
        buffer.queue(object);
      }

      private void inputLoop() {
        try {
          while (!closed) {
              logger.finest("Going to wait for a message...");
            Object object = reader.nextValue();
            logger.log(Level.FINEST, "Message received!\n{0}", object.toString());
            Dispatcher.this.buffer.queue(object);
          }
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        } finally {
            try {
                close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
      }

      private void outputLoop() {
        try {
          while (!closed) {
            Object object = this.buffer.blockingDequeue();
            if (object instanceof JSONObject) {
                ((JSONObject)object).write(streamWriter);
                streamWriter.flush();
            } else {
                logger.warning("Unsupported object!");
            }
          }
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        } finally {
            try {
                close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
      }

      public void close() throws IOException {
        closed = true;
        streamWriter.close();
        //if (reader != null) reader.close();
        //if (writer != null) writer.close();
        remove(this);
      }

    }

    /**
     *
     */
    public void close() {
      closed = true;
    }

    /** Port on which the server listens. */
    private final int port;

    /**
     * Opens server socket on the given port. New Client object is created for
     * each new incoming connection request.
     */
    private void server() {
      try {
        ServerSocket socketServer = new ServerSocket(port);
        logger.info("Server socket created...");
        while (!closed) {
          Socket socket = socketServer.accept();
          addClient(new Client(socket));
        }
      } catch (Exception e) {
          logger.log(Level.SEVERE, null, e);
      } finally {
        close();
      }
    }

    /**
     * It expects port number as an command line argument. It creates an
     * instance of the Dispatcher class and runs it.
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
      int port = Integer.parseInt(args[0]);
      Dispatcher instance = new Dispatcher(port);
      instance.start();
      instance.server();
    }

}
