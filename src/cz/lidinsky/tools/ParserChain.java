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

import java.util.HashMap;

public class ParserChain {

  private HashMap<Class<?>, Parser<?>> parsers
                        = new HashMap<Class<?>, Parser<?>>();

  public static <T> T parse(ParserChain chain, String text, Class<T> type) {
    Parser<T> parser = (Parser<T>)chain.parsers.get(type);
    if (parser != null) {
      return parser.parse(text);
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.NO_SUCH_ELEMENT);
    }
  }

  public static <T> void add(
      ParserChain chain, Class<T> type, Parser<T> parser) {

    chain.parsers.put(type, parser);
  }

}
