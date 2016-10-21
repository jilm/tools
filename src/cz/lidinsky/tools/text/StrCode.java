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


/**
 */
public enum StrCode {

  APPENDIX,
  EMPHASIZE,
  END,
  EXTENDED,
  HEAD0,
  HEAD1,
  HEAD2,
  HEAD3,
  HEAD4,
  HORIZONTAL_RULE,
  ITEM,
  LIST_ORDERED,
  LIST_UNORDERED,
  LITERAL,
  NESTED,
  NEW_LINE,
  PARAGRAPH,
  STRONG,
  TABLE,
  TEXT;

  char getCode() {
    return (char)((int)'0' + ordinal());
  }

  static StrCode getStrCode(char code) {
    return StrCode.class.getEnumConstants()[(int)(code - '0')];
  }

}
