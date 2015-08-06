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

}
