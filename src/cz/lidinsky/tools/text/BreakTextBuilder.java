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
 */
public class BreakTextBuilder extends TextBuilder {

  private final TextBuilder builder;

  private int pageWidth;
  private int lineLength;

  public BreakTextBuilder(TextBuilder builder) {
    this.builder = builder;
    this.pageWidth = 80;
    this.lineLength = 0;
  }

  @Override
  public String toString() {
    return builder.toString();
  }

  @Override
  protected void appendLine(String text) {
    if (!isEmpty(text)) {
      int remaining = pageWidth - builder.getCharCount();
      if (text.length() <= remaining) {
        builder.appendLine(text);
      } else {
        int spaceIndex = text.indexOf(" ");
        if (spaceIndex < 0) {
          builder.appendLine(text.substring(0, remaining - 3) + ">>>");
          newLine();
          appendLine(text.substring(remaining - 2));
        } else {

        }
      }
    }
  }



}
