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

import org.apache.commons.lang3.tuple.Pair;

public class ToStringDecoratorFactory {

  public static IToStringBuildable decorate(Object object) {
    if (object != null) {
      if (object instanceof Pair) {
	return new PairDecorator((Pair)object);
      }
    }
    return null;
  }

  private static class PairDecorator implements IToStringBuildable {

    private Pair object;

    PairDecorator(Pair object) {
      this.object = object;
    }

    public void toString(ToStringBuilder builder) {
      builder.append("left", object.getLeft())
	  .append("right", object.getRight());
    }

  }

}
