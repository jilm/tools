package cz.lidinsky.tools.functors;

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

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.FunctorException;

public class TransformedPredicate<I, O> implements Predicate<I> {

  private Transformer<I, O> transformer;
  private Predicate<O> predicate;

  public TransformedPredicate(
      Transformer<I, O> transformer, Predicate<O> predicate) {

    this.transformer = transformer;
    this.predicate = predicate;
  }

  public TransformedPredicate() {}

  public boolean evaluate(I object) {
    if (transformer == null || predicate == null) {
      throw new FunctorException();
    }
    return predicate.evaluate(transformer.transform(object));
  }

  public Predicate<O> getPredicate() {
    return predicate;
  }

  public Transformer<I, O> getTransformer() {
    return transformer;
  }

  public TransformedPredicate setPredicate(Predicate<O> predicate) {
    this.predicate = predicate;
    return this;
  }

  public TransformedPredicate setTransformer(Transformer<I, O> transformer) {
    this.transformer = transformer;
    return this;
  }

}
