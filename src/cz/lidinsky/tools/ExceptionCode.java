/*
 *  Copyright 2015 Jiri Lidinsky
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

package cz.lidinsky.tools;

public enum ExceptionCode {

  NOT_SPECIFIED       (null),
  ILLEGAL_ARGUMENT    (IllegalArgumentException.class),
  NULL_ARGUMENT       (NullPointerException.class),
  NULL_POINTER        (NullPointerException.class),
  BLANK_ARGUMENT      (IllegalArgumentException.class),
  INDEX_OUT_OF_BOUNDS (IndexOutOfBoundsException.class),
  DUPLICATE_ELEMENT   (null),
  NO_SUCH_ELEMENT     (java.util.NoSuchElementException.class),
  ILLEGAL_STATE       (IllegalStateException.class),
  PARSE               (null),
  CYCLIC_DEFINITION   (null),
  CLASS_CAST          (ClassCastException.class),
  CLASS_NOT_FOUND     (ClassNotFoundException.class),
  INSTANTIATION       (InstantiationException.class),
  ILLEGAL_ACCESS      (IllegalAccessException.class),
  UNSUPPORTED_TYPE    (null);

  ExceptionCode(Class<? extends Throwable> e) {
    counterpartException = e;
  }

  private Class<? extends Throwable> counterpartException;

  public Class<? extends Throwable> getCounterpart() {
    return counterpartException;
  }

}
