package cz.lidinsky.tools;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ParserChainTest {

  ParserChain chain = new ParserChain();

  @Before
    public void init() {
      ParserChain.add(chain, Integer.class, ParserUtils.intParser());
      ParserChain.add(chain, Double.class, ParserUtils.doubleParser());
      ParserChain.add(chain, Boolean.class, ParserUtils.booleanParser());
    }

  @Test
    public void test1() {
      int result = 12;
      assertEquals(
          (int)result,
          (int)ParserChain.parse(chain, "12", Integer.class));
    }

  @Test
    public void test2() {
      double result = 45.67d;
      assertEquals(
          (double)result,
          (double)ParserChain.parse(chain, "45.67", Double.class),
          0.001d);
    }

  @Test
    public void test3() {
      boolean result = true;
      assertTrue(ParserChain.parse(chain, "true", Boolean.class));
    }

}
