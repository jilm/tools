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

package cz.lidinsky.tools;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;
import java.util.List;

public class Validate {

  public static <T> T notNull(T object) {
    if (object == null) {
      throw new CommonException()
        .setCode(ExceptionCode.NULL_POINTER);
    } else {
      return object;
    }
  }

  public static <T> T notNull(T object, T def) {
    return object == null ? def : object;
  }

  public static <T> Collection<T> notEmpty(Collection<T> collection) {
    if (notNull(collection).isEmpty()) {
      throw new CommonException()
        .setCode(ExceptionCode.EMPTY)
        .set("message", "Given collection is empty!");
    } else {
      return collection;
    }
  }

  public static String notBlank(String object) {
    if (isBlank(object)) {
      throw new CommonException()
        .setCode(ExceptionCode.BLANK_ARGUMENT)
        .set("message", "The argument is blank!")
        .set("argument", object);
    } else {
      return object;
    }
  }

  public static String notBlank(String object, String def) {
    return isBlank(object) ? def : object;
  }

  public static int checkIndex(final List<?> list, final int index) {
    return checkIndex(list.size(), index);
  }

  public static int checkIndex(final int size, final int index) {
    if (index < 0) {
      throw new CommonException()
        .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
        .set("index", index);
    } else if (index >= size) {
      throw new CommonException()
        .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
        .set("index", index)
        .set("list size", size);
    } else {
      return index;
    }
  }

  public static int notNegative(final int number) {
    if (number < 0) {
      throw new CommonException()
        .setCode(ExceptionCode.NEGATIVE_INDEX)
        .set("number", number);
    } else {
      return number;
    }
  }

}
