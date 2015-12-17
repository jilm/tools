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

package cz.lidinsky.tools;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *  String buffer which place a special marks between the characters. These
 *  marks are then used to format the whole text.
 */
class StrBuffer {

  private StringBuilder sb;

  /**
   *  Creates new empty buffer.
   */
  StrBuffer() {
    sb = new StringBuilder();
  }

  //--------------------------------------------------------- Public Interface.

  public StrBuffer append(char code, String value) {
    appendCode(code, value.length());
    sb.append(value);
    return this;
  }

  public StrBuffer append(char code, double value) {
    int mark = sb.length();
    appendCode(code, 0);
    sb.append(value);
    setCode(mark, code, sb.length() - mark - 3);
    return this;
  }

  public StrBuffer append(char code, float value) {
    int mark = sb.length();
    appendCode(code, 0);
    sb.append(value);
    setCode(mark, code, sb.length() - mark - 3);
    return this;
  }

  public StrBuffer append(char code, int value) {
    int mark = sb.length();
    appendCode(code, 0);
    sb.append(value);
    setCode(mark, code, sb.length() - mark - 3);
    return this;
  }

  public StrBuffer append(char code, long value) {
    int mark = sb.length();
    appendCode(code, 0);
    sb.append(value);
    setCode(mark, code, sb.length() - mark - 3);
    return this;
  }

  public StrBuffer append(char code, Object value) {
    int mark = sb.length();
    appendCode(code, 0);
    sb.append(value);
    setCode(mark, code, sb.length() - mark - 3);
    return this;
  }

  @Override
  public String toString() {
    cleen(0);
    return sb.toString();
  }

  //------------------------------------------------------------------- Cursor.

  private int firstIndex = 0;

  private int cursor = 0;

  private Deque<Integer> cursorStack = new ArrayDeque<Integer>();

  /**
   *  Place the cursor in the internal stack. The <code>pop</code> method may
   *  be used to restore the cursor position.
   */
  public StrBuffer push() {
    cursorStack.push(cursor);
    return this;
  }

  /**
   *  Restore the cursor from the internal stack.
   */
  public StrBuffer pop() {
    cursor = cursorStack.pop();
    return this;
  }

  /**
   *  Moves the internal cursor to the first element in the buffer.
   */
  public StrBuffer reset() {
    cursor = firstIndex;
    return this;
  }

  /**
   *  Moves the internal cursor to the first element in the buffer with the
   *  given code. It is equivalent to: <code>reset().next(code);</code>. If
   *  there is not such an item in the buffer, the cursor points at the end of
   *  the buffer.
   */
  public StrBuffer first(char code) {
    cursor = firstIndex;
    while (cursor < sb.length()) {
      if (getCode(cursor) == code) return this;
      cursor = getNext(cursor);
    }
    return this;
  }

  /**
   *  Moves the internal cursor at the end of the buffer.
   */
  public StrBuffer last() {
    cursor = sb.length();
    return this;
  }

  /**
   *  Moves the internal cursor to the next element.
   *
   *  @throws CommonException
   *             if the cursor is already at the end of the buffer
   */
  public StrBuffer next() {
    cursor = getNext(validate(cursor));
    return this;
  }

  public StrBuffer next(char code) {
    cursor = getNext(validate(cursor));
    while (cursor < sb.length()) {
      if (getCode(cursor) == code) return this;
      cursor = getNext(cursor);
    }
    return this;
  }

  public StrBuffer setCode(char code) {
    setCode(validate(cursor), code);
    return this;
  }

  public boolean hasNext() {
    return cursor < sb.length() && getNext(cursor) < sb.length();
  }

  /**
   *  Returns true if the internal cursor points behind the buffer.
   */
  public boolean isAtTheEnd() {
    return cursor >= sb.length();
  }

  public char getCode() {
    return getCode(validate(cursor));
  }

  public int getLength() {
    return decode(validate(cursor));
  }

  public char getNextCode() {
    int index = getNext(validate(cursor));
    return getCode(validate(index));
  }

  public int getNextLength() {
    int index = getNext(validate(cursor));
    return decode(validate(index));
  }

  public StrBuffer join() {
    join(cursor);
    return this;
  }

  public StrBuffer join(String delimiter) {
    int next = getNext(cursor);
    int length = decode(cursor) + decode(next) + delimiter.length();
    cleen(next);
    sb.insert(next, delimiter);
    setCode(cursor, getCode(cursor), length);
    return this;
  }

  public StrBuffer insertBefore(String value) {
    int length = decode(cursor);
    sb.insert(cursor + 3, value);
    setCode(cursor, getCode(cursor), length + value.length());
    return this;
  }

  public StrBuffer insertBehind(String value) {
    int length = decode(cursor);
    sb.insert(cursor + 3 + length, value);
    setCode(cursor, getCode(cursor), length + value.length());
    return this;
  }

  public StrBuffer joinAll() {
    int index = getNext(cursor);
    int length = decode(cursor);
    while (index < sb.length()) {
      length += decode(index);
      index = cleenAndGetNext(index);
    }
    setCode(cursor, getCode(cursor), length);
    return this;
  }

  public StrBuffer joinAll(char code, char nextCode, char resultCode) {
    int index = cursor;
    while (getNext(index) < sb.length()) {
      if (getCode(index) == code && getCode(getNext(index)) == nextCode) {
        join(index);
        setCode(index, resultCode);
      } else {
        index = getNext(index);
      }
    }
    return this;
  }

  public StrBuffer replaceAll(char code, char replacement) {
    int index = cursor;
    while (index < sb.length()) {
      if (getCode(index) == code) {
        setCode(index, replacement);
      }
      index = getNext(index);
    }
    return this;
  }

  //--------------------------------------- Internal Mark Manipulation Methods.

  /**
   *  Insert a coded number into the string builder.
   *
   *  @param index
   *             where in the string builder the code should be inserted
   *
   *  @param code
   *             user defined char which servers as a control code or hint for
   *             next processing
   *
   *  @param length
   *             length of the string that follows
   */
  protected void insertCode(int index, char code, int length) {
    validate(index);
    sb.insert(index, code);
    sb.insert(index + 1, (char)('0' + (int)(length / 70)));
    sb.insert(index + 2, (char)('0' + (int)(length % 70)));
  }

  protected void appendCode(char code, int length) {
    sb.append(code);
    sb.append((char)('0' + (int)(length / 70)));
    sb.append((char)('0' + (int)(length % 70)));
  }

  protected void setCode(int index, char code, int length) {
    validate(index);
    sb.setCharAt(index, code);
    sb.setCharAt(index + 1, (char)('0' + (int)(length / 70)));
    sb.setCharAt(index + 2, (char)('0' + (int)(length % 70)));
  }

  /**
   *  Returns decoded length.
   */
  protected int decode(int index) {
    validate(index);
    return (int)(sb.charAt(index + 1) - '0') * 70
      + (int)(sb.charAt(index + 2) - '0');
  }

  /**
   *  Returns index of the next control code. If the given index is already at
   *  the end of the buffer, nothing happens. In such a case it returns the
   *  length of the buffer.
   */
  protected int getNext(int index) {
    if (index >= sb.length()) return sb.length();
    int length = decode(index);
    return index + length + 3;
  }

  /**
   *  Removes control codes at the given index and returns index of the next
   *  control code. If the given index is already at the end of the buffer,
   *  nothing happens.
   */
  protected int cleenAndGetNext(int index) {
    if (index >= sb.length()) return sb.length();
    int length = decode(index);
    sb.delete(index, index + 3);
    return index + length;
  }

  /**
   *  Removes control codes at the given position. If the index is already at
   *  the end of the buffer, nothing happens.
   */
  protected void cleen(int index) {
    if (index >= sb.length()) return;
    sb.delete(index, index + 3);
  }

  /**
   *  Returns control code that is at the given index.
   *
   *  @throws CommonException
   *             if the index is out of bounds
   */
  protected char getCode(int index) {
    return sb.charAt(validate(index));
  }

  protected void setCode(int index, char code) {
    sb.setCharAt(validate(index), code);
  }

  /**
   *  Joins two consecutive items into one.
   */
  protected void join(int index) {
    if (index < sb.length()) {
      int next = getNext(index);
      if (next < sb.length()) {
        int length = decode(index) + decode(next);
        char code = getCode(index);
        setCode(index, code, length);
        cleen(next);
      }
    }
  }

  protected int validate(int index) {
    if (index >= sb.length() || index < 0) {
      throw new CommonException()
        .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS);
    } else {
      return index;
    }
  }

}
