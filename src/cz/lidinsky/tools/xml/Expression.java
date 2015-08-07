package cz.lidinsky.tools.xml;

/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of java tools library.
 *
 *  java tools is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  java tools library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with java tools library.  If not, see <http://www.gnu.org/licenses/>.
 */

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.text.StrMatcher.charMatcher;
import static org.apache.commons.lang3.text.StrMatcher.charSetMatcher;
import static org.apache.commons.lang3.text.StrMatcher.singleQuoteMatcher;
import static org.apache.commons.collections4.IteratorUtils.asIterable;
import static org.apache.commons.collections4.IteratorUtils.transformedIterator;

import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Expression implements Comparator<Expression> {

  public static final Pair<String, String> ROOT
      = new ImmutablePair<String, String>("", "");

  /** The pair contains uri and local name of the element */
  protected ArrayList<Pair<String, String>> path
      = new ArrayList<Pair<String, String>>();

  public Expression parse(String expression, String defaultUri) {
    //notNull(expression);
    expression = expression.replace("{", "`");
    expression = expression.replace("}", "}`");
    StrTokenizer tokens
        = new StrTokenizer(expression, charMatcher('/'), charMatcher('`'));
    tokens.setIgnoreEmptyTokens(false);
    tokens.setEmptyTokenAsNull(true);
    for (String token : asIterable(tokens)) {
      if (token == null) {
        path.add(null);
      } else {
        path.add(parseUriElementName(token, defaultUri));
      }
    }
    if (path.get(0) == null) {
      path.set(0, ROOT);
    }
    return this;
  }

  protected Pair<String, String> parseUriElementName(
                      String expression, String defaultUri) {

    String uri = defaultUri;
    String localName;
    expression = expression.replace("{", "");
    int index = expression.indexOf('}');
    if (index >= 0) {
      uri = expression.substring(0, index);
      localName = expression.substring(index + 1);
    } else {
      localName = expression;
    }
    return new ImmutablePair<String, String>(uri, localName);
  }

  public boolean evaluate(List<Pair<String, String>> context) {
    int pathIndex = path.size() - 1;
    int contextIndex = context.size() - 1;
    return evaluate(context, pathIndex, contextIndex);
  }

  protected boolean evaluate(
      List<Pair<String, String>> context, int pathIndex, int contextIndex) {

    while (pathIndex >= 0 && contextIndex >= 0) {
      if (path.get(pathIndex) != null) {
        if (equals(path.get(pathIndex), context.get(contextIndex))) {
          pathIndex--;
          contextIndex--;
        } else {
          return false;
        }
      } else {
        while (contextIndex >= 0) {
          if (evaluate(context, pathIndex - 1, contextIndex)) {
            return true;
          } else {
            contextIndex--;
          }
        }
      }
    }

    return pathIndex < 0;
  }

  protected boolean equals(
      Pair<String, String> path, Pair<String, String> context) {

    String pathUri = path.getLeft();
    String pathName = path.getRight();
    String contextUri = context.getLeft();
    String contextName = context.getRight();
    if (path == context) {
      return true;
    } else if (pathName.equals("*") && pathUri.equals("*")) {
      return true;
    } else if (pathName.equals("*") && pathUri.equals(contextUri)) {
      return true;
    } else if (pathName.equals(contextName) && pathUri.equals("*")) {
      return true;
    } else if (pathName.equals(contextName) && pathUri.equals(contextUri)) {
      return true;
    } else {
      return false;
    }
  }

  public static void main(String[] args) {

    String expression = args[0];
    expression = expression.replace("{", "`");
    expression = expression.replace("}", "}`");
    System.out.println(expression);
    StrTokenizer tokens
        = new StrTokenizer(expression, charMatcher('/'), charMatcher('`'));
    tokens.setIgnoreEmptyTokens(false);
    for (String token : asIterable(tokens)) {
      System.out.println(token);
    }

  }

  public int compare(Expression e1, Expression e2) {
    int index1 = e1.path.size() - 1;
    int index2 = e2.path.size() - 1;
    while (index1 >= 0 && index2 >= 0) {
      int rank1 = getRank(e1.path.get(index1));
      int rank2 = getRank(e2.path.get(index2));
      if (rank1 == rank2) {
        index1--;
        index2--;
      } else {
        return rank1 - rank2;
      }
    }
    return index1 - index2;
  }

  private int getRank(Pair<String, String> element) {
    return element == null || element.getRight().equals("*") ? 0 : 1;
  }

  @Override
  public String toString() {
    return join(
        transformedIterator(
            path.iterator(),
            new Transformer<Pair<String, String>, String>() {
              public String transform(Pair<String, String> pathElement) {
                return pathElementToString(pathElement);
              }
            }
        ), '/');
  }

  protected String pathElementToString(Pair<String, String> element) {
    if (element == null) {
      return "";
    } else if (element == ROOT) {
      return "";
    } else {
      return new StringBuilder()
        .append('{')
        .append(element.getLeft())
        .append('}')
        .append(element.getRight())
        .toString();
    }
  }

}
