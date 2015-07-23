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
public abstract class Node<T> implements INode {

  protected Node<T> parent;

  protected ArrayList<Node<T>> children;

  public Node<T> getParent() {
    return parent;
  }

  public Collection<Node<T>> getChildren() {
    return CollectionUtils.unmodifiableCollection(
        CollectionUtils.emptyIfNull(children));
  }

  public Node<T> getChild(int index) {
    if (children != null) {
      return children.get(index);
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  public int getIndexOfChild(Node<T> node) {
    if (children != null) {
      return children.indexOf(node);
    } else {
      throw new NoSuchElementException();
    }
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isLeaf() {
    return children == null || children.size() == 0;
  }

  public boolean hasChildren() {
    return !isLeaf();
  }

  public boolean isSibling(Node<T> node) {
    notNull(node);
    if (node == this) {
      return true; // ???
    } else if (parent == null) {
      return false;
    } else {
      return parent == node.getParent();
    }
  }

  public Node<T> getRoot() {
    Node<T> node = this;
    while (node.getParent() != null) {
      node = node.getParent();
    }
    return node;
  }

  private T decorated;

  public T getDecorated() {
    return decorated;
  }

  public void setDecorated(T object) {
    this.decorated = object;
  }

}
