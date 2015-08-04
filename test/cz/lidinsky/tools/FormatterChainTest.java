package cz.lidinsky.tools;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class FormatterChainTest {

  FormatterChain chain = new FormatterChain();

  @Before
    public void init() {
      FormatterChain.add(chain, Integer.class, FormatterUtils.numberFormatter());
      FormatterChain.add(chain, Double.class, FormatterUtils.numberFormatter());
      FormatterChain.add(chain, Boolean.class, FormatterUtils.toStringFormatter());
    }

  @Test
    public void test1() {
      assertEquals("12", FormatterChain.format(12, chain));
    }

  @Test
    public void test2() {
      assertEquals("45.67", FormatterChain.format(45.67d, chain));
    }

  @Test
    public void test3() {
      assertEquals("true", FormatterChain.format(true, chain));
    }

}
