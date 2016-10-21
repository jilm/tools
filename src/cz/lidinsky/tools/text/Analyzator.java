/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.text;

/**
 *
 * @author jilm
 */
public class Analyzator {

  private final int[] count;

  public Analyzator() {
    int size = StrCode.class.getEnumConstants().length;
    count = new int[size];
  }

  public void analyze(StrIterator iterator) {
    StrElement element = iterator.next();
    while (element != null) {
      StrCode code = element.getCode();
      count[code.ordinal()]++;
      switch (code) {
      }
      element = iterator.next();
    }
  }

  public int getCount(StrCode code) {
    return count[code.ordinal()];
  }

}
