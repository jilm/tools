/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jilm
 */

class ParagraphFormatter extends Formatter {

  private final StrElement element;

  private List<String> literals;

  private boolean allignFirstLine;

  ParagraphFormatter(int lineLength, boolean allignFirstLine, StrElement element) {
    super(lineLength);
    this.element = element;
    this.literals = new ArrayList<>();
    this.allignFirstLine = allignFirstLine;
    for (StrElement child : element.getChildren()) {
      literals.addAll(StrUtils.splitWords(child.getText()));
    }
  }

  @Override
    protected String getNextLiteral() {
      return literals.isEmpty() ? null : literals.remove(0);
    }

  @Override
    protected String allign() {
      return allignLeft();
    }

  @Override
    protected String getPrefix() {
      return getLineNumber() == 0 && allignFirstLine ? "    " : "";
    }

}
