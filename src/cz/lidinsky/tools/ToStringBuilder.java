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

import java.util.Map;
import java.util.Set;
import java.util.ArrayDeque;

public class ToStringBuilder {

  protected StringBuilder sb;

  public ToStringBuilder() {
    this(new StringBuilder());
  }

  public ToStringBuilder(StringBuilder sb) {
    this.sb = sb;
  }

  protected ToStringBuilder(StringBuilder sb, String indent) {
    this(sb);
    this.indent = indent;
  }

  public ToStringBuilder append(String fieldName, IToStringBuildable object) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(object);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object object) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(object);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, String value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Iterable value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String value) {
    if (value != null) {
      appendValue(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, String[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String[] value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(String fieldName, int value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, long value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, boolean value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, float value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, double value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(IToStringBuildable object) {
    if (object != null) {
      appendValue(object);
    }
    return this;
  }

  public ToStringBuilder append(Object object) {
    if (object != null) {
      appendValue(object);
    }
    return this;
  }

  public ToStringBuilder append(Iterable object) {
    if (object != null) {
      appendValue(object);
    }
    return this;
  }

  public ToStringBuilder append(int value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(long value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(boolean value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(float value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(double value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(String fieldName, IToStringBuildable[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, int[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, long[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, boolean[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, float[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, double[] value) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      appendValue(value);
      appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(int[] value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(long[] value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(boolean[] value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(float[] value) {
    appendValue(value);
    return this;
  }

  public ToStringBuilder append(double[] value) {
    appendValue(value);
    return this;
  }

  //-----------------------------------

  protected void appendValue(IToStringBuildable object) {
    if (object == null) {
      appendNull();
    } else {
      appendClassName(object.getClass());
      appendHashCode(object);
      startObject();
      ToStringBuilder nested = new ToStringBuilder(sb, incIndent());
      object.toString(nested);
      nested.removeFieldDelimiter();
      endObject();
    }
  }

  protected void appendValue(IToStringBuildable[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      appendClassName(value.getClass());
      appendHashCode(value);
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  protected void appendValue(Object[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      appendClassName(value.getClass());
      appendHashCode(value);
      startArray();
      for (int i = 0; i < getArraySize(value.length); i++) {
        appendValue(value[i]);
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  protected void appendValue(Object object) {
    if (object == null) {
      appendNull();
    } else {
      if (object instanceof IToStringBuildable) {
        appendValue((IToStringBuildable)object);
      } else if (object instanceof Integer) {
        appendValue(((Integer)object).intValue());
      } else {
        sb.append(object);
      }
    }
  }

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
        appendArrayDelimiter();
      }
      endArray();
    }
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  //-----------------------------------

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

  protected void appendValue(String[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
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
    } else if (value.length == 0) {
      appendEmptyArray();
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
    } else if (value.length == 0) {
      appendEmptyArray();
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
    } else if (value.length == 0) {
      appendEmptyArray();
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
    } else if (value.length == 0) {
      appendEmptyArray();
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
    } else if (value.length == 0) {
      appendEmptyArray();
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
  }

  protected void endObject() {
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
    if (fieldCount > 0) {
      removeLastChars(fieldDelimiter.length());
    }
  }

  protected void appendNull() {
    sb.append("<null>");
  }

  protected void appendEmptyArray() {
    sb.append("<empty array>");
  }

  protected void appendEmptyCollection() {
    sb.append("<empty collection>");
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

  protected String indent = "";

  protected String incIndent() {
    return indent;
  }

  protected void newLine() {
    sb.append("\n")
      .append(indent);
  }

  protected int position = 0;

  protected void mark() {
    position = sb.length();
  }

  protected void removeToMark() {
    sb.delete(position, sb.length());
  }

}
