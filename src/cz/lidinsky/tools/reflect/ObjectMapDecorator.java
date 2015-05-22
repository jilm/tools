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

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.apache.commons.lang3.StringUtils.right;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.collections4.CollectionUtils.addAll;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.ListUtils;

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

public class ObjectMapDecorator<T> implements Map<String, T> {

  protected Object decorated;

  protected HashMap<String, BufferEntry> buffer;

  protected Set<Class<? extends Annotation>> getterAnnotations;
  protected Set<Class<? extends Annotation>> setterAnnotations;

  protected Class<T> valueClass;

  protected Predicate<AccessibleObject> getterFilter;
  protected Predicate<AccessibleObject> setterFilter;
  protected Transformer<AccessibleObject, Closure<T>> setterFactory;

  public ObjectMapDecorator(Class<T> valueClass) {
    this.valueClass = valueClass;
    getterAnnotations = new HashSet<Class<? extends Annotation>>();
    getterAnnotations.add(Getter.class);
    setterAnnotations = new HashSet<Class<? extends Annotation>>();
    setterAnnotations.add(Setter.class);
    getterFilter = getDefaultGetterFilter();
    setterFilter = getDefaultSetterFilter();
  }

  public Object getDecorated() {
    return decorated;
  }

  public void setDecorated(Object object) {
    this.decorated = notNull(object);
    setterFactory = getSetterClosureFactory(object, false);
    scan();
  }

  public void setDecorated(Object object,
      Transformer<AccessibleObject, Closure<T>> setterFactory) {

    this.decorated = notNull(object);
    this.setterFactory = notNull(setterFactory);
    scan();
  }

  /*
   *
   *     Map interface implementation.
   *
   */

  public int size() {
    return buffer.size();
  }

  public boolean isEmpty() {
    return buffer.isEmpty();
  }

  public boolean containsKey(Object key) {
    return buffer.containsKey(key);
  }

  public boolean containsValue(Object value) {
    // TODO:
    return false;
  }

  public T get(Object key) {
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
    BufferEntry entry = buffer.get(key);
    if (entry == null) {
      throw new IllegalArgumentException();
    } else if (!entry.isWritable()) {
      throw new IllegalArgumentException();
    } else {
      T oldValue = entry.getValue();
      entry.setValue(value);
      return oldValue;
    }
  }

  public T remove(Object key) {
    throw new UnsupportedOperationException();
  }

  public void putAll(Map<? extends String, ? extends T> m) {
    throw new UnsupportedOperationException();
    // TODO:
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public Set<String> keySet() {
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

  protected class BufferEntry {

    Factory<T> getter;
    Closure<T> setter;

    boolean isReadable() {
      return getter != null;
    }

    boolean isWritable() {
      return setter != null;
    }

    T getValue() {
      return getter.create();
    }

    void setValue(T value) {
      setter.execute(value);
    }

  }

  /**
   *  Scannes the given class and fills the buffer.
   */
  protected void scan() {

    buffer.clear();

    // collection of all of the object members
    Collection<? extends AccessibleObject> members = CollectionUtils.union(
        getMethods(decorated.getClass()), getFields(decorated.getClass()));

    List<AccessibleObject> setters = ListUtils.select(members, setterFilter);
    Collections.sort(setters, getComparator());
    for (AccessibleObject setter : setters) {
      String key = getSetterKey(setter);
      BufferEntry entry = getBufferEntry(key);
      entry.setter = setterFactory.transform(setter);
    }

    List<AccessibleObject> getters = ListUtils.select(members, getterFilter);
    Collections.sort(getters, getComparator());
    for (AccessibleObject getter : getters) {
      String key = getGetterKey(getter);
      BufferEntry entry = getBufferEntry(key);
      entry.getter = getGetterFactory(decorated, getter, true);
    }

  }

  protected Predicate<AccessibleObject> getDefaultGetterFilter() {
    return PredicateUtils.allPredicate(
	getGetterSignatureCheckPredicate(),
	getGetterDataTypeCheckPredicate(valueClass));
  }

  protected Predicate<AccessibleObject> getDefaultSetterFilter() {
    return PredicateUtils.allPredicate(
	getSetterSignatureCheckPredicate(),
	getSetterDataTypeCheckPredicate(valueClass));
  }

  protected BufferEntry getBufferEntry(final String key) {
    BufferEntry entry = buffer.get(key);
    if (entry != null) {
      return entry;
    } else {
      entry = new BufferEntry();
      buffer.put(key, entry);
      return entry;
    }
  }

  /**
   *  Returns methods of the given class.
   */
  public static Collection<Method> getMethods(final Class objectClass) {
    Method[] methods = objectClass.getMethods();
    HashSet<Method> set = new HashSet<Method>();
    addAll(set, methods);
    methods = objectClass.getDeclaredMethods();
    addAll(set, methods);
    return set;
  }

  public static Collection<Field> getFields(final Class objectClass) {
    Field[] fields = objectClass.getFields();
    HashSet<Field> set = new HashSet<Field>();
    addAll(set, fields);
    fields = objectClass.getDeclaredFields();
    addAll(set, fields);
    return set;
  }

  public static Predicate<AccessibleObject> getGetterDataTypeCheckPredicate(
      final Class dataType) {

    notNull(dataType);
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(AccessibleObject object) {
	Class objectDataType;
	if (object instanceof Field) {
	  objectDataType = ((Field)object).getType();
	} else if (object instanceof Method) {
	  objectDataType = ((Method)object).getReturnType();
	} else {
	  throw new IllegalArgumentException(); // TODO:
	}
	return dataType.isAssignableFrom(objectDataType);
      }
    };
  }

  public static Predicate<AccessibleObject> getSetterDataTypeCheckPredicate(
      final Class dataType) {

    notNull(dataType);
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(final AccessibleObject object) {
	Class objectDataType;
	if (object instanceof Field) {
	  objectDataType = ((Field)object).getType();
	} else if (object instanceof Method) {
	  objectDataType = ((Method)object).getParameterTypes()[0];
	} else {
	  throw new IllegalArgumentException(); // TODO:
	}
	return objectDataType.isAssignableFrom(dataType);
      }
    };
  }

  public static Predicate<AnnotatedElement> getAnnotationPredicate(
      final Class<? extends Annotation> anno) {

    notNull(anno);
    return new Predicate<AnnotatedElement>() {
      public boolean evaluate(final AnnotatedElement object) {
	notNull(object);
	return object.isAnnotationPresent(anno);
      }
    };
  }

  protected static boolean hasGetterSignature(final AccessibleObject object) {
    notNull(object);
    if (object instanceof Field) {
      return true;
    } else if (object instanceof Method) {
      int args = ((Method)object).getParameterTypes().length;
      return args == 0 && ((Method)object).getReturnType() != Void.TYPE;
    } else {
      throw new IllegalArgumentException(); // TODO: or return false?
    }
  }

  public static Predicate<AccessibleObject> getGetterSignatureCheckPredicate() {
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(AccessibleObject object) {
	return hasGetterSignature(object);
      }
    };
  }

  public static boolean hasSetterSignature(final AccessibleObject object) {
    notNull(object);
    if (object instanceof Field) {
      return true;
    } else if (object instanceof Method) {
      int args = ((Method)object).getParameterTypes().length;
      return args == 1 && ((Method)object).getReturnType() == Void.TYPE;
    } else {
      throw new IllegalArgumentException(); // TODO: or return false?
    }
  }

  public static Predicate<AccessibleObject> getSetterSignatureCheckPredicate() {
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(AccessibleObject object) {
	return hasSetterSignature(object);
      }
    };
  }

  public static String getGetterKey(final AccessibleObject object) {
    notNull(object);
    // if there is a getter annotation take the annotation value
    Getter anno = object.getAnnotation(Getter.class);
    if (anno != null) {
      String key = anno.value();
      if (!isBlank(key)) return key;
    }
    return getKeyFromName(object);
  }

  public static Transformer<AccessibleObject, String>
      getGetterKeyTransformer() {

    return new Transformer<AccessibleObject, String>() {
      public String transform(AccessibleObject object) {
	return getGetterKey(object);
      }
    };
  }

  public static Predicate<AccessibleObject> hasGetterKeyPredicate(
      final String key) {

    notBlank(key);
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(final AccessibleObject member) {
	return getGetterKey(member).equals(key);
      }
    };
  }

  public static String getSetterKey(final AccessibleObject object) {
    notNull(object);
    // if there is a setter annotation take the annotation value
    Setter anno = object.getAnnotation(Setter.class);
    if (anno != null) {
      String key = anno.value();
      if (!isBlank(key)) return key;
    }
    return getKeyFromName(object);
  }

  public static Transformer<AccessibleObject, String>
      getSetterKeyTransformer() {

    return new Transformer<AccessibleObject, String>() {
      public String transform(AccessibleObject object) {
	return getSetterKey(object);
      }
    };
  }

  public static Predicate<AccessibleObject> hasSetterKeyPredicate(
      final String key) {

    notBlank(key);
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(final AccessibleObject member) {
	return getSetterKey(member).equals(key);
      }
    };
  }

  public static String getKeyFromName(final AccessibleObject object) {
    if (object instanceof Field) {
      return ((Field)object).getName();
    } else if (object instanceof Method) {
      String key = ((Method)object).getName();
      if ((startsWith(key, "get") || startsWith(key, "set"))
	  && key.length() > 3) {
        key = right(key, key.length() - 3);
        key = uncapitalize(key);
      }
      return key;
    } else {
      throw new IllegalArgumentException(); // TODO:
    }
  }

  public static <T> T get(
      final Object object,
      final AccessibleObject member,
      final boolean setAccessible)
      throws IllegalAccessException, InvocationTargetException {

    notNull(member);
    if (member instanceof Field) {
      member.setAccessible(setAccessible);
      return (T)((Field)member).get(object);
    } else if (member instanceof Method) {
      member.setAccessible(setAccessible);
      return (T)((Method)member).invoke(object);
    } else {
      throw new IllegalArgumentException(); // TODO:
    }
  }

  public static <T> T get(
      final Object object,
      final String key,
      final boolean setAccessible)
      throws IllegalAccessException, InvocationTargetException {

    Collection<? extends AccessibleObject> members = CollectionUtils.union(
        getMethods(object.getClass()), getFields(object.getClass()));
    Predicate<AccessibleObject> filter = PredicateUtils.allPredicate(
        getGetterSignatureCheckPredicate(),
	hasGetterKeyPredicate(key));
    List<AccessibleObject> getters = ListUtils.select(members, filter);
    Collections.sort(getters, Collections.reverseOrder(getComparator()));
    if (getters.size() == 0) {
      throw new NoSuchElementException();
    }
    AccessibleObject getter = getters.get(0);
    return get(object, getter, setAccessible);
  }

  public static <T> Factory<T> getGetterFactory(
      final Object object,
      final AccessibleObject member,
      final boolean setAccessible) {

    notNull(member);
    return new Factory<T>() {
      public T create() {
	try {
	  return get(object, member, setAccessible);
	} catch (IllegalAccessException e) {
	  throw new IllegalArgumentException(e);
	} catch (InvocationTargetException e) {
	  throw new IllegalArgumentException(e);
	}
      }
    };
  }

  public static <T> void set(
      final Object object,
      final AccessibleObject member,
      final T value,
      final boolean setAccessible)
      throws IllegalAccessException, InvocationTargetException {

    notNull(member);
    if (member instanceof Field) {
      member.setAccessible(setAccessible);
      ((Field)member).set(object, value);
    } else if (member instanceof Method) {
      member.setAccessible(setAccessible);
      ((Method)member).invoke(object, value);
    } else {
      throw new IllegalArgumentException(); // TODO:
    }
  }

  public static <T> void set(
      final Object object,
      final String key,
      final T value,
      final boolean setAccessible)
      throws IllegalAccessException, InvocationTargetException {

    Collection<? extends AccessibleObject> members = CollectionUtils.union(
        getMethods(object.getClass()), getFields(object.getClass()));
    Predicate<AccessibleObject> filter = PredicateUtils.allPredicate(
        getSetterSignatureCheckPredicate(),
	hasSetterKeyPredicate(key));
    List<AccessibleObject> setters = ListUtils.select(members, filter);
    Collections.sort(setters, Collections.reverseOrder(getComparator()));
    if (setters.size() == 0) {
      throw new NoSuchElementException();
    }
    AccessibleObject setter = setters.get(0);
    set(object, setter, value, setAccessible);
  }

  public static <T> Closure<T> getSetterClosure(
      final Object object,
      final AccessibleObject member,
      final boolean setAccessible) {

    notNull(member);
    return new Closure<T>() {
      public void execute(T value) {
	try {
	  set(object, member, value, setAccessible);
	} catch (IllegalAccessException e) {
	  throw new IllegalArgumentException(e);
	} catch (InvocationTargetException e) {
	  throw new IllegalArgumentException(e);
	}
      }
    };
  }

  public static Closure<String> getStringSetterClosure(
      final Object object,
      final AccessibleObject member,
      final boolean setAccessible) {

    notNull(member);
    final Class dataType = getValueDataType(member);

    return new Closure<String>() {
      public void execute(String value) {
	try {
          if (dataType == int.class || dataType == Integer.class) {
            set(object, member, Integer.parseInt(value), setAccessible);
          } else if (dataType == long.class || dataType == Long.class) {
            set(object, member, Long.parseLong(value), setAccessible);
	  } else if (dataType == float.class || dataType == Float.class) {
            set(object, member, Float.parseFloat(value), setAccessible);
	  } else if (dataType == double.class || dataType == Double.class) {
            set(object, member, Double.parseDouble(value), setAccessible);
	  } else if (dataType == boolean.class || dataType == Boolean.class) {
	    set(object, member, Boolean.parseBoolean(value), setAccessible);
	  } else if (dataType == String.class) {
	    set(object, member, value, setAccessible);
	  } else {
	    throw new IllegalArgumentException();
	  }
        } catch (IllegalAccessException e) {
	  throw new IllegalArgumentException(e);
	} catch (InvocationTargetException e) {
	  throw new IllegalArgumentException(e);
	}
      }
    };
  }

  public static <T> Transformer<AccessibleObject, Closure<T>>
      getSetterClosureFactory(
	  final Object object,
	  final boolean setAccessible) {

    return new Transformer<AccessibleObject, Closure<T>>() {
      public Closure<T> transform(final AccessibleObject member) {
	return getSetterClosure(object, member, setAccessible);
      }
    };
  }

  public static Transformer<AccessibleObject, Closure<String>>
      getStringSetterClosureFactory(
	  final Object object,
	  final boolean setAccessible) {

    return new Transformer<AccessibleObject, Closure<String>>() {
      public Closure<String> transform(final AccessibleObject member) {
	return getStringSetterClosure(object, member, setAccessible);
      }
    };
  }

  public static Class getValueDataType(final AccessibleObject member) {
    if (member instanceof Field) {
      return ((Field)member).getType();
    } else if (member instanceof Method) {
      if (((Method)member).getParameterTypes().length == 0) {
	return ((Method)member).getReturnType();
      } else {
	return ((Method)member).getParameterTypes()[0];
      }
    } else {
      throw new IllegalArgumentException();
    }
  }

  public static Comparator<AccessibleObject> getComparator() {
    return new Comparator<AccessibleObject>() {
      public int compare(AccessibleObject o1, AccessibleObject o2) {
	int rank1 = o1.isAnnotationPresent(Getter.class) ||
	    o1.isAnnotationPresent(Setter.class) ? 1 : 0;
	int rank2 = o2.isAnnotationPresent(Getter.class) ||
	    o2.isAnnotationPresent(Setter.class) ? 1 : 0;
        if (rank1 != rank2) return rank1 - rank2;
	rank1 = Modifier.isPublic(((Member)o1).getModifiers()) ? 1 : 0;
	rank2 = Modifier.isPublic(((Member)o2).getModifiers()) ? 1 : 0;
        if (rank1 != rank2) return rank1 - rank2;
        rank1 = o1 instanceof Method ? 1 : 0;
        rank2 = o2 instanceof Method ? 1 : 0;
        return rank1 - rank2;
      }
      public boolean equals(Object object) {
	if (object == null) return false;
	return this == object;
      }
    };
  }

  public void setter(int arg) {
    System.out.println("setter invoked: " + arg);
  }

  private String aaa() {
    return "met";
  }

  private String aaa = "fild";



  @Getter("ahoj")
  public static void main(String[] args) throws Exception {
    ObjectMapDecorator object = new ObjectMapDecorator(Object.class);
    String value = get(object, "aaa", true);
    System.out.println(value);
    set(object, "ter", 587, true);
  }

}
