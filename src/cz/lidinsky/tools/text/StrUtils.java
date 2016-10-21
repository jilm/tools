/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author jilm
 */
public class StrUtils {

  public static String center(String text, int width) {
    if (isBlank(text)) {
      return "";
    } else {
      text = text.trim();
      if (text.length() > width) {
        return text;
      } else if (text.length() == width) {
        return text;
      } else {
        char[] result = new char[width];
        Arrays.fill(result, ' ');
        int offset = (width - text.length()) / 2;
        text.getChars(0, text.length(), result, offset);
        return new String(result);
      }
    }
  }

  public static boolean isEmpty(String text) {
    return text == null || text.isEmpty();
  }

  public static boolean isBlank(String text) {
    return text == null || text.isEmpty() || text.trim().isEmpty();
  }

  public static List<String> splitWords(String text) {
    List<String> result = new ArrayList<>();
    char[] chars = text.toCharArray();
    int begin = 0;
    boolean word = false;
    for (int i = 0; i < chars.length; i++) {
      if (Character.isWhitespace(chars[i])) {
        if (word) {
          result.add(new String(chars, begin, i - begin));
          word = false;
        }
      } else {
        if (!word) {
          begin = i;
          word = true;
        }
      }
    }
    if (word) {
      result.add(new String(chars, begin, chars.length - begin));
    }
    return result;
  }

}
