/*
 *  Copyright 2015, 2016 Jiri Lidinsky
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

package cz.lidinsky.tools.xml;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.sort;
import java.util.Comparator;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.collections4.Closure;
import static org.apache.commons.collections4.CollectionUtils.filter;
import static org.apache.commons.collections4.CollectionUtils.forAllDo;
import static org.apache.commons.collections4.CollectionUtils.select;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Predicate;
import static org.apache.commons.collections4.PredicateUtils.identityPredicate;
import org.apache.commons.collections4.Transformer;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsWithAnnotation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 *  Uses a SAX parser to read given XML document. This class serves like a
 *  dispatcher which receives events from the parser and hand them over to the
 *  custom handler objects. It uses annotations to choose appropriate receiver
 *  method for a given event.
 *
 */
public class XMLReader extends DefaultHandler {

  /** Number of events that are recognized. */
  protected static final int EVENTS = 3;

  /** A code for the start element event. */
  protected static final int START_ELEMENT_EVENT = 0;

  /** A code for the end element event. */
  protected static final int END_ELEMENT_EVENT = 1;

  /** A code for the text content of the element event. */
  protected static final int TEXT_EVENT = 2;

  /** Annotation classes that are used to annotate particular handler
      methods. */
  private ArrayList<Class<? extends Annotation>> annotationClasses;

  /** Labels for events, serves mainly for error reports. */
  protected static final String[] EVENT_LABELS
                         = {"Start element", "End element", "Text content"};

  /** Handler objects. */
  protected ArrayList<IXMLHandler> handlerObjects
        		                     = new ArrayList<>();

  /** Handler methods for events. */
  protected ArrayList<ArrayList<Triple<Expression, Method, IXMLHandler>>>
        							   handlers;

  /** Contains current element path. Each entry contains uri and element
      local name. */
  protected ArrayList<Pair<String, String>> elementStack
        	                    = new ArrayList<>();

  public static final Logger logger = Logger.getLogger("cz.lidinsky"); // TODO:

  /**
   *  Initialize the internal data structures.
   */
  public XMLReader() {

    // Initialize the array of handlers
    handlers
       = new ArrayList<ArrayList<Triple<Expression, Method, IXMLHandler>>>();
    for (int i=0; i<EVENTS; i++) {
      handlers.add(new ArrayList<Triple<Expression, Method, IXMLHandler>>());
    }

    // Initialize element stack
    elementStack.add(Expression.ROOT);

    // Initialize the array of annotation classes
    annotationClasses = new ArrayList<Class<? extends Annotation>>(EVENTS);
    annotationClasses.add(AXMLStartElement.class);
    annotationClasses.add(AXMLEndElement.class);
    annotationClasses.add(AXMLText.class);
  }

  /*
   *
   *    Locator methods.
   *
   */

  /** Locator of the document */
  private Locator locator;

  /** Actual location. */
  private int line1 = 1;
  private int column1 = 1;
  private int line2 = 1;
  private int column2 = 1;
  private String publicId;
  private String systemId;

  /**
   *  Store actual document location.
   */
  protected void storeLocation() {
    line1 = line2;
    column1 = column2;
    line2 = locator.getLineNumber();
    column2 = locator.getColumnNumber();
    publicId = locator.getPublicId();
    systemId = locator.getSystemId();
  }

  /**
   *  Returns current document location in the human readable form.
   * @return
   */
  public String getLocation() {
    StringBuilder sb = new StringBuilder();
    if (line1 == line2) {
      sb.append("On line: ")
        .append(line1)
        .append("; ");
      if (column1 == column2) {
        sb.append("on column: ")
          .append(column1);
      } else {
        sb.append("somewhere between columns: ")
          .append(column1)
          .append(" and ")
          .append(column2);
      }
    } else {
      sb.append("Somewhere between lines: ")
        .append(line1)
        .append(" and ")
        .append(line2);
    }
    sb.append('.');
    if (publicId != null) {
      sb.append("Public id: ")
        .append(publicId)
        .append(".");
    }
    if (systemId != null) {
      sb.append("System id: ")
        .append(systemId)
        .append(".");
    }
    return sb.toString();
  }

  /**
   *  Adds the handler object. Methods of this object are used to
   *  respond to the xml start element and xml end element events.
   *  Such methods must be annotated by AXMLStartElement and
   *  AXMLEndElement annotations.
   * @param handler
   */
  public void addHandler(IXMLHandler handler) {

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
        //System.out.println(annotationValue);
        handlers.get(i).add(
            new ImmutableTriple(
        	new Expression().parse(annotationValue, defaultUri),
        	method, handler
            )
        );
      }

      // Sort the handler list
      sort(
          handlers.get(i),
          ComparatorUtils.reversedComparator(
            ComparatorUtils.transformedComparator(
              (Comparator<Expression>)new Expression(),
              getGetLeftTransformer(Expression.class))));

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
    } else if (annotation instanceof AXMLText) {
      return ((AXMLText)annotation).value();
    } else {
      // Should not happen
      throw new AssertionError();
    }
  }

    public File getFile() {
        return file;
    }

    private File file;
  public void load(File file) {
    try {
        this.file = file;
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setXIncludeAware(true);
      SAXParser parser = factory.newSAXParser();
      handlerObjects.stream().forEach(p -> p.setXMLReader(this));
      parser.parse(file, this);
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "Exception while reading a XML file!")
        .set("file", file)
        .set("line1", line1)
        .set("column1", column1)
        .set("line2", line2)
        .set("column2", column2)
        .set("public id", publicId)
        .set("system id", systemId);
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

        Method handlerMethod = null;
        try {
            boolean processed = false; // indicate that the event was processed
            Collection<Triple<Expression, Method, IXMLHandler>> handlers
                    = findHandlerMethods(event);
            for (Triple<Expression, Method, IXMLHandler> handler : handlers) {
                handlerMethod = handler.getMiddle();
                switch (event) {
                    case START_ELEMENT_EVENT:
                        processed = (Boolean) handlerMethod.invoke(
                                handler.getRight(), attributes);
                        break;
                    case END_ELEMENT_EVENT:
                        processed = (Boolean) handlerMethod.invoke(handler.getRight());
                        break;
                    case TEXT_EVENT:
                        processed = (Boolean) handlerMethod.invoke(
                                handler.getRight(), characterBuffer.toString());
                        break;
                }
                if (processed) {
                    return;
                }
            }
            reportMissingHandler(EVENT_LABELS[event]);
            Exception e = new CommonException()
                    .setCode(ExceptionCode.NO_SUCH_ELEMENT)
                    .set("message", "Missing handler!")
                    .set("handlers", handlers)
                    .set("elementStack", elementStack);
            System.err.println(e.toString());
            throw e;
        } catch (Exception e) {
            CommonException ex = new CommonException()
                    .setCause(e)
                    .set("message", "Handler method invocation failed!")
                    .set("event", event)
                    .set("handler method", handlerMethod)
                    .set("elementStack", elementStack);
            System.err.println(ex.toString()); // TODO:
            throw ex;
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
      String uri, String localName, String qName, Attributes attributes) {

    try {

      // Store location
      storeLocation();

      // If there is a content in the text buffer
      fireTextEvent();

      // Store the element name and uri into the element stack
      elementStack.add(new ImmutablePair<String, String>(uri, localName));

      // Find and call appropriate handler method
      callHandlerMethod(START_ELEMENT_EVENT, attributes);

    } catch (Exception e) {
      CommonException ex = new CommonException()
        .setCause(e)
        .set("message", "Exception while processing Start Element event!")
        .set("uri", uri)
        .set("local name", localName)
        .set("qName", qName)
        .set("attributes", attributes);
      logger.severe(ex.toString());
      throw ex;
    }
  }

  /**
   *  Gets end element from SAX parser, finds appropriate method and
   *  runs it. Do not override this method.
   */
  @Override
  public final void endElement(String uri, String localName, String qName)
  throws SAXException {

    // Store location
    storeLocation();

    // If there is a content in the text buffer
    fireTextEvent();

    // Find and call appropriate handler method
    try {
      callHandlerMethod(END_ELEMENT_EVENT, null);
    } catch (Exception e) {
      logger.severe(e.toString());
      throw new SAXException(e);
    }

    // Remove the element name and uri from the element stack
    elementStack.remove(elementStack.size() - 1);
  }

  /**
   *
   */
  @Override
  public final void startDocument() {
    storeLocation();
    forAllDo(handlerObjects,
      new Closure<IXMLHandler>() {
        public void execute(IXMLHandler handler) {
          handler.startProcessing();
        }
      }
    );
  }

  /**
   *  Does nothing.
   */
  @Override
  public final void endDocument() {
    //handlerStack.handler.endProcessing();
    storeLocation();
    forAllDo(handlerObjects,
        new Closure<IXMLHandler>() {
          public void execute(IXMLHandler handler) {
            handler.endProcessing();
          }
        }
      );
    }

  /**
   *  Stores a locator for future use.
   */
  @Override
  public final void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  /** A buffer to collect the whole text content of some element */
  private StringBuilder characterBuffer = new StringBuilder();

  /**
   *  Stores characters into the character buffer.
   */
  @Override
  public final void characters(char[] ch, int start, int length) {
    characterBuffer.append(ch, start, length);
    storeLocation();
  }

  protected void fireTextEvent() throws SAXException {
    if (!isBlank(characterBuffer)) {
      try {
        callHandlerMethod(TEXT_EVENT, null);
      } catch (Exception e) {
        reportException(e);
        throw new SAXException(e);
      } finally {
        characterBuffer.delete(0, characterBuffer.length());
      }
    }
  }

  /**
   *  Does nothing, except, it stores a location.
   */
  @Override
  public final void ignorableWhitespace(char[] ch, int start, int length) {
    storeLocation();
  }

  @Override
  public final void error(SAXParseException e) throws SAXException {
    super.error(e);
    reportException(e);
  }

  @Override
  public final void fatalError(SAXParseException e) throws SAXException {
    reportException(e);
    super.fatalError(e);
  }

  @Override
  public final void warning(SAXParseException e) throws SAXException {
    super.warning(e);
    reportException(e);
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
  protected void reportMissingHandler(String event) {
    StringBuilder sb = new StringBuilder();
    sb.append("Didn''t find any handler for the event: ")
      .append(event)
      .append("\n")
      .append(getLocation());
    printElementStack(sb);
    System.err.println(sb.toString());
  }

  protected void printElementStack(StringBuilder sb) {
    String delimiter = "";
    for (Pair<String, String> element : elementStack) {
      sb.append(delimiter)
        .append('{')
        .append(element.getLeft())
        .append('}')
        .append(element.getRight());
      delimiter = "/";
    }
  }

  protected void reportException(Exception e) {
    StringBuilder sb = new StringBuilder();
    sb.append("Exception while reading a XML file!\n")
      .append(e.getClass().getName())
      .append("\n")
      .append(e.getMessage())
      .append("\n")
      .append(getLocation())
      .append("\n");
    printElementStack(sb);
    System.err.println(sb.toString());
  }

}
