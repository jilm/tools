/*
 *  Copyright 2013, 2014, 2016 Jiri Lidinsky
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

import cz.lidinsky.tools.text.Formatter;
import cz.lidinsky.tools.text.StrBuffer;
import cz.lidinsky.tools.text.StrIterator;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An exception which contains a map which allows to attach additional
 * information.
 */
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
    if (code == ExceptionCode.NOT_SPECIFIED) {
      return getCode(cause);
    } else {
      return code;
    }
  }

  public static ExceptionCode getCode(Throwable throwable) {
    if (throwable == null) {
      return ExceptionCode.NOT_SPECIFIED;
    } else if (throwable instanceof CommonException) {
      return ((CommonException)throwable).getCode();
    } else {
      for (ExceptionCode ec : ExceptionCode.values()) {
        if (ec.getCounterpart() == throwable.getClass()) {
          return ec;
        }
      }
      return ExceptionCode.NOT_SPECIFIED;
    }
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

  @Override
  public Throwable getCause() {
    return cause;
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

  public String get(String key) {
    if (key != null) {
      return fields.get(key);
    } else {
      return null;
    }
  }

  @Override
  public String getMessage() {
    return fields.get("message");
  }

  @Override
  public String toString() {
    return new cz.lidinsky.tools.text.ExceptionStatementBuilder().toString(this);
    //StrBuffer buffer = new StrBuffer();
    //toString(buffer);
    //return new Formatter().format(new StrIterator(buffer.toString()));
  }

  public void toString(StrBuffer sb) {
    //StrBuffer sb = new StrBuffer();
    List<Throwable> chain = getExceptionChain(this);
    Collections.reverse(chain);
    sb.appendHead("A detailed statement of the exception.");
    // list of all of the exception classes in the chain
    sb.appendSubHead("Exception chain")
            .startOrderedList();
    chain.stream()
            .forEach(
              e -> sb.startItem()
                     .append(getCode(e).name())
                     .appendInBrackets(e.getClass().getSimpleName()));
    sb.closeList();
    // print all of the messages
    sb.appendHead("Messages")
            .startOrderedList();
    chain.stream()
            .forEach(
              e -> sb.startItem()
                     .append(e.getMessage()));
    sb.closeList();
    // print details
    Map<String, String> more = getMore(chain);
    sb.appendHead("Details")
      .startUnorderedList();
    more.entrySet().stream()
      .forEach(entry -> sb.startItem(entry.getKey())
          .append(entry.getValue()));
    sb.closeList();
    // print stack info
    sb.appendHead("Stack info")
      .startOrderedList();
    chain.stream()
      .filter(e -> e.getStackTrace().length > 0)
      .map(e -> e.getStackTrace()[0])
      .forEach(trace -> sb.startItem()
          .append(trace.getClassName() + "#" + trace.getMethodName()));
    sb.closeList();
    // return result
    //return sb.toString();
  }

  public String toStringOld() {
    // header
    StringBuilder sb = new StringBuilder()
      .append("An EXCEPTION was thrown! Detailed statement follows.\n")
      .append("----------------------------------------------------\n")
      .append("a) Exception chain\n");
    printExceptionChain(sb, this);
    sb.append("b) Message.\n");
    printMessage(sb, this);
    sb.append("c) Detail info.\n");
    printMore(sb, this);
    sb.append("d) Stack info.\n");
    printStackInfo(sb, this);
    sb.append("----------------------------------------------------\n")
      .append("This is the end of the exception statement.\n");
    return sb.toString();
  }

  private int printMessage(StringBuilder sb, Throwable e) {
    if (e == null) return 1;
    int index = printMessage(sb, e.getCause());
    String message = e.getMessage();
    sb.append("    ")
      .append(index)
      .append(". ")
      .append(message)
      .append("\n");
    return index + 1;
  }

  private static List<Throwable> getExceptionChain(Throwable e) {
    List<Throwable> result = new ArrayList<>();
    while (e != null) {
      result.add(e);
      e = e.getCause();
    }
    return result;
  }

  private int printExceptionChain(StringBuilder sb, Throwable e) {
    if (e == null) return 1;
    Throwable cause = e.getCause();
    int index = printExceptionChain(sb, cause);
    ExceptionCode code = getCode(e);
    sb.append("    ")
      .append(index)
      .append(". ")
      .append(code)
      .append(" (")
      .append(e.getClass().getSimpleName())
      .append(")\n");
    return index + 1;
  }

  private void printMore(StringBuilder sb, Throwable e) {
    if (e == null) return;
    java.util.SortedSet<String> data = new java.util.TreeSet<String>();
    Throwable cause = e;
    while (cause != null) {
      if (cause instanceof CommonException) {
        for (Map.Entry<String, String> entry
            : ((CommonException)cause).fields.entrySet()) {
          data.add(entry.getKey() + ": " + entry.getValue());
        }
      }
      cause = cause.getCause();
    }
    for (String item : data) {
      sb.append("    ")
        .append(item)
        .append("\n");
    }

  }

  private Map<String, String> getMore(List<Throwable> chain) {
    Map<String, String> result = new HashMap<>();
    chain.stream()
         .filter(e -> e instanceof CommonException)
         .map(e -> (CommonException)e)
         .forEach(e -> result.putAll(e.fields));
    return result;
  }

  private int printStackInfo(StringBuilder sb, Throwable e) {
    if (e == null) return 1;
    int index = printStackInfo(sb, e.getCause());
    if (e.getStackTrace().length > 0) {
      sb.append("    ")
        .append(index)
        .append(". ")
        .append(e.getStackTrace()[0].getClassName())
        .append("#")
        .append(e.getStackTrace()[0].getMethodName())
        .append("\n");
    }
    return index + 1;
  }

  public static void main(String[] args) {
    CommonException e = new CommonException()
      .setCode(ExceptionCode.NULL_POINTER)
      .set("message", "This is a message of the exception!")
      .set("key1", "more specific information 1")
      .set("key2", "more specific information nr. 2.")
      .set("key3", "the last row of some specific information. This time, the row is longer than the number of characters per one screen line!");

    System.out.println(e.toString());
  }

}
