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

class UnorderedListFormatter extends Formatter {

  private List<StrElement> items;

  UnorderedListFormatter(int lineLength, StrElement element) {
    super(lineLength);
    this.items = new ArrayList<>();
    this.items.addAll(element.getChildren());
  }

  private Formatter formatter;

  @Override
  public String getLine() {
    String line;
    while (!items.isEmpty()) {
      if (formatter == null) {
        formatter
              = new ItemFormatter(
                      getLineLength(), items.remove(0));
      }
      line = formatter.getLine();
      if (line == null) {
        formatter = null;
      } else {
        line = getPrefix() + line + getSuffix();
        return line;
      }
    }
    return null;
  }


  @Override
    protected String getNextLiteral() {
      return null;
    }

  @Override
    protected String allign() {
      return allignLeft();
    }

  @Override
  protected String getPrefix() {
    return "";
  }


}
