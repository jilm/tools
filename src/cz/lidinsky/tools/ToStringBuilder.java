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

import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.text.StrBuilder;

import java.util.Map;
import java.util.Set;
import java.util.ArrayDeque;

/**
 *  Examples:
 *  <pre>
 *    ColorClass@123456[R: 0, G: 128, B: 128]
 *
 *    Employees@abcdg {
 *      Employee@456789[name: Johny Walker, position: director, born: 1973],
 *      Employee@ABYCDH[name: Bob Dylen, position: performer, born: 1963],
 *      ...
 *    }
 *
 *    Recipe@845421 [
 *      name: Lemon-avocado chicken salad,
 *      categories: Set {
 *        Avocados, Chicken, Chicken Salad, Fruit, Lunch, Lunch Salad,
 *        Quick and Easy, Salad
 *      },
 *      ingredients: {
 *        00: chopped deli-rosted chicken, chopped celery, mayonnaise,
 *        03: thinly sliced green onion tops, chopped fresh parsley,
 *        05: finely shredded Parmesan cheese, lemon juice, lemon zest,
 *        08: garlic, ground black pepper, chpped avocado
 *      },
 *      directions: List{
 *        In a small bowl, stir together chicken, celery, mayonnaise, green onion, parsley, lemon juice and black pepper. Add Parmesan cheese, lemon zest and garlic. Toss gently to combine. Add avocado before serving. Gently toss to combine.
 *      }
 *    ]
 *
 *  </pre>
 *
 *
 */
public class ToStringBuilder {

  protected StrBuffer sb;

  public ToStringBuilder() {
    this(new StrBuffer());
  }

  public ToStringBuilder(StrBuffer sb) {
    this.sb = sb;
  }

  /**
   *  It place following sequence into the buffer.
   *  <ol>
   *    <li> [CLASS_NAME_CODE][class name]
   *    <li> [HASH_CODE][class hash]
   *    <li> [OBJECT_START_CODE]
   *    <li> object content
   *    <li> [OBJECT_END_CODE]
   *  </ol>
   */
  public ToStringBuilder append(String fieldName, IToStringBuildable object) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(object);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object object) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(object);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, String value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Iterable value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, String[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, int value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, long value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, boolean value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, float value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, double value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String value) {
    if (value == null) {
      appendNull();
    } else {
      sb.append(ATOM_CODE, value);
    }
    return this;
  }

  public ToStringBuilder append(String[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      sb.append(CLASS_NAME_CODE, "String[]");
      appendHashCode(value);
      startArray();
      for (String element : value) {
        append(element);
      }
      endArray();
    }
    return this;
  }

  public ToStringBuilder append(IToStringBuildable object) {
    if (object == null) {
      appendNull();
    } else {
      appendClassName(object.getClass());
      appendHashCode(object);
      startObject();
      object.toString(new ToStringBuilder(sb));
      endObject();
    }
    return this;
  }

  public void append(Object object) {
    if (object == null) {
      appendNull();
    } else {
      if (object instanceof IToStringBuildable) {
        append((IToStringBuildable)object);
      } else if (object instanceof Integer) {
        append(((Integer)object).intValue());
      } else {
        sb.append(ATOM_CODE, object);
      }
    }
  }

  public ToStringBuilder append(int value) {
    sb.append(ATOM_CODE, value);
    return this;
  }

  public ToStringBuilder append(long value) {
    sb.append(ATOM_CODE, value);
    return this;
  }

  public ToStringBuilder append(boolean value) {
    sb.append(ATOM_CODE, value);
    return this;
  }

  public ToStringBuilder append(float value) {
    sb.append(ATOM_CODE, value);
    return this;
  }

  public ToStringBuilder append(double value) {
    sb.append(ATOM_CODE, value);
    return this;
  }

  public ToStringBuilder append(Iterable object) {
    if (object == null) {
      appendNull();
    } else {
      sb.append(CLASS_NAME_CODE, "Iterable[]");
      appendHashCode(object);
      startArray();
      for (Object element : object) {
        append(element);
      }
      endArray();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, IToStringBuildable[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, Object[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
      //appendFieldDelimiter();
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, int[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, long[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, boolean[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, float[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(String fieldName, double[] value) {
    if (fieldName != null) {
      sb.append(KEY_CODE, fieldName);
      append(value);
    }
    return this;
  }

  public ToStringBuilder append(int[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      sb.append(CLASS_NAME_CODE, "int[]");
      appendHashCode(value);
      startArray();
      for (int element : value) {
        append(element);
      }
      endArray();
    }
    return this;
  }

  public ToStringBuilder append(long[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      sb.append(CLASS_NAME_CODE, "long[]");
      appendHashCode(value);
      startArray();
      for (long element : value) {
        append(element);
      }
      endArray();
    }
    return this;
  }

  public ToStringBuilder append(boolean[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      sb.append(CLASS_NAME_CODE, "boolean[]");
      appendHashCode(value);
      startArray();
      for (boolean element : value) {
        append(element);
      }
      endArray();
    }
    return this;
  }

  public ToStringBuilder append(float[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      sb.append(CLASS_NAME_CODE, "float[]");
      appendHashCode(value);
      startArray();
      for (float element : value) {
        append(element);
      }
      endArray();
    }
    return this;
  }

  public ToStringBuilder append(double[] value) {
    if (value == null) {
      appendNull();
    } else if (value.length == 0) {
      appendEmptyArray();
    } else {
      sb.append(CLASS_NAME_CODE, "double[]");
      appendHashCode(value);
      startArray();
      for (double element : value) {
        append(element);
      }
      endArray();
    }
    return this;
  }

  //----------------------------------------------------- Append Value Methods.

  @Override
  public String toString() {
    new DefaultTypesetter().build(sb);
    return sb.toString();
  }

  public static final char KEY_CODE = 'k';
  public static final char ATOM_CODE = 'a';
  public static final char OBJECT_START_CODE = 'o';
  public static final char ARRAY_START_CODE = 'y';
  public static final char OBJECT_END_CODE = 'e';
  public static final char ARRAY_END_CODE = 'n';
  public static final char CLASS_NAME_CODE = 'c';
  public static final char HASH_CODE = 'h';

  protected void appendHashCode(Object object) {
    sb.append(HASH_CODE, Integer.toHexString(object.hashCode()));
  }

  protected boolean simpleClassName = true;

  protected void appendClassName(Class _class) {
    sb.append(
        CLASS_NAME_CODE,
        simpleClassName ? _class.getSimpleName() : _class.getName());
  }

  protected void startObject() {
    sb.append(OBJECT_START_CODE, "");
  }

  protected void endObject() {
    sb.append(OBJECT_END_CODE, "");
  }

  protected void appendNull() {
    sb.append(ATOM_CODE, "<null>");
  }

  protected void appendEmptyArray() {
    sb.append(ATOM_CODE, "<empty array>");
  }

  //------------------------------------------ Collection of Integers Handling.

  /** Indicate that the collection or array is rendered. */
  protected boolean collection = false;

  /** Counts number of elements of the collection or array. */
  protected int collectionSize;

  /** How many elements will be rendered before it is abbreviated. */
  protected int elementsBeforeAbbreviate = 20;

  /** If the collection is abbreviated. */
  protected boolean collectionAbbreviated;

  /** First value of the arithmetic sequence. */
  protected int arithmeticSeqA0;

  /** Difference between consecutive elements of the arithmetic sequence. */
  protected int arithmeticSeqDiff;

  /** Number of elements of the arithmetic sequence. */
  protected int arithmeticSeqN;

  /** Number of characters of the biggest elements in the sequence. */
  protected int maxElementSize;

  /** Index of the first character of the first element of the collection into
  the string buffer. */
  protected int collectionValuesStartIndex;

  /** Number of elements which were appended into the collection. */
  protected int collectionElements;

  protected int arithmeticSeqA0Index;

  /** Index into the string builder that indicates where to place collection
  size. */
  protected int collectionSizeIndex;

  /**
   *  Initialize all of the neccessary counters and temporary fields before
   *  some sequence is started and appends characters that preceeds the
   *  collection.
   */
  protected void startArray() {
    sb.append(ARRAY_START_CODE, "");
    collection = true;
    collectionSize = 0;
    collectionElements = 0;
    collectionAbbreviated = false;
    arithmeticSeqN = 0;
  }

  protected void endArray() {
    //handleArithmeticSeq();
    // inserts the collection size
    //reformatCollection();
    //sb.insert(collectionSizeIndex, collectionSize);
    // close the collection
    //if (collectionAbbreviated) sb.append(", ...");
    collection = false;
    sb.append(ARRAY_END_CODE, "");
  }

  /*

  protected void reformatCollection() {
    if (!collection) return;
    int index = collectionValuesStartIndex;
    while (index < sb.length() - 3) {
      index = cleenAndGetNext(index);
      if (index < sb.length() - 1) {
        sb.insert(index, ", ");
        index += 2;
      }
    }
  }
  */

  /*

  protected void detectArithmeticSeq(int value) {
    if (arithmeticSeqN == 0) {
      arithmeticSeqA0 = value;
      arithmeticSeqN = 1;
      arithmeticSeqA0Index = sb.length();
    } else if (arithmeticSeqN == 1) {
      arithmeticSeqDiff = value - arithmeticSeqA0;
      arithmeticSeqN++;
    } else {
      if (arithmeticSeqA0 + (arithmeticSeqN) * arithmeticSeqDiff == value) {
        // if this point is next element in the sequence
        arithmeticSeqN++;
      } else {
        // if this point is no longer next element of the arithmetic seq.
        if (arithmeticSeqDiff == 0 || arithmeticSeqN >= 3) {
          handleArithmeticSeq();
          // initialize the sequence counters
          arithmeticSeqA0 = value;
          arithmeticSeqN = 1;
          arithmeticSeqA0Index = sb.length();
        } else {
          // initialize the sequence counters
          arithmeticSeqA0 += arithmeticSeqDiff;
          arithmeticSeqA0Index = getNext(arithmeticSeqA0Index);
          arithmeticSeqDiff = value - arithmeticSeqA0;
          arithmeticSeqN = 2;
        }
      }
    }
  }
  */

  /*

  protected void handleArithmeticSeq() {
    if (arithmeticSeqDiff == 0 && arithmeticSeqN >= 2) {
      // there are at least two consecutive identical values
      // get index of the first element of the sequence
      int index = arithmeticSeqA0Index;
      // delete all of the element that belongs to the sequence
      sb.delete(index, sb.length());
      collectionElements -= arithmeticSeqN;
      collectionSize--;
      // append abbreviated form of the sequence
      String strSeq
        = Integer.toString(arithmeticSeqN)
        + "x" + Integer.toString(arithmeticSeqA0);
      appendValue(strSeq);
    } else if (arithmeticSeqN >= 3) {
      // there are at least three consecutive values that forms the
      // arithmetic sequence.
      // get the index of the first element of the sequence
      int index = indexOfCollectionElement(collectionElements - arithmeticSeqN);
      // delete all of the element that belongs to the sequence
      sb.delete(index, sb.length());
      collectionElements -= arithmeticSeqN;
      collectionSize--;
      // append abbreviated form of the sequence
      String strSeq
        = Integer.toString(arithmeticSeqA0)
        + "+nx" + Integer.toString(arithmeticSeqDiff)
        + ";for n in (0.." + Integer.toString(arithmeticSeqN) + ")";
      appendValue(strSeq);
    } else {
    }
  }

  */
/*
  protected int indexOfCollectionElement(int elementN) {
    int index = collectionValuesStartIndex;
    for (int i = 0; i < elementN; i++) {
      int elementLength = Integer.parseInt(sb.midString(index, 3));
      index += elementLength + 3;
    }
    return index;
  }
*/

}
