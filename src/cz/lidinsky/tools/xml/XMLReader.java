package cz.lidinsky.tools.xml;

/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of tools.
 *
 *  tools is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  tools is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with tools.  If not, see <http://www.gnu.org/licenses/>.
 */

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsWithAnnotation;
import static org.apache.commons.collections4.CollectionUtils.forAllDo;
import static org.apache.commons.collections4.CollectionUtils.filter;
import static org.apache.commons.collections4.CollectionUtils.select;
import static org.apache.commons.collections4.ComparatorUtils.transformedComparator;
import static org.apache.commons.collections4.PredicateUtils.identityPredicate;
import static java.util.Collections.sort;

import cz.lidinsky.tools.functors.TransformedPredicate;

import java.io.InputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collection;
import java.util.NoSuchElementException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.FilterIterator;

/**
 *
 *  Uses a SAX parser to read given XML document. This class
 *  serves like a dispatcher which receives events from parser
 *  and hand them over to the custom handler object. It uses
 *  annotations to choose appropriate receiver method for a
 *  given event.
 *
 *  <p>The input XML document may contain parts that belongs
 *  to defferent grammers. To handle such a document, this
 *  class contains a stack for a handler classes. If the handler
 *  reach an element which is the root element of the foreign
 *  subtree, it must simply create an appropriate handler and
 *  the events will be dispatched into that handler as long as
 *  the end element of the subtree is reached. Than the control
 *  is return back to the parent handler.
 *
 */
public class XMLReader extends DefaultHandler
{

  /** Number of events that are recognized. */
  protected static final int EVENTS = 2;

  protected static final int START_ELEMENT_EVENT = 0;
  protected static final int END_ELEMENT_EVENT = 1;

  protected ArrayList<IXMLHandler> handlerObjects
			      = new ArrayList<IXMLHandler>();

  protected ArrayList<ArrayList<Triple<Expression, Method, IXMLHandler>>> handlers;

  /** Contains current element path. Each entry contains uri and element
      local name. */
  protected ArrayList<Pair<String, String>> elementStack
		      = new ArrayList<Pair<String, String>>();

  private ArrayList<Class<? extends Annotation>> annotationClasses;

  /**
   *  Initialize the internal data structures.
   */
  public XMLReader() {

    // Initialize the array of handlers
    handlers = new ArrayList<ArrayList<Triple<Expression, Method, IXMLHandler>>>();
    for (int i=0; i<EVENTS; i++) {
      handlers.add(new ArrayList<Triple<Expression, Method, IXMLHandler>>());
    }

    // Initialize element stack
    elementStack.add(Expression.ROOT);

    // Initialize the array of annotation classes
    annotationClasses = new ArrayList<Class<? extends Annotation>>(EVENTS);
    annotationClasses.add(AXMLStartElement.class);
    annotationClasses.add(AXMLEndElement.class);
  }


  /** Locator of the document */
  private Locator locator;

  private int line;
  private int column;

  /**
   *  Adds a handler object. Methods of this object are used to
   *  respond to the xml start element and xml end element events.
   *  Such methods must be annotated by XmlStartElement and 
   *  XmlEndElement annotations. A handler must be added before
   *  the load method is called.
   */
  public void addHandler(IXMLHandler handler)
  {

    handlerObjects.add(notNull(handler));

    // Get default uri for the handler
    String defaultUri = "";
    AXMLDefaultUri defUriAnno
	= handler.getClass().getAnnotation(AXMLDefaultUri.class);
    if (defUriAnno != null) {
      defaultUri = defUriAnno.value();
    }

    // Get all of the handler methods
    for (int i = 0; i < EVENTS; i++) {

      Class<? extends Annotation> annotationClass = annotationClasses.get(i);
      Method[] methods = getMethodsWithAnnotation(
	              handler.getClass(), annotationClass);
      for (Method method : methods) {
	Annotation annotation = method.getAnnotation(annotationClass);
	String annotationValue = getAnnotationValue(annotation);
	handlers.get(i).add(
	    new ImmutableTriple(
		new Expression().parse(annotationValue, defaultUri),
		method, handler
	    )
	);
      }

      // Sort the handler list
      sort(
	  handlers.get(i), transformedComparator(
	      (Comparator<Expression>)new Expression(), 
	      getGetLeftTransformer(Expression.class))
      );

    }
  }

  public void removeHandler(IXMLHandler handler) {

    handlerObjects.remove(notNull(handler));

    Transformer<Triple<?, ?, IXMLHandler>, IXMLHandler>
	getRightTransformer = getGetRightTransformer(IXMLHandler.class);

    for (int i = 0; i < EVENTS; i++) {
      filter(handlers.get(i), getTransformedPredicate(
	  identityPredicate(handler), getRightTransformer));
    }
  }

  protected static <I, O> Predicate<I> getTransformedPredicate(
	    final Predicate<O> predicate, final Transformer<I, O> transformer) {

    return new Predicate<I>() {
      public boolean evaluate(I object) {
	return predicate.evaluate(transformer.transform(object));
      }
    };
  }

  protected static <O> Transformer<Triple<O, ?, ?>, O>
      getGetLeftTransformer(Class<O> _class) {

    return new Transformer<Triple<O, ?, ?>, O>() {
      public O transform(Triple<O, ?, ?> triple) {
	return triple.getLeft();
      }
    };
  }

  protected static <O> Transformer<Triple<?, ?, O>, O>
      getGetRightTransformer(Class<O> _class) {

    return new Transformer<Triple<?, ?, O>, O>() {
      public O transform(Triple<?, ?, O> triple) {
	return triple.getRight();
      }
    };
  }

  private String getAnnotationValue(Annotation annotation) {
    if (annotation instanceof AXMLStartElement) {
      return ((AXMLStartElement)annotation).value();
    } else if (annotation instanceof AXMLEndElement) {
      return ((AXMLEndElement)annotation).value();
    } else if (annotation instanceof AXMLDefaultUri) {
      return ((AXMLDefaultUri)annotation).value();
    } else {
      throw new ClassCastException(); // TODO:
    }
  }

  /**
   *  Creates new SAX parser and reads the XML document from 
   *  the given input stream.
   *
   *  @param inputStream
   *             a stream from which the XML document is read
   *
   *  @throws IOException
   *             if something goes wrong
   */
  public void load(InputStream inputStream) throws IOException
  {
    try
    {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setXIncludeAware(true);
      SAXParser parser = factory.newSAXParser();
      parser.parse(inputStream, this);
    }
    catch (SAXException e)
    {
      throw new IOException(e);
    }
    catch (ParserConfigurationException e)
    {
      throw new IOException(e);
    }
  }

  protected Collection<Triple<Expression, Method, IXMLHandler>>
      findHandlerMethods(int event) {

    // Predicate to filter only handlers that are satisfied
    Predicate<Expression> evaluatePredicate
	= new Predicate<Expression>() {
      public boolean evaluate(Expression expression) {
	return expression.evaluate(elementStack);
      }
    };

    // get only expression from the triple
    Predicate<Triple<Expression, Method, IXMLHandler>> filter
	= new TransformedPredicate<Triple<Expression, ?, ?>, Expression>()
	.setTransformer(getGetLeftTransformer(Expression.class))
	.setPredicate(evaluatePredicate);

    return select(handlers.get(event), filter);
  }

  protected void callHandlerMethod(int event, Attributes attributes) {

    try {
      Collection<Triple<Expression, Method, IXMLHandler>> handlers
	          = findHandlerMethods(event);
      for (Triple<Expression, Method, IXMLHandler> handler : handlers) {
        boolean result
	    = (attributes != null)
	    ? (Boolean)handler.getMiddle().invoke(handler.getRight(), attributes)
	    : (Boolean)handler.getMiddle().invoke(handler.getRight());
	if (result) {
	  break;
	}
      }
    } catch (NoSuchElementException e) {
    } catch (IllegalAccessException e) {
    } catch (IllegalArgumentException e) {
    } catch (InvocationTargetException e) {
    }
  }

  /*
   *
   *   Overriden methods of the SAX Default Handler class.
   *   Respond methods to the SAX parser events.
   *
   */

  @Override
  public final void startElement(
      String uri, String localName, String qName, Attributes attributes)
      throws SAXException
  {
    // Store location

    // Store the element name and uri into the element stack
    elementStack.add(new ImmutablePair<String, String>(uri, localName));

    // Find and call appropriate handler method
    callHandlerMethod(START_ELEMENT_EVENT, attributes);
  }

  /**
   *  Gets end element from SAX parser, finds appropriate method and
   *  runs it. Do not override this method.
   */
  @Override
  public final void endElement(String uri, String localName, String qName)
  throws SAXException
  {
    // Store location

    // Find and call appropriate handler method
    callHandlerMethod(END_ELEMENT_EVENT, null);

    // Remove the element name and uri from the element stack
    elementStack.remove(elementStack.size() - 1);
  }

  /**
   *
   */
  @Override
  public final void startDocument()
  {
    forAllDo(handlerObjects,
      new Closure<IXMLHandler>() {
        public void execute(IXMLHandler handler) {
	  handler.startProcessing();
        }
      }
    );
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /**
   *  Does nothing.
   */
  @Override
  public final void endDocument()
  {
    //handlerStack.handler.endProcessing();
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /**
   *  Stores a locator for future use.
   */
  @Override
  public final void setDocumentLocator(Locator locator)
  {
    this.locator = locator;
  }

  /** A buffer to collect the whole text content of some element */
  private StringBuilder characterBuffer = new StringBuilder();

  /**
   *  Stores characters into the character buffer.
   */
  @Override
  public final void characters(char[] ch, int start, int length)
  {
    characterBuffer.append(ch, start, length);
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /**
   *  Does nothing, except, it stores a location.
   */
  @Override
  public final void ignorableWhitespace(char[] ch, int start, int length)
  {
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /**
   *  Log the fact, that the handler method for some XML event
   *  has not been found. It could mean, that eather the XML
   *  document contains something it should not, or it means,
   *  that the developer doesn't write the handler object
   *  properly.
   *
   *  @param event
   *             a text description of the event. For example:
   *             "start element", "end element", ...
   */
  protected void reportMissingHandler(String event)
  {
    Pair<String, String> element = elementStack.get(elementStack.size() - 1);
    String uri = element.getLeft();
    String name = element.getRight();
    System.err.println(java.text.MessageFormat.format(
        "Didn''t find any handler for the event: {0}.\n" +
        "Last start element: '{'{1}'}':{2},\n" +
        "on line: {3,number,integer}; column: {4,number,integer}; " +
        "public id: {5}; system id: {6}",
        "", uri, name, line, column,
        locator.getPublicId(), locator.getSystemId()));
    // TODO:
  }

  public static void main(String[] args) throws Exception {
    java.io.InputStream is = new java.io.FileInputStream(args[0]);
    XMLReader reader = new XMLReader();
    reader.load(is);
    is.close();
  }

}
