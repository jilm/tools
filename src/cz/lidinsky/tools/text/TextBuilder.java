/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

/**
 */
public class TextBuilder {

  private final StringBuilder builder;

  public TextBuilder() {
    this.builder = new StringBuilder();
    charCounter = 0;
  }

  @Override
  public String toString() {
    return builder.toString();
  }

  private int charCounter;

  /**
   * Appends given text. This method split argument around the new line
   * characters and calls methods append and newLine.
   *
   * @param text
   *            a text to append
   */
  public void append(String text) {
    if (!isEmpty(text)) {
      String[] lines = text.split("\n", -1);
      appendLine(lines[0]);
      for (int i = 1; i < lines.length; i++) {
        newLine();
        appendLine(lines[i]);
      }
    }
  }

  protected void appendLine(String text) {
    if (!isEmpty(text)) {
      builder.append(text);
      charCounter += text.length();
    }
  }

  /**
   *  Insert new line character.
   */
  public void newLine() {
    builder.append("\n");
    charCounter = 0;
  }

  /**
   * Retuns number of characters on the last line.
   *
   * @return number of characters on the last line
   */
  public int getCharCount() {
    return charCounter;
  }

  /**
   * Returns true if either the given text is null or the length of the text
   * zero.
   *
   * @param text
   *
   * @return
   */
  public static boolean isEmpty(String text) {
    return text == null || text.length() == 0;
  }

}
