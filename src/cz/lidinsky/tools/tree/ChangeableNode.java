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

import static org.apache.commons.lang3.Validate.notNull;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 *  A node of the tree.
 */
public class ChangeableNode<T> extends Node<T> {

  public Node<T> addChild(final Node<T> node) {
    notNull(node);
    if (children == null) {
      children = new ArrayList<Node<T>>();
    }
    children.add(node);
    node.parent = this;
    return node;
  }

  public void insertChild(Node<T> node, int position) {
    if (children == null) {
      children = new ArrayList<Node<T>>();
    }
    children.add(position, node);
    node.parent = this;
  }

  public void removeChild(Node<T> node) {
    notNull(node);
    if (children == null) {
      throw new NoSuchElementException();
    }
    if (children.remove(node)) {
      node.parent = null;
    } else {
      throw new NoSuchElementException();
    }
  }

  public Node<T> removeChild(int index) {
    if (children != null) {
      return children.remove(index);
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

}
