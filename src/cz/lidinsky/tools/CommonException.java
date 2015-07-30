/*
 *  Copyright 2013, 2014 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.lidinsky.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;

public class CommonException extends RuntimeException {

  public CommonException() {
    super();
  }

  /** Exception code. */
  private ExceptionCode code = ExceptionCode.NOT_SPECIFIED;

  /**
   *  Sets the exception code.
   */
  public CommonException setCode(ExceptionCode code) {
    this.code = code;
    return this;
  }

  /**
   *  Returns the exception code. It is either the code which was set
   *  by the {@link #setCode} method, or the code which is infered
   *  from the cause exception.
   */
  public ExceptionCode getCode() {
    if (code == ExceptionCode.NOT_SPECIFIED && cause != null) {
      if (cause instanceof CommonException) {
        return ((CommonException)cause).getCode();
      } else {
        for (ExceptionCode ec : ExceptionCode.values()) {
          if (ec.getCounterpart() == cause.getClass()) {
            return ec;
          }
        }
      }
    }
    return code;
  }

  /** Cause of the exception. */
  private Throwable cause;

  /**
   *  Sets the cause of the exception.
   */
  public CommonException setCause(Throwable cause) {
    this.cause = cause;
    while (this.cause instanceof InvocationTargetException) {
      this.cause = ((InvocationTargetException)this.cause).getTargetException();
    }
    return this;
  }

  /** Additional information. */
  protected HashMap<String, String> fields = new HashMap<String, String>();

  /**
   *  Allows to attache additional information, mainly conditions that
   *  could be important to find out what is going on.
   */
  public CommonException set(String key, String value) {
    if (key != null) {
      if (value != null) {
        fields.put(key, value);
      } else {
        fields.put(key, "<null>");
      }
    }
    return this;
  }

  public CommonException set(String key, int value) {
    if (key != null) {
      fields.put(key, Integer.valueOf(value).toString());
    }
    return this;
  }

  public CommonException set(String key, Object value) {
    if (key != null) {
      if (value != null) {
        fields.put(key, value.toString());
      } else {
        fields.put(key, "<null>");
      }
    }
    return this;
  }

  public String getMessage() {
    // header
    StringBuilder sb = new StringBuilder()
      .append("EXCEPTION ! Exception Code: ")
      .append(getCode());
    // additional information
    Set<Map.Entry<String, String>> entries = fields.entrySet();
    for (Map.Entry<String, String> entry : entries) {
      sb.append("\n")
        .append(entry.getKey())
        .append(", ")
        .append(entry.getValue());
    }
    // stack information
    if (getStackTrace().length > 0) {
      sb.append("\nClass Name: ")
        .append(getStackTrace()[0].getClassName())
        .append("\nMethod Name: ")
        .append(getStackTrace()[0].getMethodName());
    }
    // cause
    if (cause != null) {
      sb.append("\nCause: ")
        .append(cause.getClass().getName())
        .append("\n")
        .append(cause.getMessage());
    }
    return sb.toString();
  }

}
