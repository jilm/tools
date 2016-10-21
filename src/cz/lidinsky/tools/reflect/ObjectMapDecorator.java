package cz.lidinsky.tools.reflect;

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

import static cz.lidinsky.tools.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.apache.commons.lang3.StringUtils.right;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.collections4.CollectionUtils.addAll;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;

/**
 *
 *  It allows to access the fields and methods of the decorated object
 *  like a map. New keys cannot be, of course, added to the map.
 *  This object must be adjusted correctly before use.
 *
 *  <p>First of all the filters which choose only the
 *
 *  <p>Than assign a decorated object.
 *
 */
public class ObjectMapDecorator<T> implements Map<String, T> {

  /** The Object which serves like a back-end storadge of the map. */
  protected Object decorated;

  /** An internal buffer to store methods and fields of the object. */
  protected HashMap<String, BufferEntry> buffer
                   = new HashMap<String, BufferEntry>();

  public ObjectMapDecorator(Class<T> valueClass) {
    //this.valueClass = valueClass;
    getterFilter = ObjectMapUtils.getGetterSignatureCheckPredicate();
    setterFilter = ObjectMapUtils.getSetterSignatureCheckPredicate();
    setSetterFactory(null);
    setGetterFactory(null);
    setGetterKeyTransformer(null);
    setSetterKeyTransformer(null);
  }

  //--------------------------------------------------------- Decorated Object.

  /**
   *  Returns the decorated object.
   */
  public Object getDecorated() {
    return decorated;
  }

  /**
   *  Sets the decorated object.
   *
   *  @param object
   *             object whose methods and fields will be accessible through
   *             the map interface. May be <code>null</code> value.
   */
  public void setDecorated(Object object) {
    if (object != this.decorated) {
      this.decorated = object;
      dirty = true;
    }
  }

  //--------------------------------------------------------------- Parameters.

  /**
   *  Something has been changed and the decorated object should be
   *  rescanned.
   */
  private boolean dirty = true;

  /**
   *  Predicate which is used to select setter fields and methods of the
   *  decorated object.
   */
  private Predicate<? super AccessibleObject> setterFilter;

  /**
   *  Sets the setter filter. It is the Predicate which is used to
   *  select the appropriate members of the decorated object which
   *  are than used to set values. Only members for which the predicate
   *  is true are used as the setters.
   *
   *  <p>After this method is used, the object is re-scaned (map
   *  is actualized).
   *
   *  <p>If the parameter is <code>null</code>, the predicate which
   *  returnes always false is used instead.
   */
  public ObjectMapDecorator setSetterFilter(
      Predicate<? super AccessibleObject> filter) {

    if (filter == null) {
      setterFilter = PredicateUtils.falsePredicate();
    } else {
      setterFilter = filter;
    }
    dirty = true;
    return this;
  }

  /**
   *  Predicate which is used to select getter fields and methods of the
   *  decorated object.
   */
  private Predicate<? super AccessibleObject> getterFilter;

  public ObjectMapDecorator setGetterFilter(
      Predicate<? super AccessibleObject> filter) {

    if (filter == null) {
      getterFilter = PredicateUtils.falsePredicate();
    } else {
      getterFilter = filter;
    }
    dirty = true;
    return this;
  }

  /**
   *  A Transformer (factory), which returns appropriate setter closure
   *  for each setter method of the decorated object.
   */
  private Transformer<Pair<Object, AccessibleObject>, Closure<T>> setterFactory;

  /**
   *  Sets a setter factory. It is the transformer which returns an
   *  appropriate closure for each setter method of the decorated
   *  object.
   *
   *  <p>After the new factory is set, the map is actualized.
   */
  public ObjectMapDecorator setSetterFactory(
      Transformer<Pair<Object, AccessibleObject>, Closure<T>> factory) {

    if (factory == null) {
      setterFactory = ObjectMapUtils.setterClosureFactory(false);
    } else {
      setterFactory = factory;
    }
    dirty = true;
    return this;
  }

  private Transformer<Pair<Object, AccessibleObject>, Factory<T>> getterFactory;

  public ObjectMapDecorator setGetterFactory(
      Transformer<Pair<Object, AccessibleObject>, Factory<T>> factory) {

    if (factory == null) {
      getterFactory = ObjectMapUtils.getterFactory(false);
    } else {
      getterFactory = factory;
    }
    dirty = true;
    return this;
  }

  private Transformer<AccessibleObject, String> getterKeyTransformer;

  public ObjectMapDecorator setGetterKeyTransformer(
      Transformer<AccessibleObject, String> keyTransformer) {

    if (keyTransformer == null) {
      getterKeyTransformer = ObjectMapUtils.keyFromName();
    } else {
      getterKeyTransformer = keyTransformer;
    }
    dirty = true;
    return this;
  }

  private Transformer<AccessibleObject, String> setterKeyTransformer;

  public ObjectMapDecorator setSetterKeyTransformer(
      Transformer<AccessibleObject, String> keyTransformer) {

    if (keyTransformer == null) {
      setterKeyTransformer = ObjectMapUtils.keyFromName();
    } else {
      setterKeyTransformer = keyTransformer;
    }
    dirty = true;
    return this;
  }

  //--------------------------------------------  Map Interface Implementation.

  public int size() {
    scan();
    return buffer.size();
  }

  public boolean isEmpty() {
    scan();
    return buffer.isEmpty();
  }

  public boolean containsKey(Object key) {
    scan();
    return buffer.containsKey(key);
  }

  public boolean containsValue(Object value) {
    // TODO:
    return false;
  }

  public T get(Object key) {
    scan();
    BufferEntry entry = buffer.get(key);
    if (entry == null) {
      return null;
    } else if (!entry.isReadable()) {
      return null;
    } else {
      return entry.getValue();
    }
  }

  public T put(String key, T value) {
    try {
      scan();
      BufferEntry entry = buffer.get(key);
      if (entry == null) {
        throw new CommonException()
          .setCode(ExceptionCode.NO_SUCH_ELEMENT)
          .set("message", "An object doesn't have a member with given key!");
      } else if (!entry.isWritable()) {
        throw new CommonException()
          .setCode(ExceptionCode.ACCESS_DENIED)
          .set("message",
              "You are trying to write the key that is not writable!");
      } else {
        T oldValue = entry.isReadable() ? entry.getValue() : entry.value;
        entry.setValue(value);
        return oldValue;
      }
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message",
            "An exception while setting the item of the object map!")
        .set("key", key)
        .set("value", value)
        .set("object", decorated);
    }
  }

  /**
   *  Not supported operation.
   */
  public T remove(Object key) {
    throw new UnsupportedOperationException();
  }

  public void putAll(Map<? extends String, ? extends T> m) {
    throw new UnsupportedOperationException();
    // TODO:
  }

  /**
   *  Not supported operation.
   */
  public void clear() {
    throw new UnsupportedOperationException();
  }

  public Set<String> keySet() {
    scan();
    return buffer.keySet();
  }

  public Collection<T> values() {
    // TODO:
    return null;
  }

  public Set<Map.Entry<String, T>> entrySet() {
    // TODO:
    return null;
  }

  public boolean equals(Object object) {
    // TODO:
    return false;
  }

  public int hashCode() {
    // TODO:
    return 0;
  }

  //-------------------------------------------------------------------- Other.

  public boolean isWritable(String key) {
    scan();
    BufferEntry entry = buffer.get(key);
    if (entry != null) {
      return entry.isWritable();
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.NO_SUCH_ELEMENT)
        .set("message", "There is no item with required key!")
        .set("key", key)
        .set("object", getDecorated());
    }
  }

  public boolean isReadable(String key) {
    scan();
    BufferEntry entry = buffer.get(key);
    if (entry != null) {
      return entry.isReadable();
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.NO_SUCH_ELEMENT)
        .set("message", "There is no item with required key!")
        .set("key", key)
        .set("object", getDecorated());
    }
  }

  public Class<?> getDataType(String key) {
    scan();
    BufferEntry entry = buffer.get(key);
    if (entry != null) {
      return entry.dataType;
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.NO_SUCH_ELEMENT)
        .set("message", "There is no item with required key!")
        .set("key", key)
        .set("object", getDecorated());
    }
  }

  public Closure<T> getSetter(String key) {
    scan();
    BufferEntry entry = buffer.get(key);
    if (entry != null) {
      return entry.setter;
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.NO_SUCH_ELEMENT)
        .set("message", "There is no item with required key!")
        .set("key", key)
        .set("object", getDecorated());
    }
  }

  //---------------------------------------------------------- Internal Buffer.

  protected class BufferEntry {

    Factory<T> getter;
    Closure<T> setter;
    Class<?> dataType;
    T value;

    boolean isReadable() {
      return getter != null;
    }

    boolean isWritable() {
      return setter != null;
    }

    T getValue() {
      if (getter == null) {
        throw new CommonException()
          .setCode(ExceptionCode.ACCESS_DENIED)
          .set("message", "Value is not Readable!");
      } else {
        return getter.create();
      }
    }

    void setValue(T value) {
      if (setter == null) {
        throw new CommonException()
          .setCode(ExceptionCode.ACCESS_DENIED)
          .set("message", "Value is not writable!");
      } else {
        setter.execute(value);
        this.value = value;
      }
    }

  }

  /**
   *  Scannes the given class and fills the buffer.
   */
  protected void scan() {

    // Scan the object only if it is neccessary.
    if (!dirty) return;

    buffer.clear();

    // If there is nothing to scan.
    if (decorated == null) return;

    // collection of all of the object members
    Collection<? extends AccessibleObject> members = CollectionUtils.union(
        ObjectMapUtils.getMethods(decorated.getClass()),
        ObjectMapUtils.getFields(decorated.getClass()));

    // select only setter members and place them into the internal buffer
    List<AccessibleObject> setters = ListUtils.select(members, setterFilter);
    Collections.sort(setters, ObjectMapUtils.getComparator());
    System.out.println("setters are: ");
    for (AccessibleObject setter : setters) {
      String key = setterKeyTransformer.transform(setter);
      System.out.print(key + " ");
      BufferEntry entry = getBufferEntry(key);
      entry.setter = setterFactory.transform(
          new ImmutablePair(decorated, setter));
      entry.dataType = ObjectMapUtils.getValueDataType(setter);
      System.out.println();
    }

    // select only getter members and place them into the internal buffer
    List<AccessibleObject> getters = ListUtils.select(members, getterFilter);
    Collections.sort(getters, ObjectMapUtils.getComparator());
    for (AccessibleObject getter : getters) {
      String key = getterKeyTransformer.transform(getter);
      BufferEntry entry = getBufferEntry(key);
      entry.getter = getterFactory.transform(
          new ImmutablePair(decorated, getter));
      entry.dataType = ObjectMapUtils.getValueDataType(getter);
    }

    dirty = false;

  }

  //-------------------------------------------------------- Auxiliary Methods.

  private BufferEntry getBufferEntry(final String key) {
    BufferEntry entry = buffer.get(key);
    if (entry != null) {
      return entry;
    } else {
      entry = new BufferEntry();
      buffer.put(key, entry);
      return entry;
    }
  }

}
