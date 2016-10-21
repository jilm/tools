/*
 *  Copyright 2016 Jiri Lidinsky
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  String buffer which place a special marks between the characters. These
 *  marks are then used to format the whole text.
 */
public class StrIterator {

  private final String buffer;

  /**
   *  Creates new empty buffer.
   */
  public StrIterator(String buffer) {
    this.buffer = buffer;
  }

  //--------------------------------------------------------- Public Interface.

  /** Actual index into the buffer. */
  private int cursor;


  //------------------------------------------------------------ State machine.

  private StrCode[] codeBuffer;

  private int codeBufferIndex;
  private int codeBufferEnd;

  private StrCode[][] patterns = new StrCode[][] {
    {StrCode.HEAD0, StrCode.TEXT}
  };

  /**
   *
   * @return
   */
  public StrElement next() {
    Builder builder = null;
    while (hasNext(cursor)) {
      StrCode code = getCode(cursor);
      int length = getLength(cursor);
      String text = getText(cursor);
      if (builder != null) {
        if (builder.accept(code, text)) {
        } else {
          return builder;
        }
      } else {
        switch (code) {
          case APPENDIX:
            builder = new Head(code, text);
            break;
          case EMPHASIZE:
          case STRONG:
          case EXTENDED:
          case LITERAL:
            builder = new Paragraph();
            builder.accept(code, text);
            break;
          case HEAD0:
          case HEAD1:
          case HEAD2:
          case HEAD3:
          case HEAD4:
            builder = new Head(code, text);
            break;
          case HORIZONTAL_RULE:
          case NEW_LINE:
            builder = new Builder(code);
            return builder;
          case LIST_ORDERED:
          case LIST_UNORDERED:
            builder = new StrList(code);
            break;
          case ITEM:
          case TEXT:
          case PARAGRAPH:
            builder = new Paragraph();
            builder.accept(StrCode.TEXT, text);
            break;
          case TABLE:
          case END:
          case NESTED:
            throw new UnsupportedOperationException();
        }
      }
      cursor = next(cursor);
    }
    return builder;
  }

  class Builder implements StrElement {

    boolean accept(StrCode code, String text) {
      return false;
    }

    private final StrCode code;

    Builder(StrCode code) {
      this.code = code;
    }

    @Override
    public StrCode getCode() {
      return this.code;
    }

    @Override
    public List<StrElement> getChildren() {
      return Collections.emptyList();
    }

    @Override
    public String getText() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
      return !getChildren().stream()
              .anyMatch(child -> !child.isEmpty());
    }
  }

  class Head extends Builder {

    private String head;

    Head(StrCode code, String text) {
      super(code);
      this.head = text;
    }

    @Override
    boolean accept(StrCode code, String text) {
      switch (code) {
        case EXTENDED:
          head += text;
          return true;
        default:
          return false;
      }
    }

    @Override
    public String getText() {
      return head;
    }

    @Override
    public boolean isEmpty() {
      return StrUtils.isBlank(head);
    }
  }

  class Inline extends Builder {

    private String text;

    Inline(StrCode code, String text) {
      super(code);
      this.text = text;
    }

    @Override
    boolean accept(StrCode code, String text) {
      switch (code) {
        case EXTENDED:
          this.text += text;
          return true;
        default:
          if (code == getCode()) {
            this.text += text;
            return true;
          } else {
            return false;
          }
      }
    }

    @Override
    public String getText() {
      return text;
    }

    @Override
    public boolean isEmpty() {
      return StrUtils.isEmpty(text);
    }
  }

  class Paragraph extends Builder {

    private final List<StrElement> inlines;

    Paragraph() {
      super(StrCode.PARAGRAPH);
      inlines = new ArrayList<>();
      inlines.add(new Inline(StrCode.TEXT, ""));
    }

    @Override
    boolean accept(StrCode code, String text) {
      //if (!((Builder)(inlines.get(inlines.size() - 1))).accept(code, text)) {
        switch (code) {
          case TEXT:
          case STRONG:
          case LITERAL:
          case EMPHASIZE:
            inlines.add(new Inline(code, text));
            return true;
          case NEW_LINE:
            inlines.add(new Builder(code));
            return true;
          default:
            return false;
        }
      //} else {
      //  return true;
      //}
    }

    @Override
    public List<StrElement> getChildren() {
      return (List<StrElement>) inlines;
    }

  }

  /**
   * An item of the list. It may contain paragraphs and nested lists.
   */
  class Item extends Builder {

    private final List<StrElement> children;

    public Item() {
      super(StrCode.ITEM);
      children = new ArrayList<>();
      children.add(new Paragraph());
    }

    @Override
    boolean accept(StrCode code, String text) {
      if (((Builder)children.get(children.size() - 1)).accept(code, text)) {
        return true;
      } else {
        switch (code) {
          case PARAGRAPH:
            children.add(new Paragraph());
            return true;
          default:
            return false;
        }
      }
    }

    @Override
    public List<StrElement> getChildren() {
      return children;
    }

  }


  class StrList extends Builder {

    private List<StrElement> items;

    StrList(StrCode code) {
      super(code);
      this.items = new ArrayList<>();
      this.items.add(new Item());
    }

    @Override
    boolean accept(StrCode code, String text) {
      if (((Builder)items.get(items.size() - 1)).accept(code, text)) {
        return true;
      } else {
        switch (code) {
          case ITEM:
            items.add(new Item());
            return true;
          default:
            return false;
        }
      }
    }

    @Override
    public List<StrElement> getChildren() {
      return items;
    }

  }

  //--------------------------------------- Internal Mark Manipulation Methods.

  private static final int MAX_LENGTH = 70 * 70;
  private static final String ZERO_LENGTH_MARK = "00";

  private StrCode getCode(int index) {
    validateIndex(index);
    return StrCode.getStrCode(buffer.charAt(index));
  }

  private int getLength(int index) {
    validateIndex(index);
    return (int)(buffer.charAt(index + 1) - '0') * 70
      + (int)(buffer.charAt(index + 2) - '0');
  }

  private int next(int index) {
    validateIndex(index);
    return index + getLength(index) + 3;
  }

  private int validateIndex(int index) {
    if (index >= buffer.length() || index < 0) {
      throw new CommonException()
              .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
              .set("index", index)
              .set("buffer size", buffer.length());
    } else {
      return index;
    }
  }

  private String getText(int index) {
    validateIndex(index);
    int length = getLength(index);
    String text = buffer.substring(index + 3, index + 3 + length);
    return text;
  }

  private boolean hasNext(int index) {
    return index < buffer.length();
  }

}
