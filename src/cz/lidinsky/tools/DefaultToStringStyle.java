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

  public void append(StringBuilder sb, String fieldName, String value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, String[] value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String[] value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, int value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, int[] value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, int value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, int[] value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, long value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, long[] value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, long value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, long[] value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, boolean value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, boolean[] value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, boolean value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, boolean[] value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, float value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, float[] value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, float value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, float[] value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, double value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, String fieldName, double[] value) {
    appendFieldName(sb, fieldName);
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, double value) {
    appendValue(sb, value);
  }

  public void append(StringBuilder sb, double[] value) {
    appendValue(sb, value);
  }

  //-------------------------

  protected void appendValue(StringBuilder sb, String value) {
    if (value == null) {
      appendNull(sb);
    } else {
      sb.append(value);
    }
  }

  protected void appendValue(StringBuilder sb, int value) {
    sb.append(value);
  }

  protected void appendValue(StringBuilder sb, long value) {
    sb.append(value);
  }

  protected void appendValue(StringBuilder sb, boolean value) {
    sb.append(value);
  }

  protected void appendValue(StringBuilder sb, float value) {
    sb.append(value);
  }

  protected void appendValue(StringBuilder sb, double value) {
    sb.append(value);
  }

  protected void appendValue(StringBuilder sb, Object value) {
    if (value == null) {
      appendNull(sb);
    } else {
      sb.append(value);
    }
  }

  protected void appendValue(StringBuilder sb, String[] value) {
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

  protected void appendValue(StringBuilder sb, int[] value) {
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

  protected void appendValue(StringBuilder sb, long[] value) {
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

  protected void appendValue(StringBuilder sb, boolean[] value) {
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

  protected void appendValue(StringBuilder sb, float[] value) {
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

  protected void appendValue(StringBuilder sb, double[] value) {
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

  protected void appendFieldName(StringBuilder sb, String fieldName) {
    if (fieldName != null) {
      sb.append(fieldName);
      appendFieldValueDelimiter(sb);
    }
  }

  protected void appendHashCode(StringBuilder sb, Object object) {
    sb.append('@');
    sb.append(Integer.toHexString(object.hashCode()));
  }

  protected boolean simpleClassName = true;

  protected void appendClassName(StringBuilder sb, Class _class) {
    if (simpleClassName) {
      sb.append(_class.getSimpleName());
    } else {
      sb.append(_class.getName());
    }
  }

  protected void startObject(StringBuilder sb) {
    sb.append('[');
  }

  protected void endObject(StringBuilder sb, int fieldCount) {
    if (fieldCount > 0) {
      removeFieldDelimiter(sb);
    }
    sb.append(']');
  }

  protected void removeLastChars(StringBuilder sb, int count) {
    sb.delete(sb.length() - count, sb.length());
  }

  protected void appendFieldValueDelimiter(StringBuilder sb) {
    sb.append('=');
  }

  protected String fieldDelimiter = ",";

  protected void appendFieldDelimiter(StringBuilder sb) {
    sb.append(fieldDelimiter);
  }

  protected void removeFieldDelimiter(StringBuilder sb) {
    removeLastChars(sb, fieldDelimiter.length());
  }

  protected void appendNull(StringBuilder sb) {
    sb.append("<null>");
  }

  protected void startArray(StringBuilder sb) {
    sb.append('{');
  }

  protected void endArray(StringBuilder sb, int arrayLength) {
    if (arrayLength > arraySize) {
      sb.append("... ");
      sb.append("<total size: ");
      sb.append(arrayLength);
      sb.append(">");
      sb.append('}');
    } else {
      removeArrayDelimiter(sb);
      sb.append('}');
    }
  }

  protected void removeArrayDelimiter(StringBuilder sb) {
    removeLastChars(sb, arrayDelimiter.length());
  }

  protected String arrayDelimiter = ",";

  protected void appendArrayDelimiter(StringBuilder sb) {
    sb.append(arrayDelimiter);
  }

  protected static int arraySize = 10;

  protected int getArraySize(int arrayLength) {
    return Math.min(arraySize, arrayLength);
  }

}
