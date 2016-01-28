/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jilm
 */
public abstract class Formatter {

  private int lineLength;
  private int lineNumber;

  public Formatter(int lineLength) {
    this.lineLength = lineLength;
    this.lineNumber = 0;
    this.literals = new ArrayList<>();
    //this.lengthRemaining = getLineLength();
  }

  private List<String> literals;
  private int lengthRemaining = -1;

  public String getLine() {
    if (lengthRemaining < 0) {
      this.lengthRemaining = getLineLength();
    }
    String literal = getNextLiteral();
    while (literal != null) {
      String line = appendLiteral(literal);
      if (line != null) {
        line = getPrefix() + line + getSuffix();
        lineNumber++;
        return line;
      }
      literal = getNextLiteral();
    }
    String line = flush();
    if (line != null) {
      return getPrefix() + line + getSuffix();
    }
    return null;
  }

  protected abstract String getNextLiteral();

  public String appendLiteral(String literal) {
    if (!StrUtils.isBlank(literal)) {
      if (lengthRemaining <= literal.length()) {
        String line = allign();
        //appendLine(line);
        //lineNumber++;
        literals.clear();
        literals.add(literal);
        lengthRemaining = getLineLength() - literal.length();
        return line;
      } else {
        literals.add(literal);
        lengthRemaining -= literal.length() + 1;
        return null;
      }
    }
    return null;
  }

  public String flush() {
    if (!literals.isEmpty()) {
      String line = allign();
      //appendLine(line);
      //lineNumber++;
      literals.clear();
      lengthRemaining = getLineLength();
      return line;
    }
    return null;
  }

  protected abstract String allign();

  protected String allignLeft() {
    char[] buffer = new char[getLineLength()];
    Arrays.fill(buffer, ' ');
    int index = 0;
    for (String literal : literals) {
      literal.getChars(0, literal.length(), buffer, index);
      index += literal.length() + 1;
    }
    return new String(buffer);
  }

  protected String allignCenter() {
    char[] buffer = new char[getLineLength()];
    Arrays.fill(buffer, ' ');
    int index = lengthRemaining / 2;
    for (String literal : literals) {
      literal.getChars(0, literal.length(), buffer, index);
      index += literal.length() + 1;
    }
    return new String(buffer);
  }

  protected String getPrefix() {
    return "";
  }

  protected String getSuffix() {
    return "";
  }

  protected int getLineNumber() {
    return lineNumber;
  }

  protected int getLineLength() {
    return lineLength - getPrefix().length() - getSuffix().length();
  }


}
