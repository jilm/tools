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

/**
 *
 *  Helps to build a tree.
 *
 */
public class Builder<T> {

  private Node<T> pointer;

  public void open() {
    if (pointer == null) {
      pointer = new ChangeableNode<T>();
    } else {
      pointer = ((ChangeableNode)pointer).addChild(new ChangeableNode<T>());
    }
  }

  public Node<T> close(T object) {
    pointer.setDecorated(object);
    Node<T> result = pointer;
    pointer = pointer.getParent();
    return result;
  }

}
