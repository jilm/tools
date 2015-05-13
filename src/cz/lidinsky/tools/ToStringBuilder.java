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

  private DefaultToStringStyle style;

  protected ArrayDeque<DefaultToStringStyle> styleStack
      = new ArrayDeque<DefaultToStringStyle>();

  public ToStringBuilder() {
    this.style = new DefaultToStringStyle(sb);
  }

  public ToStringBuilder(ToStringStyle style) {
    this();
  }

  protected StringBuilder sb = new StringBuilder();

  public ToStringBuilder append(String fieldName, IToStringBuildable object) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      appendValue(object);
      style.appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object object) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      appendValue(object);
      style.appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, int value) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      style.appendValue(value);
      style.appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, long value) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      style.appendValue(value);
      style.appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, boolean value) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      style.appendValue(value);
      style.appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, float value) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      style.appendValue(value);
      style.appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, double value) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      style.appendValue(value);
      style.appendFieldDelimiter();
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

  public ToStringBuilder append(int value) {
    style.appendValue(value);
    return this;
  }

  public ToStringBuilder append(long value) {
    style.appendValue(value);
    return this;
  }

  public ToStringBuilder append(boolean value) {
    style.appendValue(value);
    return this;
  }

  public ToStringBuilder append(float value) {
    style.appendValue(value);
    return this;
  }

  public ToStringBuilder append(double value) {
    style.appendValue(value);
    return this;
  }

  public ToStringBuilder append(String fieldName, IToStringBuildable[] value) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      appendValue(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object[] value) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      appendValue(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, int[] value) {
    style.append(fieldName, value);
    return this;
  }

  public ToStringBuilder append(String fieldName, long[] value) {
    style.append(fieldName, value);
    return this;
  }

  public ToStringBuilder append(String fieldName, boolean[] value) {
    style.append(fieldName, value);
    return this;
  }

  public ToStringBuilder append(String fieldName, float[] value) {
    style.append(fieldName, value);
    return this;
  }

  public ToStringBuilder append(String fieldName, double[] value) {
    style.append(fieldName, value);
    return this;
  }

  public ToStringBuilder append(int[] value) {
    style.append(value);
    return this;
  }

  public ToStringBuilder append(long[] value) {
    style.append(value);
    return this;
  }

  public ToStringBuilder append(boolean[] value) {
    style.append(value);
    return this;
  }

  public ToStringBuilder append(float[] value) {
    style.append(value);
    return this;
  }

  public ToStringBuilder append(double[] value) {
    style.append(value);
    return this;
  }

  protected void appendValue(IToStringBuildable object) {
    if (object == null) {
      style.appendNull();
    } else {
      style.appendClassName(object.getClass());
      style.appendHashCode(object);
      style.startObject();
      object.toString(this);
      style.endObject();
    }
  }

  protected void appendValue(IToStringBuildable[] value) {
    if (value == null) {
      style.appendNull();
    } else {
      style.appendClassName(value.getClass());
      style.appendHashCode(value);
      style.startArray();
      for (int i = 0; i < style.getArraySize(value.length); i++) {
        appendValue(value[i]);
        style.appendArrayDelimiter();
      }
      style.endArray();
    }
  }

  protected void appendValue(Object[] value) {
    if (value == null) {
      style.appendNull();
    } else {
      style.appendClassName(value.getClass());
      style.appendHashCode(value);
      style.startArray();
      for (int i = 0; i < style.getArraySize(value.length); i++) {
        appendValue(value[i]);
        style.appendArrayDelimiter();
      }
      style.endArray();
    }
  }

  protected void appendValue(Object object) {
    if (object == null) {
      style.appendNull();
    } else {

      if (object instanceof IToStringBuildable) {
        appendValue((IToStringBuildable)object);
      } else if (object instanceof Integer) {
        style.appendValue(((Integer)object).intValue());
      } else {
        style.appendValue(object);
      }
    }
  }

  @Override
  public String toString() {
    return sb.toString();
  }

}
