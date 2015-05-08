package cz.lidinsky.tools.xml;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.Method;
import java.util.Collection;
import org.xml.sax.Attributes;

import java.util.ArrayList;

@AXMLDefaultUri("http://control4j.lidinsky.cz/application")
public class XMLReaderTest extends XMLReader implements IXMLHandler
{

  public void startProcessing() {} 

  public void endProcessing() {}

  protected boolean startApplicationCalled = false;

  @AXMLStartElement("/application")
  public boolean startApplication() {
    System.out.println("startApplication called");
    startApplicationCalled = true;
    return true;
  }

  @AXMLStartElement("application/module")
  public boolean startModule() {
    return true;
  }

  protected XMLReader reader;

  @Before
  public void initialize()
  {
    reader = new XMLReader();
    reader.addHandler(this);
  }

  @Test
  public void test1()
  {
    reader.elementStack.add(new ImmutablePair(
          "http://control4j.lidinsky.cz/application", "application"));
    Collection<Triple<Expression, Method, IXMLHandler>>handlers
          = reader.findHandlerMethods(START_ELEMENT_EVENT);
    assertEquals("startApplication",
          handlers.iterator().next().getMiddle().getName());
  }

  @Test
  public void test2()
  {
    reader.elementStack.add(new ImmutablePair(
          "http://control4j.lidinsky.cz/application", "application"));
    reader.elementStack.add(new ImmutablePair(
          "http://control4j.lidinsky.cz/application", "module"));
    Collection<Triple<Expression, Method, IXMLHandler>>handlers
          = reader.findHandlerMethods(START_ELEMENT_EVENT);
    assertEquals("startModule",
          handlers.iterator().next().getMiddle().getName());
  }

  @Test
  public void test3() throws Exception
  {
    reader.elementStack.add(new ImmutablePair(
          "http://control4j.lidinsky.cz/application", "application"));
    Collection<Triple<Expression, Method, IXMLHandler>>handlers
                     = reader.findHandlerMethods(START_ELEMENT_EVENT);
    startApplicationCalled = false;
    reader.callHandlerMethod(START_ELEMENT_EVENT, null);
    assertTrue(startApplicationCalled);
  }

}
