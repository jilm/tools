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

class ItemFormatter extends Formatter {

  private final StrElement element;

  private String prefix = " o  ";

  private final List<StrElement> children;

  ItemFormatter(int lineLength, StrElement element) {
    super(lineLength);
    this.element = element;
    this.children = new ArrayList<>();
    this.children.addAll(element.getChildren());
  }

  private Formatter formatter;

  @Override
  public String getLine() {
    String line;
    while (!children.isEmpty()) {
      if (formatter == null) {
        formatter
              = new ParagraphFormatter(
                      getLineLength(), false, children.remove(0));
      }
      line = formatter.getLine();
      if (line == null) {
        formatter = null;
      } else {
        line = getPrefix() + line + getSuffix();
        prefix = "    ";
        return line;
      }
    }
    return null;
  }

  @Override
    protected String allign() {
      return allignLeft();
    }

  @Override
    protected String getPrefix() {
      return prefix;
    }

  @Override
  protected String getNextLiteral() {
    return null;
  }

}
