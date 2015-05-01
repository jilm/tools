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

  private ToStringStyle style;

  public ToStringBuilder(ToStringStyle style) {
    this.style = style;
  }

  /**
   *  Writes the given object in the following form:
   *
   *  <pre>
   *  <indent><fieldName>r'='<className>'@'<hashCode>'['
   *  <indent+><objectContent>
   *  ']'
   *  </pre>
   */
  public ToStringBuilder append(String fieldName, IToStringBuildable object) {
    if (fieldName == null && object == null) {
      return this;
    } else if (fieldName == null) {
      return append(object);
    } else if (object == null) {
      appendFieldName(fieldName);
      style.appendNull()
	   .appendFieldsDelimiter();
    } else {
      appendFieldName(fieldName);
      style.appendClassName(object)
	   .appendHash(object)
	   .startObject();
      object.toString(this);
      style.endObject()
	   .appendFieldsDelimiter();
    }
    return this;
  }

  /**
   *  Writes the given object in the following form:
   *
   *  <pre>
   *  <indent><fieldName>'='<className>'@'<hashCode>'['
   *  <indent+><objectContent>
   *  ']'
   *  </pre>
   */
  public ToStringBuilder append(String fieldName, Object object) {
    if (fieldName == null && object == null) {
      return this;
    } else if (fieldName == null) {
      return append(object);
    } else if (object == null) {
      appendFieldName(fieldName);
      style.appendNull()
	   .appendFieldsDelimiter();
    } else {
      appendFieldName(fieldName);
      append(object);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object[] array) {
    if (fieldName == null && array == null) {
      return this;
    } else if (fieldName == null) {
      return append(array);
    } else if (array == null) {
      appendFieldName(fieldName);
      style.appendNull();
      style.appendFieldsDelimiter();
    } else {
      appendFieldName(fieldName);
      style.appendClassName(array)
	   .appendHash(array)
           .startArray();
      for (Object object : array) {
	append(object);
	style.appendArrayDelimiter();
      }
      style.endArray();
      style.appendFieldsDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Iterable field) {

    if (fieldName == null && field == null) {
      return this;
    } else if (fieldName == null) {
      append(field);
    } else if (field == null) {
      appendFieldName(fieldName);
      style.appendNull();
      style.appendFieldsDelimiter();
    } else {
      appendFieldName(fieldName);
      append(field);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, int[] array) {
    appendFieldName(fieldName);
    if (array == null) {
      style.appendNull();
    } else {
      style.startArray();
      for (int i : array) {
	style.append(i);
	style.appendArrayDelimiter();
      }
      style.endArray();
    }
    style.appendFieldsDelimiter();
    return this;
  }

  public ToStringBuilder append(String fieldName, Map field) {
    if (fieldName == null && field == null) {
      return this;
    } else if (fieldName == null) {
      append(field);
    } else if (field == null) {
      appendNullField(fieldName);
    } else {
      appendFieldName(fieldName);
      append(field);
    }
    return this;
  }

  public ToStringBuilder append(int[] array) {
    if (array == null) {
      style.appendNull();
    } else {
      style.startArray();
      for (int i : array) {
	style.append(i);
	style.appendArrayDelimiter();
      }
      style.endArray();
    }
    style.appendFieldsDelimiter();
    return this;
  }

  public ToStringBuilder append(Iterable field) {
    if (field == null) {
      style.appendNull();
    } else {
      style.startArray();
      for (Object element : field) {
	append(element);
	style.appendArrayDelimiter();
      }
      style.endArray();
    }
    style.appendFieldsDelimiter();
    return this;
  }

  protected void appendFieldName(String fieldName) {
    if (fieldName != null) {
      style.appendFieldName(fieldName);
      style.appendFieldValueDelimiter();
    }
  }

  protected void appendNullField(String fieldName) {
    if (fieldName != null) {
      appendFieldName(fieldName);
      style.appendNull();
    }
  }

  public ToStringBuilder append(IToStringBuildable object) {
    if (object != null) {
      style.appendClassName(object)
	   .appendHash(object)
	   .startObject();
      object.toString(this);
      style.endObject();
      style.appendFieldsDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String text) {
    if (text != null) {
      style.appendText(text);
      style.appendFieldsDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(Object[] array) {
    if (array != null) {
      style.startArray();
      for (Object object : array) {
	append(object);
	style.appendArrayDelimiter();
      }
      style.endArray();
      style.appendFieldsDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(Map field) {
    if (field != null) {
      style.startArray();
      Set keys = field.keySet();
      for (Object key : keys) {
	append(key);
	style.appendFieldsDelimiter();
	append(field.get(key));
	style.appendArrayDelimiter();
      }
      style.endArray();
      style.appendFieldsDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(Object object) {
    if (object == null) {
      style.appendNull();
    } else if (object instanceof IToStringBuildable) {
      append((IToStringBuildable)object);
    } else if (object instanceof Iterable) {
      append((Iterable)object);
    } else if (object instanceof Map) {
      append((Map)object);
    } else if (object instanceof int[]) {
      append((int[])object);
    } else {
      IToStringBuildable decorator = ToStringDecoratorFactory.decorate(object);
      if (decorator == null) {
        style.appendObject(object);
      } else {
	append(decorator);
      }
      style.appendFieldsDelimiter();
    }
    return this;
  }

  public String toString() {
    return style.toString();
  }

}
