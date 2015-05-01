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

public class ToStringBuilder {

  private DefaultToStringStyle style;

  public ToStringBuilder(DefaultToStringStyle style) {
    this.style = style;
  }

  protected StringBuffer sb = new StringBuffer();

  public ToStringBuilder append(String fieldName, IToStringBuildable object) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      appendValue(object);
      style.appendFieldDelimiter(sb);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object object) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      appendValue(object);
      style.appendFieldDelimiter(sb);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, int value) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      style.appendValue(sb, value);
      style.appendFieldDelimiter(sb);
    }
  }

  public ToStringBuilder append(String fieldName, long value) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      style.appendValue(sb, value);
      style.appendFieldDelimiter(sb);
    }
  }

  public ToStringBuilder append(String fieldName, boolean value) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      style.appendValue(sb, value);
      style.appendFieldDelimiter(sb);
    }
  }

  public ToStringBuilder append(String fieldName, float value) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      style.appendValue(sb, value);
      style.appendFieldDelimiter(sb);
    }
  }

  public ToStringBuilder append(String fieldName, double value) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      style.appendValue(sb, value);
      style.appendFieldDelimiter(sb);
    }
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
    style.appendValue(sb, value);
  }

  public ToStringBuilder append(String fieldName, IToStringBuildable[] value) {
    if (fieldName != null) {
      style.appendFieldName(sb, fieldName);
      appendValue(value);
    }
    return this;
  }

  protected void appendValue(IToStringBuildable object) {
    if (object == null) {
      style.appendNull(sb);
    } else {
      style.appendClassName(sb, object.getClass());
      style.appendHashCode(sb, object);
      style.startObject(sb);
      object.toString(this);
      style.endObject(sb);
    }
  }

  protected void appendValue(IToStringBuildable[] value) {
    if (value == null) {
      style.appendNull(sb);
    } else {
      style.appendClassName(sb, value.getClass());
      style.appendHashCode(sb, value);
      style.startArray(sb);
      for (int i = 0; i < style.getArraySize(value.length); i++) {
        appendValue(value[i]);
        style.appendArrayDelimiter(sb);
      }
      style.endArray(sb, value.length);
    }
  }

  protected void appendValue(Object[] value) {
    if (value == null) {
      style.appendNull(sb);
    } else {
      style.appendClassName(sb, value.getClass());
      style.appendHashCode(sb, value);
      style.startArray(sb);
      for (int i = 0; i < style.getArraySize(value.length); i++) {
        appendValue(value[i]);
        style.appendArrayDelimiter(sb);
      }
      style.endArray(sb, value.length);
    }
  }

  protected void appendValue(Object object) {
    if (object == null) {
      style.appendNull(sb);
    } else {

      if (object instanceof IToStringBuildable) {
        appendValue((IToStringBuildable)object);
      } else if (object instanceof Integer) {
        style.appendValue(sb, ((Integer)object).intValue());
      } else {
        style.appendValue(object);
      }
    }
  }

}
