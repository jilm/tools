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

import java.util.HashMap;

public class FormatterChain {

  private HashMap<Class<?>, Formatter<?>> formatters
                          = new HashMap<Class<?>, Formatter<?>>();

  public static <T> String format(T object, FormatterChain chain) {
    Formatter<T> formatter = (Formatter<T>)chain.formatters
      .get(object.getClass());
    if (formatter != null) {
      return formatter.format(object);
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.NO_SUCH_ELEMENT);
    }
  }

  public static <T> void add(
      FormatterChain chain, Class<T> type, Formatter<? super T> formatter) {

    chain.formatters.put(type, formatter);
  }

}
