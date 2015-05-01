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

public class DefaultToStringStyle {

  public DefaultToStringStyle() {}

  public void append(StringBuffer sb, String fieldName, String value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuffer sb, String value) {
    appendValue(sb, value);
  }

  public void append(StringBuffer sb, String fieldName, String[] value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuffer sb, String[] value) {
    appendValue(sb, value);
  }

  public void append(StringBuffer sb, String fieldName, int value) {
  }

  public void append(StringBuffer sb, String fieldName, int[] value) {
  }

  //-------------------------

  protected void appendValue(StringBuffer sb, String value) {
    if (value == null) {
      appendNull(sb);
    } else {
      sb.append(value);
    }
  }

  protected void appendValue(StringBuffer sb, int value) {
    sb.append(value);
  }

  protected void appendValue(StringBuffer sb, long value) {
    sb.append(value);
  }

  protected void appendValue(StringBuffer sb, boolean value) {
    sb.append(value);
  }

  protected void appendValue(StringBuffer sb, float value) {
    sb.append(value);
  }

  protected void appendValue(StringBuffer sb, double value) {
    sb.append(value);
  }

  protected void appendValue(StringBuffer sb, Object value) {
    if (value == null) {
      appendNull(sb);
    } else {
      sb.append(value);
    }
  }

  protected void appendValue(StringBuffer sb, String[] value) {
    if (value == null) {
      appendNull(sb);
    } else {
      startArray(sb);
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(sb, value[i]);
        appendArrayDelimiter(sb);
      }
      endArray(sb, value.length);
    }
  }

  //-------------------------

  protected void appendFieldName(StringBuffer sb, String fieldName) {
    if (fieldName != null) {
      sb.append(fieldName);
      appendFieldDelimiter(sb);
    }
  }

  protected void appendFieldDelimiter(StringBuffer sb) {
    sb.append('=');
  }

  protected void appendNull(StringBuffer sb) {
    sb.append("<null>");
  }

  protected void startArray(StringBuffer sb) {
    sb.append('{');
  }

  protected void endArray(StringBuffer sb, int arrayLength) {
    sb.append('}');
  }

  protected static int arraySize = 10;

  protected int getArraySize(int arrayLength) {
    return Math.min(arraySize, arrayLength);
  }

}
