package cz.lidinsky.tools.xml;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;

public class ExpressionTest extends Expression
{

  String ns1 = "http://control4j.lidinsky.cz/application";
  String ns2 = "http://control4j.lidinsky.cz/gui";
  public ArrayList<Pair<String, String>> context1;
  public ArrayList<Pair<String, String>> context2;
  public ArrayList<Pair<String, String>> context3;

  @Before
  public void initialize()
  {
    context1 = new ArrayList<Pair<String, String>>();
    context1.add(ROOT);
    context1.add(new ImmutablePair<String, String>(ns1, "root"));
    context1.add(new ImmutablePair<String, String>(ns1, "element1"));
    context1.add(new ImmutablePair<String, String>(ns1, "element2"));
    context1.add(new ImmutablePair<String, String>(ns1, "element3"));

    context2 = new ArrayList<Pair<String, String>>();
    context2.add(ROOT);
    context2.add(new ImmutablePair<String, String>(ns1, "application"));
  }

  @Test
  public void test1()
  {
    String expression = "element1";
    Pair<String, String> result = parseUriElementName(expression, "");
    assertEquals("", result.getLeft());
    assertEquals("element1", result.getRight());
  }

  @Test
  public void test2()
  {
    String expression = "{*}element1";
    Pair<String, String> result = parseUriElementName(expression, "");
    assertEquals("*", result.getLeft());
    assertEquals("element1", result.getRight());
  }

  @Test
  public void test3()
  {
    String expression = "{http://control4j.lidinsky.cz/application}element1";
    Pair<String, String> result = parseUriElementName(expression, "");
    assertEquals("http://control4j.lidinsky.cz/application", result.getLeft());
    assertEquals("element1", result.getRight());
  }

  @Test
  public void test4()
  {
    String expression = "{http://control4j.lidinsky.cz/application}element3";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context1));
  }

  @Test
  public void test5()
  {
    String expression = "{*}element2/{*}element3";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context1));
  }

  @Test
  public void test6()
  {
    String expression = "{*}element2//{*}element3";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context1));
  }

  @Test
  public void test7()
  {
    String expression = "{*}root//{*}element3";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context1));
  }

  @Test
  public void test8()
  {
    String expression = "/{*}root//{*}element3";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context1));
  }

  @Test
  public void test9()
  {
    String expression = "{*}element2";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertFalse(ex.evaluate(context1));
  }

  @Test
  public void test10()
  {
    String expression = "{ns2}element3";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertFalse(ex.evaluate(context1));
  }

  @Ignore @Test
  public void test11()
  {
    String expression = "//{ns1}element3";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context1));
  }

  @Test
  public void test12()
  {
    String expression = "{*}*";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context1));
  }

  // Root element test
  @Test
  public void test13()
  {
    String expression
          = "/{http://control4j.lidinsky.cz/application}application";
    Expression ex = new Expression();
    ex.parse(expression, "");
    assertTrue(ex.evaluate(context2));
  }

  public void testCompare1() {
    String expression1 = "element1/element2";
    String expression2 = "element";
    Expression ex1 = new Expression();
    ex1.parse(expression1, "");
    Expression ex2 = new Expression();
    ex2.parse(expression2, "");
    assertEquals(1, ex1.compare(ex1, ex2));
  }

}

