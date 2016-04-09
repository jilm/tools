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

import java.util.Collection;
import java.util.List;

public class CollectionUtils {

  public static <T> void add(List<T> list, int index, T element) {
    while (list.size() <= index) {
      list.add(null);
    }
    list.add(index, element);
  }

  public static <T> T getSingleton(Collection<T> collection) {
    if (collection.size() == 1) {
      return collection.iterator().next();
    } else if (collection.isEmpty()) {
      throw new CommonException()
              .setCode(ExceptionCode.NO_SUCH_ELEMENT)
              .set("message", "Cannot return signle element from an empty collection!");
    } else {
      throw new CommonException()
              .setCode(ExceptionCode.AMBIGUITY)
              .set("message", "Given collection contains more than one element!");
    }
  }

  public static <T> T getSingleton(T[] array) {
    switch (array.length) {
      case 0:
        throw new CommonException()
                .setCode(ExceptionCode.NO_SUCH_ELEMENT)
                .set("message", "Cannot return signle element from an empty array!");
      case 1:
        return array[0];
      default:
        throw new CommonException()
                .setCode(ExceptionCode.AMBIGUITY)
                .set("message", "Given array contains more than one element!");
    }
  }

}
