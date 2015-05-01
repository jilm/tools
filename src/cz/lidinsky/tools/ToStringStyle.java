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

public class ToStringStyle {

  public ToStringStyle() {}

  protected StringBuilder sb = new StringBuilder();
  protected int mark;

  protected ToStringStyle child = null;

  ToStringStyle appendFieldsDelimiter() {
    newLine();
    return this;
  }
		
  protected int indentionLevel = 0;
  protected int indentionStep = 2;
		    
  ToStringStyle appendIndention() {
    for (int i = 0; i < indentionLevel * indentionStep; i++) {
      sb.append(' ');
    }
    return this;
  }
					
  ToStringStyle appendFieldName(String fieldName) {
    sb.append(fieldName);
    mark = sb.length();
    return this;
  }

  protected String fieldValueDelimiter = "=";

  ToStringStyle appendFieldValueDelimiter() {
    sb.append(fieldValueDelimiter);
    mark = sb.length();
    return this;
  }

  protected String nullLabel = "<null>";

  ToStringStyle appendNull() {
    sb.append(nullLabel);
    mark = sb.length();
    return this;
  }

  protected char objectStart = '[';

  ToStringStyle startObject() {
    sb.append(objectStart);
    mark = sb.length();
    indentionLevel++;
    newLine();
    return this;
  }

  protected char objectEnd = ']';

  ToStringStyle endObject() {
    deleteFromMark();
    indentionLevel--;
    newLine();
    sb.append(objectEnd);
    mark = sb.length();
    return this;
  }

  ToStringStyle appendText(String text) {
    sb.append(text);
    mark = sb.length();
    return this;
  }

  protected char arrayStart = '{';

  ToStringStyle startArray() {
    sb.append(arrayStart);
    mark = sb.length();
    indentionLevel++;
    newLine();
    return this;
  }

  protected char arrayEnd = '}';

  ToStringStyle endArray() {
    deleteFromMark();
    indentionLevel--;
    newLine();
    sb.append(arrayEnd);
    mark = sb.length();
    return this;
  }

  protected char arrayDelimiter = ',';

  ToStringStyle appendArrayDelimiter() {
    sb.append(arrayDelimiter);
    return this;
  }

  ToStringStyle appendObject(Object object) {
    sb.append(object.toString());
    mark = sb.length();
    return this;
  }

  protected boolean shortClassName = true;

  ToStringStyle appendClassName(Object object) {
    if (shortClassName) {
      sb.append(object.getClass().getSimpleName());
    } else {
      sb.append(object.getClass().getName());
    }
    mark = sb.length();
    return this;
  }

  ToStringStyle appendHash(Object object) {
    sb.append('@')
      .append(Integer.toHexString(object.hashCode()));
    mark = sb.length();
    return this;
  }

  ToStringStyle append(int i) {
    sb.append(i);
    mark = sb.length();
    return this;
  }

  protected void newLine() {
    sb.append("\n");
    appendIndention();
  }

  protected void deleteFromMark() {
    sb.delete(mark, sb.length());
  }

  @Override
  public String toString() {
    return sb.toString();
  }

}
