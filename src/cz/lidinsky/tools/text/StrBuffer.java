/*
 *  Copyright 2015, 2016 Jiri Lidinsky
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

package cz.lidinsky.tools.text;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

/**
 *  String buffer which place a special marks between the characters. These
 *  marks are then used to format the whole text.
 */
public class StrBuffer {

  private final StringBuilder sb;

  /**
   *  Creates new empty buffer.
   */
  public StrBuffer() {
    sb = new StringBuilder();
    headLevel = 0;
  }

  //--------------------------------------------------------- Public Interface.

  private int headLevel = 0;

  /**
   * Appends a head of the actual head level.
   */
  public StrBuffer appendHead(String head) {
    return appendHead(headLevel, head);
  }

  public StrBuffer appendSubHead(String head) {
    return appendHead(headLevel + 1, head);
  }

  public StrBuffer appendUpperHead(String head) {
    return appendHead(headLevel - 1, head);
  }

  /**
   * Appends given head together with the given head level.
   *
   * @param level
   *            head level. Heads may be arranged hierarchicaly where smaller
   *            number denotes head that is hierarchically higher. Valid values
   *            are between 0 and 4. Level parameter is validated and if less
   *            than 0, zero is used, if higher than 4, 4 is used.
   *
   * @param head
   *            text of the head. Should not be too long. If null, empty string
   *            is used instead.
   */
  public StrBuffer appendHead(int level, String head) {
    level = level < 0 ? 0 : level;
    level = level > 4 ? 4 : level;
    StrCode code = StrCode.HEAD4;
    switch (level) {
      case 0:
        code = StrCode.HEAD0;
        break;
      case 1:
        code = StrCode.HEAD1;
        break;
      case 2:
        code = StrCode.HEAD2;
        break;
      case 3:
        code = StrCode.HEAD3;
        break;
      case 4:
        code = StrCode.HEAD4;
        break;
    }
    append(code, head == null ? "" : head);
    headLevel = level;
    return this;
  }

  public int getHeadLevel() {
    return headLevel;
  }

  public StrBuffer append(String text) {
    if (text != null && text.length() > 0) {
      append(StrCode.TEXT, text);
    }
    return this;
  }

  public StrBuffer appendInBrackets(String text) {
    if (!isEmpty(text)) {
      append(StrCode.TEXT, "(" + text + ")");
    }
    return this;
  }

  public StrBuffer startParagraph() {
    append(StrCode.PARAGRAPH);
    return this;
  }

  public StrBuffer startParagraph(String text) {
    if (isEmpty(text)) {
      startParagraph();
    } else {
      append(StrCode.PARAGRAPH, text);
    }
    return this;
  }

  public StrBuffer startOrderedList() {
    append(StrCode.LIST_ORDERED);
    return this;
  }

  public StrBuffer startUnorderedList() {
    append(StrCode.LIST_UNORDERED);
    return this;
  }

  public StrBuffer startItem() {
    append(StrCode.ITEM);
    return this;
  }

  public StrBuffer startItem(String key) {
    if (isEmpty(key)) {
      startItem();
    } else {
      append(StrCode.ITEM, key);
    }
    return this;
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  //------------------------------------------------------------------- Cursor.

  private int cursor;

  /**
   *  Moves the internal cursor to the next element.
   *
   *  @throws CommonException
   *             if the cursor is already at the end of the buffer
   */
  public StrBuffer next() {
    cursor = next(cursor);
    return this;
  }

  public boolean hasNext() {
    if (cursor < sb.length()) {
      int nextIndex = next(cursor);
      return nextIndex < sb.length();
    } else {
      return false;
    }
  }

  public StrBuffer first() {
    cursor = 0;
    return this;
  }

  public StrCode getCode() {
    return getCode(cursor);
  }

  public String getText() {
    return getText(cursor);
  }

  //--------------------------------------- Internal Mark Manipulation Methods.

  protected void append(final StrCode code) {
    sb.append(code.getCode());
    sb.append(ZERO_LENGTH_MARK);
  }

  private static final int MAX_LENGTH = 70 * 70;
  private static final String ZERO_LENGTH_MARK = "00";

  protected void append(final StrCode code, String text) {
    int length = text.length();
    if (length > MAX_LENGTH) {
      append(code, text.substring(0, MAX_LENGTH));
      append(StrCode.EXTENDED, text.substring(MAX_LENGTH));
    } else {
      sb.append(code.getCode());
      sb.append((char)('0' + (int)(length / 70)));
      sb.append((char)('0' + (int)(length % 70)));
      sb.append(text);
    }
  }

  private boolean isEmpty(String text) {
    return text == null || text.length() == 0;
  }

  private StrCode getCode(int index) {
    validateIndex(index);
    return StrCode.getStrCode(sb.charAt(index));
  }

  private int getLength(int index) {
    validateIndex(index);
    return (int)(sb.charAt(index + 1) - '0') * 70
      + (int)(sb.charAt(index + 2) - '0');
  }

  private int next(int index) {
    validateIndex(index);
    return index + getLength(index) + 3;
  }

  private int validateIndex(int index) {
    if (index >= sb.length() || index < 0) {
      throw new CommonException()
              .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
              .set("index", index)
              .set("buffer size", sb.length());
    } else {
      return index;
    }
  }

  private String getText(int index) {
    validateIndex(index);
    int length = getLength(index);
    String text = sb.substring(index + 3, index + 3 + length);
    if (getCode(next(index)) == StrCode.EXTENDED) {
      text += getText(next(index));
    }
    return text;
  }

}
