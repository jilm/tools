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

import org.apache.commons.collections4.Transformer;

public class FunctorUtils {

  public static <I, O> Transformer<I, O> chainedTransformer(
      final Transformer<I, O> ... transformers) {

    return new Transformer<I, O>() {
      public O transform(I param) {
        for (Transformer<I, O> transformer : transformers) {
          try {
            return transformer.transform(param);
          } catch (Exception e) {} // It is OK, just try another transformer
        }
        throw new CommonException()
          .setCode(ExceptionCode.NO_SUCH_ELEMENT)
          .set("message", "There is no one successful tranformer!")
          .set("param", param);
      }
    };
  }

}
