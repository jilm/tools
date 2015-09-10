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
    sb.insertBefore(indent);
    int lineCounter = sb.getLength();
    while (sb.hasNext()) {
      int length = sb.getNextLength();
      char controlCode = sb.getNextCode();
      if (controlCode == INDENT_CODE) {
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
        return;
      } else {
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

  protected int calculateLength(StrBuffer sb) {
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
    return length;
  }

}
