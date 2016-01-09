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
import java.net.Socket;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author jilm
 */
public class Client {
    
    private final OutputStreamWriter writer;
    private final JSONTokener reader;
    
    public Client(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        writer = new OutputStreamWriter(socket.getOutputStream());
        reader = new JSONTokener(socket.getInputStream());
    }
    
    public void send(JSONObject object) throws IOException {
        object.write(writer);
        writer.flush();
    }
    
    public Object read() {
        return reader.nextValue();
    }
    
    public void close() throws IOException {
        writer.close();
    }
    
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 12345);
        JSONObject object = new JSONObject();
        object.put("class", "control4j.Signal");
        object.put("valid", true);
        object.put("value", 45.689d);
        client.send(object);
        Object received = client.read();
        System.out.println(received);
        client.close();
    }
    
}
