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

  ACCESS_DENIED       (null),
  BLANK_ARGUMENT      (IllegalArgumentException.class),
  CLASS_CAST          (ClassCastException.class),
  CLASS_NOT_FOUND     (ClassNotFoundException.class),
  CYCLIC_DEFINITION   (null),
  DUPLICATE_ELEMENT   (null),
  EMPTY               (null),
  ILLEGAL_ACCESS      (IllegalAccessException.class),
  ILLEGAL_ARGUMENT    (IllegalArgumentException.class),
  ILLEGAL_STATE       (IllegalStateException.class),
  IMPLEMENTATION      (null),
  INDEX_OUT_OF_BOUNDS (IndexOutOfBoundsException.class),
  INSTANTIATION       (InstantiationException.class),
  INVOCATION_TARGET   (java.lang.reflect.InvocationTargetException.class),
  NEGATIVE_INDEX      (null),
  NOT_SPECIFIED       (null),
  NO_SUCH_ELEMENT     (java.util.NoSuchElementException.class),
  NULL_ARGUMENT       (NullPointerException.class),
  NULL_POINTER        (NullPointerException.class),
  PARSE               (null),
  UNSUPPORTED_OPERATION (UnsupportedOperationException.class),
  UNSUPPORTED_TYPE    (null), SYNTAX_ERROR (null), AMBIGUITY (null);

  ExceptionCode(Class<? extends Throwable> e) {
    counterpartException = e;
  }

  private Class<? extends Throwable> counterpartException;

  public Class<? extends Throwable> getCounterpart() {
    return counterpartException;
  }

  public static ExceptionCode getCode(Class<? extends Throwable> throwable) {
    if (throwable == null) {
      return NOT_SPECIFIED;
    }
    for (ExceptionCode code : ExceptionCode.values()) {
      if (code.counterpartException != null
          && code.counterpartException.equals(throwable)) {
        return code;
      }
    }
    return NOT_SPECIFIED;
  }

}
