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

public class ToStringMultilineStyle extends ToStringBuilder {

  public ToStringMultilineStyle() {
    super();
  }

  public ToStringMultilineStyle(StringBuilder sb) {
    super(sb);
  }

  protected ToStringMultilineStyle(StringBuilder sb, String indent) {
    super(sb, indent);
  }

  //-----------------------------------

  @Override
  protected void startObject() {
    sb.append('[');
    mark();
    newLine();
  }

  @Override
  protected void endObject() {
    removeToMark();
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

}
