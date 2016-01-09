package cz.lidinsky.tools.dispatch;

/*
 *  Copyright 2013, 2015, 2016 Jiri Lidinsky
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

import java.util.LinkedList;

/**
 *
 * FIFO data structure. This data structure is dedicated for information
 * exchange between threads. All of the methods are synchronized.
 *
 * @param <T>
 */
public class Queue<T>
{

  /** The queue. */
  protected final LinkedList<T> list = new LinkedList<>();

  /**
   *  Adds an item at the end of the queue.
   *
   *  @param item 
   *             an item to be added at the end of the queue
   */
  public synchronized void queue(T item)
  {
    list.add(item);
    notify();
  }

  /**
   *  Removes and returns an item from the head (first item) of the queue.
   *
   *  @return an item at the head of the queue or null if
   *             the queue is empty
   */
  public synchronized T dequeue()
  {
    return list.poll();
  }
  
  /**
   *  Removes all of the elements from this queue.
   */
  public synchronized void clear()
  {
    list.clear();
  }

  /**
   *  Removes and returns an item from the head (first item) of the 
   *  queue. If the queue is empty, this method blocks until there
   *  is an item to return.
   *  
   *  @return an item at the head of the queue 
   */
  public synchronized T blockingDequeue()
  {
    while (list.isEmpty()) {
      try { 
          wait(); 
      } catch (InterruptedException e) {
      // it is OK
      }
    }
    return list.poll();
  }

  /**
   *  Returns true if and only if the queue is empty, it means, it doesn't
   *  contain any item.
   *
   *  @return true if the queue is empty, false otherwise.
   */
  public synchronized boolean isEmpty()
  {
    return list.isEmpty();
  }
  
}
