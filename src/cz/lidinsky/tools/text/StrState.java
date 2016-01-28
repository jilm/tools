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

/**
 *  String buffer which place a special marks between the characters. These
 *  marks are then used to format the whole text.
 *
 * PAR, ?, HEAD -> HEAD
 * PAR, PAR -> PAR
 * PAR, TEXT, PAR -> PAR, TEXT, END_PAR, PAR
 * TEXT, EXTEND -> TEXT
 *
 */
class StrState {

  private final StrBuffer sb;

  /**
   *  Creates new empty buffer.
   */
  StrState(StrBuffer buffer) {
    sb = buffer;
  }

  private StrCode[] buffer;


}
