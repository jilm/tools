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

import org.apache.commons.lang3.text.StrBuilder;

public class ToStringMultilineStyle extends ToStringBuilder {

  public ToStringMultilineStyle() {
    super();
  }

  public ToStringMultilineStyle(StrBuilder sb) {
    super(sb);
  }

  protected ToStringMultilineStyle(StrBuilder sb, String indent) {
    super(sb, indent);
  }

  //-----------------------------------

  @Override
  protected void appendValue(IToStringBuildable object) {
    if (object == null) {
      appendNull();
    } else {
      appendClassName(object.getClass());
      appendHashCode(object);
      startObject();
      ToStringBuilder nested = new ToStringMultilineStyle(sb, incIndent());
      nested.newLine();
      object.toString(nested);
      nested.removeFieldDelimiter();
      endObject();
    }
  }

  @Override
  protected void appendValue(Iterable value) {
    if (value == null) {
      appendNull();
    } else if (!value.iterator().hasNext()) {
      appendEmptyCollection();
    } else {
      appendClassName(value.getClass());
      appendHashCode(value);
      startArray();
      for (Object element : value) {
        appendValue(element);
        newLine();
        //appendArrayDelimiter();
      }
      endArray();
    }
  }

  @Override
  protected void startObject() {
    sb.append('[');
    mark();
  }

  @Override
  protected void endObject() {
    newLine();
    sb.append(']');
    mark();
    newLine();
  }

  @Override
  protected void appendFieldDelimiter() {
    mark();
    sb.append(fieldDelimiter);
    newLine();
  }

  @Override
  protected String incIndent() {
    return indent + "  ";
  }

  @Override
  protected void removeFieldDelimiter() {
    removeToMark();
  }

}
