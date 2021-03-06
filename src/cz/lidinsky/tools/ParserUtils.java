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

public class ParserUtils {

  public static Parser<Integer> intParser() {
    return new Parser<Integer>() {
      public Integer parse(String text) {
        return Integer.valueOf(text);
      }
    };
  }

  public static Parser<Double> doubleParser() {
    return new Parser<Double>() {
      public Double parse(String text) {
        return Double.valueOf(text);
      }
    };
  }

  public static Parser<Boolean> booleanParser() {
    return new Parser<Boolean>() {
      public Boolean parse(String text) {
        return Boolean.valueOf(text);
      }
    };
  }

}
