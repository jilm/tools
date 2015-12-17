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

public class DefaultTypesetter {

  /**
   *  Takes string buffer that was produced by the ToStringBuilder object and
   *  render the content to plain text.
   */
  public void build(StrBuffer buffer) {
    insertLineBreakMarks(buffer);
    buffer.reset();
    format(buffer, "");
  }

  //-------------------------------------------------------- Insert Typo Marks.

  protected static final char BREAK_CODE = 'b';
  protected static final char INDENT_CODE = 'i';
  protected static final char UNINDENT_CODE = 'u';

  /**
   *  Connect key and consecutive atom into one atom with break before it.
   *  Insert delimiter between elements inside the content.
   *  Replace content start with indent code.
   *  Replace content end with unindent code.
   */
  protected void insertLineBreakMarks(StrBuffer sb) {

    // join class name with subsequent hash code
    sb.first(ToStringBuilder.CLASS_NAME_CODE);
    while (sb.hasNext()) {
      if (sb.getNextCode() == ToStringBuilder.HASH_CODE) {
        sb.join("@");
      }
      if (sb.getNextCode() == ToStringBuilder.OBJECT_START_CODE) {
        sb.insertBehind("[");
      } else if (sb.getNextCode() == ToStringBuilder.ARRAY_START_CODE) {
        sb.insertBehind("{");
      }
      sb.setCode(BREAK_CODE)
        .next(ToStringBuilder.CLASS_NAME_CODE);
    }

    // join key with next element
    sb.first(ToStringBuilder.KEY_CODE);
    while (sb.hasNext()) {
      sb.join(": ");
      sb.setCode(BREAK_CODE);
      sb.next(ToStringBuilder.KEY_CODE);
    }

    // replace all atom codes with break code
    sb.reset()
      .replaceAll(ToStringBuilder.ATOM_CODE, BREAK_CODE);

    // replace content start and end with indent and unindent codes.
    // remove consecutive break code
    sb.reset();
    while (!sb.isAtTheEnd()) {
      if (sb.getCode() == ToStringBuilder.OBJECT_END_CODE) {
        sb.insertBefore("]")
          .setCode(UNINDENT_CODE);
      } else if (sb.getCode() == ToStringBuilder.ARRAY_END_CODE) {
        sb.insertBehind("}")
          .setCode(UNINDENT_CODE);
      } else if (sb.getCode() == ToStringBuilder.OBJECT_START_CODE) {
        sb.join()
          .setCode(INDENT_CODE);
      } else if (sb.getCode() == ToStringBuilder.ARRAY_START_CODE) {
        sb.join()
          .setCode(INDENT_CODE);
      }
      sb.next();
    }

    // place a delimiter between two break items.
    sb.reset();
    while (sb.hasNext()) {
      if ((sb.getCode() == BREAK_CODE || sb.getCode() == INDENT_CODE)
          && sb.getNextCode() == BREAK_CODE) {
        sb.insertBehind(", ");
      }
      sb.next();
    }
  }

  protected int lineLength = 80;

  protected void format(StrBuffer sb, String indent) {
    int lineCounter = sb.getLength();
    while (sb.hasNext()) {
      int length = sb.getNextLength();
      char controlCode = sb.getNextCode();

      if (controlCode == INDENT_CODE) {
        // following content must be intended if it is must be broken into more
        // than one line.
        sb.push().next();
        if (lineCounter + calculateLength(sb) < lineLength) {
          sb.pop().join();
          lineCounter += length;
        } else {
          sb.pop()
            .insertBehind("\n") // new line
            .push()
            .next() // go to the next element to format it
            .setCode(BREAK_CODE);
          format(sb, indent + "  ");
          sb.pop()
            .join() // join with the nested element
            .join("\n" + indent) // join with the closing bracket
            .insertBehind("\n");
          lineCounter = indent.length();
        }
      } else if (controlCode == UNINDENT_CODE) {
        // unindent code
        return;
      } else {
        // just break mark
        if (lineCounter + length < lineLength) {
          sb.join();
          lineCounter += length;
        } else {
          sb.join("\n" + indent);
          lineCounter = length + indent.length();
        }
      }
    }
  }

  /**
   *  Formats the block which starts with indent and ends with unindent marks.
   *  If it is possible to place the whole block on one line, just do it. If
   *  not separate particular items with new line char such that the line is
   *  not overflowed.
   *
   *  <p>The whole list is not longer then one line:
   *  <pre>[item1: 12, item2: "hello"]</pre>
   *
   *  <p>There is nested list, but still it is not longer than one line:
   *  <pre>[item1: NestedObject@123456[color: blue]]</pre>
   *
   *  <p>It is not possible to render the whole list on one line:
   *  <pre>
   *  [
   *    manufacturer: Skoda, type: "Forman", color: blue, engine: diesel,
   *    manufactured: 2001
   *  ]
   *  </pre>
   *
   *  <p>There is an inner object which is longer than line:
   *  <pre>
   *  [
   *    item1: NestedObject@123456 [
   *      message: "Immediately return back! You are in serious danger!"
   *    ],
   *    urgency: high
   *  ]
   *  </pre>
   *
   *  @return true if the list was formatted as multiline
   *
   */
  protected boolean formatList(StrBuffer sb, String indent, int remaining) {
    analyzeList(sb);
    boolean multiline = (listLength + 2 * (listItems - 1) >= remaining);
    if (multiline) {
      formatListMultiline(sb, indent);
    } else {
      formatListOneLine(sb);
    }
    return multiline;
  }

  /**
   *  Format list such that all of the lines are on one line.
   */
  protected int formatListOneLine(StrBuffer sb) {
    if (sb.isAtTheEnd()) return 0;
    String delimiter = ", ";
    while (sb.hasNext()) {
      char code = sb.getNextCode();
      if (code == INDENT_CODE) {
        sb.push().next().next();
        formatListOneLine(sb);
        sb.insertBefore("[").insertBehind("]");
        sb.pop();
        sb.join().join().join();
      } else if (code == UNINDENT_CODE) {
        break;
      } else {
        sb.join(delimiter);
      }
    }
    return sb.getLength();
  }

  /**
   *  Format list such that each item is on the separate line, no matter how
   *  long the item is.
   */
  protected void formatListMultiline(StrBuffer sb, String indent) {
    String delimiter = ",\n" + indent;
    while (sb.hasNext()) {
      char code = sb.getNextCode();
      if (code == INDENT_CODE) {
        sb.push().next();
        boolean multiline = formatList(
            sb, indent + "  ", lineLength - indent.length() - 2);
        sb.pop();
        if (multiline) {
          sb.join("[\n" + indent + "  ");
          sb.insertBehind("\n" + indent + "]");
        } else {
          sb.join("[").insertBehind("]");
        }
      } else if (code == UNINDENT_CODE) {
        sb.join();
        break;
      } else {
        sb.join(delimiter);
      }
    }
  }

  /**
   *  Try to use the whole line, places items on one line as long as the line
   *  is full, than starts to place another items into the new line.
   */
  protected void formatListFlow(StrBuffer sb, String indent) {
    if (sb.isAtTheEnd()) return;
    int length = indent.length() + sb.getLength();
    while (sb.hasNext()) {
      char code = sb.getNextCode();
      if (code == INDENT_CODE) {
        sb.push().next();
        formatListOneLine(sb);
        sb.insertBefore("[").insertBehind("]");
        sb.pop();
        if (length + sb.getNextLength() > lineLength) {
          length = indent.length() + sb.getNextLength();
          sb.join(",\n" + indent);
        }
      } else if (code == UNINDENT_CODE) {
        break;
      } else {
        if (length + sb.getNextLength() > lineLength) {
          length = indent.length() + sb.getNextLength();
          sb.join(",\n" + indent);
        } else {
          sb.join(", ");
        }
      }
    }
  }

  /** Total length of the whole list. */
  protected int listLength;
  /** How many items are in the list. The inner list is counted only onece*/
  protected int listItems;
  /** How many inner lists are in this list. */
  protected int innerLists;
  /** Length of the largest item. */
  protected int listLargestItem;

  protected void analyzeList(StrBuffer sb) {
    sb.push();
    listLength = 0;
    listItems = 0;
    innerLists = 0;
    listLargestItem = 0;
    while (!sb.isAtTheEnd()) {
      char code = sb.getCode();
      if (code == INDENT_CODE) innerLists++;
      if (code == UNINDENT_CODE) break;
      int length = next(sb);
      listLength += length;
      listLargestItem = Math.max(listLargestItem, length);
      listItems++;
    }
    sb.pop();
  }

  /**
   *  Moves cursor ot the given buffer and returns the length of the item. If
   *  the code under the cursor is INDENT code, than this method moves the
   *  cursor to the appropriate UNINDENT code and returns the whole lenght of
   *  the inner block.
   */
  protected int next(StrBuffer sb) {
    int length = sb.getLength();
    char code = sb.getCode();
    sb.next();
    if (code == INDENT_CODE) {
      while (!sb.isAtTheEnd() && sb.getCode() != UNINDENT_CODE) {
        length += next(sb);
      }
      length += sb.getLength();
      sb.next();
    }
    return length;
  }

  /**
   *  Returns the length of the list.
   */
  protected int calculateLength(StrBuffer sb) {
    sb.push();
    int length = 0;
    int level = 0;
    while (!sb.isAtTheEnd()) {
      char code = sb.getCode();
      if (code == INDENT_CODE) level++;
      if (code == UNINDENT_CODE) level--;
      if (level == 0) return length;
      length += sb.getLength();
      sb.next();
    }
    sb.pop();
    return length;
  }

}
