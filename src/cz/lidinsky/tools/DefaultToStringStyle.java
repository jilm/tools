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

  protected StringBuilder sb;

  public DefaultToStringStyle() {
    sb = new StringBuilder();
  }

  public DefaultToStringStyle(StringBuilder sb) {
    this.sb = sb;
  }

  /*
   *
   *     Object Fields
   *
   */

  public void append(String fieldName, String value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(String value) {
    appendValue(value);
  }

  public void append(String fieldName, String[] value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(String[] value) {
    appendValue(value);
  }

  public void append(String fieldName, int value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(String fieldName, int[] value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(int value) {
    appendValue(value);
  }

  public void append(int[] value) {
    appendValue(value);
  }

  public void append(String fieldName, long value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(String fieldName, long[] value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(long value) {
    appendValue(value);
  }

  public void append(long[] value) {
    appendValue(value);
  }

  public void append(String fieldName, boolean value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(String fieldName, boolean[] value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(boolean value) {
    appendValue(value);
  }

  public void append(boolean[] value) {
    appendValue(value);
  }

  public void append(String fieldName, float value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(String fieldName, float[] value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(float value) {
    appendValue(value);
  }

  public void append(float[] value) {
    appendValue(value);
  }

  public void append(String fieldName, double value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(String fieldName, double[] value) {
    appendFieldName(fieldName);
    appendValue(value);
    appendFieldDelimiter();
  }

  public void append(double value) {
    appendValue(value);
  }

  public void append(double[] value) {
    appendValue(value);
  }

  /*
   *
   *     Values
   *
   */

  protected void appendValue(String value) {
    if (value == null) {
      appendNull();
    } else {
      sb.append(value);
    }
  }

  protected void appendValue(int value) {
    sb.append(value);
  }

  protected void appendValue(long value) {
    sb.append(value);
  }

  protected void appendValue(boolean value) {
    sb.append(value);
  }

  protected void appendValue(float value) {
    sb.append(value);
  }

  protected void appendValue(double value) {
    sb.append(value);
  }

  protected void appendValue(Object value) {
    if (value == null) {
      appendNull();
    } else {
      sb.append(value);
    }
  }

  protected void appendValue(String[] value) {
    if (value == null) {
      appendNull();
    } else {
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  protected void appendValue(int[] value) {
    if (value == null) {
      appendNull();
    } else {
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  protected void appendValue(long[] value) {
    if (value == null) {
      appendNull();
    } else {
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  protected void appendValue(boolean[] value) {
    if (value == null) {
      appendNull();
    } else {
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  protected void appendValue(float[] value) {
    if (value == null) {
      appendNull();
    } else {
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  protected void appendValue(double[] value) {
    if (value == null) {
      appendNull();
    } else {
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  //-------------------------

  protected int fieldCount = 0;

  protected void appendFieldName(String fieldName) {
    if (fieldName != null) {
      sb.append(fieldName);
      appendFieldValueDelimiter();
    }
  }

  protected void appendHashCode(Object object) {
    sb.append('@');
    sb.append(Integer.toHexString(object.hashCode()));
  }

  protected boolean simpleClassName = true;

  protected void appendClassName(Class _class) {
    if (simpleClassName) {
      sb.append(_class.getSimpleName());
    } else {
      sb.append(_class.getName());
    }
  }

  protected void startObject() {
    sb.append('[');
    fieldCount = 0;
  }

  protected void endObject() {
    if (fieldCount > 0) {
      removeFieldDelimiter();
    }
    sb.append(']');
  }

  protected void removeLastChars(int count) {
    sb.delete(sb.length() - count, sb.length());
  }

  protected void appendFieldValueDelimiter() {
    sb.append('=');
    fieldCount++;
  }

  protected String fieldDelimiter = ",";

  protected void appendFieldDelimiter() {
    sb.append(fieldDelimiter);
  }

  protected void removeFieldDelimiter() {
    removeLastChars(fieldDelimiter.length());
  }

  protected void appendNull() {
    sb.append("<null>");
  }

  protected void startArray() {
    sb.append('{');
  }

  protected void endArray() {
    if (arrayElementCount > arraySize) {
      sb.append("... ");
      sb.append("<total size: ");
      sb.append(arrayElementCount);
      sb.append(">");
      sb.append('}');
    } else {
      removeArrayDelimiter();
      sb.append('}');
    }
  }

  protected void removeArrayDelimiter() {
    removeLastChars(arrayDelimiter.length());
  }

  protected String arrayDelimiter = ",";

  protected int arrayElementCount = 0;

  protected void appendArrayDelimiter() {
    sb.append(arrayDelimiter);
    arrayElementCount++;
  }

  protected static int arraySize = 10;

  protected int getArraySize(int arrayLength) {
    return Math.min(arraySize, arrayLength);
  }

}
