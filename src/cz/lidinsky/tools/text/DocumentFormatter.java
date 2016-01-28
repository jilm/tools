/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

public class DocumentFormatter {

  private StringBuilder sb;

  public DocumentFormatter() {
    sb = new StringBuilder();
  }

  protected void appendLine(String line) {
    sb.append(line)
      .append("\n");
  }

  public String format(StrIterator iterator) {
    StrElement element = iterator.next();
    Formatter formatter = null;
    while (element != null) {
      StrCode code = element.getCode();
      switch (code) {
        case HEAD0:
          formatter = new TitleFormatter(76, element);
          break;
        case PARAGRAPH:
          formatter = new ParagraphFormatter(76, false, element);
          break;
        case LIST_UNORDERED:
          formatter = new UnorderedListFormatter(76, element);
          break;
        default:
          formatter = null;
          break;
      }
      if (formatter != null) {
        String line = formatter.getLine();
        while (line != null) {
          appendLine(line);
          line = formatter.getLine();
        }
      }
      element = iterator.next();
    }
    return sb.toString();
  }

}
