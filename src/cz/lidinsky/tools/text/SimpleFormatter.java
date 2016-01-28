/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jilm
 */
public class SimpleFormatter {

  public SimpleFormatter() {
    indentChars = new char[10];
    Arrays.fill(indentChars, ' ');
    indentLevel = 4;
  }

  /** Width of the line. */
  private int width = 79;

  public String format(String buffer) {
    //StringBuilder sb = new StringBuilder();
    StrIterator iterator = new StrIterator(buffer);
    StrElement element = iterator.next();
    while (element != null) {
      System.out.println(element.toString());
      StrCode code = element.getCode();
      switch (code) {
        case HEAD0:
          formatTitle(element);
          firstParagraph = true;
          break;
        case PARAGRAPH:
          formatParagraph(element);
          break;
      }
      element = iterator.next();
    }
    return sb.toString();
  }

  private boolean parFirstLine = true;
  private boolean firstParagraph = false;

  protected void formatParagraph(StrElement paragraph) {
    //parFirstLine = true;
    for (StrElement element : paragraph.getChildren()) {
      List<String> words = StrUtils.splitWords(element.getText());
      for (String word : words) {
        if (linePosition + word.length() >= width) {
          newLine();
          parFirstLine = false;
        }
        appendWord(word);
      }
    }
    //appendEmptyLine();
    newLine();
    firstParagraph = false;
    parFirstLine = true;
  }

  protected void formatTitle(StrElement title) {
    String titleText = title.getText().toUpperCase();
    appendLine(StrUtils.center(titleText, width));
    appendEmptyLine();
  }

  private StringBuilder sb = new StringBuilder();

  /** Character counter on the line. */
  private int linePosition = 0;

  /**
   * Returns number of characters already on the actual line. It takes indent
   * characters into account.
   */
  protected int getLinePosition() {
    return (linePosition == 0 ? getIndent() : linePosition)
      + getPrefix().length();
  }

  private String wordDelimiter = "";

  /**
   * Appends given word into the internal buffer.
   */
  protected void appendWord(String word) {
    if (!StrUtils.isBlank(word)) {
      sb.append(newLineChar)
        //.append(indentChars, 0, getIndentLength())
        .append(getPrefix())
        .append(wordDelimiter)
        .append(word);
      linePosition += word.length() + wordDelimiter.length() + getIndent();
      wordDelimiter = " ";
      newLineChar = "";
      emptyLine = false;
    }
  }

  protected void appendLine(String line) {
    if (!StrUtils.isBlank(line)) {
      newLine();
      sb.append(newLineChar)
        .append(line);
      newLine();
      emptyLine = false;
    }
  }

  private boolean emptyLine = false;

  protected void appendEmptyLine() {
    if (!emptyLine) {
      sb.append("\n");
      newLine();
      emptyLine = true;
      lineNumber++;
    }
  }

  private String newLineChar = "";

  private int lineNumber = 0;

  protected void newLine() {
    newLineChar = "\n";
    wordDelimiter = "";
    linePosition = 0;
    lineNumber++;
  }

  private char[] indentChars;
  private int indentLevel = 2;

  protected int getIndent() {
    return linePosition == 0
            ? (indentLevel + (!firstParagraph && parFirstLine ? 4 : 0))
            : 0;
  }

  //---------------------------------------------------------- Unordered lists.
  
  public static final char[] BULLETS = new char[] {'o', '*', '-'};

  protected void formatUnorderedList(StrElement element) {
    firstLinePrefix = " o  ";
    prefix = "    ";
    element.getChildren().stream()
      .filter(child -> !child.isEmpty())
      .forEach(item -> formatItem(item));
    prefix = "";
    firstLinePrefix = "";
  }

  protected void formatItem(StrElement element) {

  }

  private String firstLinePrefix = "";
  private String prefix = "";

  protected String getPrefix() {
    return parFirstLine ? prefix : "";
  }

  //--------------------------------------------------------------------------


}
