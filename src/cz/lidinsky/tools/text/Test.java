package cz.lidinsky.tools.text;

public class Test {
  public static void main(String[] args) {
    StrBuffer sb = new StrBuffer();
    sb.appendHead("Toto je nadpis prvni urovne");
    sb.append("No a toto uz je text. Schvalne ho napiseme trosicku delsi nez je sirka jedne obrazovky terminalu, abychom vyzkouseli zalamovani textu na radek.");
    sb.startParagraph("Toto uz je ale druhy odstavec,");
    sb.append("a to jsem tedy opravdu zvedav co se s tim stane.");
    sb.startUnorderedList();
    sb.startItem();
    sb.append("Toto je prvni polozka seznamu");
    sb.startItem();
    sb.append("No a toto uz je druha polozka seznamu.");
    //SimpleFormatter sf = new SimpleFormatter();
    //System.out.println(sf.format(sb.toString()));
    System.out.println(new DocumentFormatter().format(new StrIterator(sb.toString())));
  }
}
