package cz.lidinsky.tools;

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

public class FormatterUtils {

  public static Formatter<Number> numberFormatter() {
    return new Formatter<Number>() {
      public String format(Number value) {
        return value.toString();
      }
    };
  }

  public static Formatter<Object> toStringFormatter() {
    return new Formatter<Object>() {
      public String format(Object object) {
        return object.toString();
      }
    };
  }

}
