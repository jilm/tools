package cz.lidinsky.tools.tree;

/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of java tools library.
 *
 *  java tools is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  java tools library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with java tools library.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayDeque;

public class DFSIterator<E extends INode<E>> implements Iterator<E> {

  private HashSet<E> discovered = new HashSet<E>();
  private ArrayDeque<E> stack = new ArrayDeque<E>();
  private E next;

  public DFSIterator(E root) {
    next = root;
  }

  public E next() {
    discovered.add(next);
    for (E child : next.getChildren()) {
      stack.push(child);
    }
    // find next node to return
    E node = next;
    while (!stack.isEmpty()) {
      next = stack.pop();
      if (!discovered.contains(next)) {
	break;
      } else {
        next = null;
      }
    }
    return node;
  }

  public boolean hasNext() {
    return next != null;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

}
