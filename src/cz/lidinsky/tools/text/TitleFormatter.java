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

class TitleFormatter extends Formatter {

  private final StrElement element;

  private List<String> literals;

  TitleFormatter(int lineLength, StrElement element) {
    super(lineLength);
    this.element = element;
    this.literals = new ArrayList<>();
    literals.addAll(StrUtils.splitWords(element.getText().toUpperCase()));
  }

  @Override
    protected String getNextLiteral() {
      return literals.isEmpty() ? null : literals.remove(0);
    }

  @Override
    protected String allign() {
      return allignCenter();
    }

}
