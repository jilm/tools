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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseException extends RuntimeException {

  public BaseException() {
    super();
  }

  public BaseException setCause(Throwable cause) {
    if (cause instanceof BaseException) {
      fields.putAll(((BaseException)cause).fields);
    } else {
      set("Cause exception", cause.getClass().getName());
      set("Cause message", cause.getMessage());
    }
    return this;
  }

  protected HashMap<String, String> fields = new HashMap<String, String>();

  public BaseException set(String key, String value) {
    fields.put(key, value);
    return this;
  }

  public BaseException set(String key, int value) {
    fields.put(key, Integer.valueOf(value).toString());
    return this;
  }

  public BaseException set(String key, Object value) {
    fields.put(key, value.toString());
    return this;
  }

  public String getMessage() {
    StringBuilder sb = new StringBuilder()
      .append("EXCEPTION !");
    Set<Map.Entry<String, String>> entries = fields.entrySet();
    for (Map.Entry<String, String> entry : entries) {
      sb.append("\n")
        .append(entry.getKey())
        .append(", ")
        .append(entry.getValue());
    }
    return sb.toString();
  }

}
