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

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

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

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

/**
 *
 *
 */
public class ObjectMapUtils {

  //------------------------------------------------- Getter and Setter Filters

  /**
   *  Returns a predicate which is true for methods and fields that are
   *  annotated by the given annotation. This predicate is usefull for
   *  getter and setter filters.
   *
   *  <p>The returned predicate throws CommonException if the input
   *  parameter is <code>null</null>.
   *
   *  @param annotation
   *             required annotation class
   *
   *  @throws CommonException
   *             if the <code>anno</code> is <code>null</code>
   */
  public static Predicate<AnnotatedElement> hasAnnotationPredicate(
      final Class<? extends Annotation> annotation) {

    notNull(annotation);
    return new Predicate<AnnotatedElement>() {

      public boolean evaluate(final AnnotatedElement object) {
        notNull(object);
        return object.isAnnotationPresent(annotation);
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

  /**
   *  Returns a predicate which is true for fields and methods that could
   *  be used as a getters.
   */
  public static Predicate<AccessibleObject> hasGetterSignaturePredicate() {
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(AccessibleObject object) {
        return hasGetterSignature(object);
      }
    };
  }

  protected static boolean hasSetterSignature(final AccessibleObject object) {
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

  public static Predicate<AccessibleObject> hasSetterSignaturePredicate() {
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(AccessibleObject object) {
        boolean result = hasSetterSignature(object);
        return result;
      }
    };
  }

  //--------------------------------------------------------- Key Transformers.

  /**
   *  Returns transformer which takes a Getter annotation of the given object
   *  ant returns a value of that annotation. This is usefull as a key
   *  transformer.
   */
  public static Transformer<AccessibleObject, String>
    getGetterValueTransformer() {

      return new Transformer<AccessibleObject, String>() {

        public String transform(AccessibleObject object) {
          // if there is a getter annotation take the annotation value
          Getter anno = object.getAnnotation(Getter.class);
          if (anno != null) {
            String key = anno.value();
            if (!isBlank(key)) return key;
          }
          return null; // TODO:
        }

      };
    }

  /**
   *  Returns transformer which takes a Setter annotation of the given object
   *  ant returns a value of that annotation. This is usefull as a key
   *  transformer.
   */
  public static Transformer<AccessibleObject, String>
    getSetterValueTransformer() {

      return new Transformer<AccessibleObject, String>() {

        public String transform(AccessibleObject object) {
          // if there is a setter annotation take the annotation value
          Setter anno = object.getAnnotation(Setter.class);
          if (anno != null) {
            String key = anno.value();
            if (!isBlank(key)) return key;
          }
          return null; // TODO:
        }

      };
    }

  public static Transformer<AccessibleObject, String> keyFromName() {

    return new Transformer<AccessibleObject, String>() {

      public String transform(AccessibleObject object) {
        return getKeyFromName(object);
      }
    };
  }

  //---------------------------------------------------------- Setter Closures.

  /**
   *  Returns Setter Closure which takes String as an input and provides
   *  conversion (parse) to the required datatype.
   */
  public static Closure<String> stringSetterClosure(
      final Object object,
      final AccessibleObject member,
      final boolean setAccessible) {

    notNull(member);
    final Class dataType = getValueDataType(member);
    if (dataType == int.class || dataType == Integer.class) {
      return new Closure<String>() {
        public void execute(String value) {
          set(object, member, Integer.parseInt(value), setAccessible);
        }
      };
    } else if (dataType == long.class || dataType == Long.class) {
      return new Closure<String>() {
        public void execute(String value) {
          set(object, member, Long.parseLong(value), setAccessible);
        }
      };
    } else if (dataType == float.class || dataType == Float.class) {
      return new Closure<String>() {
        public void execute(String value) {
          set(object, member, Float.parseFloat(value), setAccessible);
        }
      };
    } else if (dataType == double.class || dataType == Double.class) {
      return new Closure<String>() {
        public void execute(String value) {
          set(object, member, Double.parseDouble(value), setAccessible);
        }
      };
    } else if (dataType == boolean.class || dataType == Boolean.class) {
      return new Closure<String>() {
        public void execute(String value) {
          set(object, member, Boolean.parseBoolean(value), setAccessible);
        }
      };
    } else if (dataType == String.class) {
      return new Closure<String>() {
        public void execute(String value) {
          set(object, member, value, setAccessible);
        }
      };
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.UNSUPPORTED_TYPE)
        .set("data type", dataType);
    }
  }

  /**
   *  Returns setter closure factory that takes a string parameter and
   *  converse it to the appriate data type.
   */
  public static Transformer<Pair<Object, AccessibleObject>, Closure<String>>
      stringSetterClosureFactory(final boolean setAccessible) {

    return new Transformer<Pair<Object, AccessibleObject>, Closure<String>>() {
      public Closure<String> transform(
          final Pair<Object, AccessibleObject> param) {

        Object decorated = param.getLeft();
        AccessibleObject member = param.getRight();
        return stringSetterClosure(decorated, member, setAccessible);
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

  public static <T> Closure<T> getSetterClosure(
      final Object object,
      final AccessibleObject member,
      final boolean setAccessible) {

    notNull(member);
    return new Closure<T>() {
      public void execute(T value) {
        set(object, member, value, setAccessible);
      }
    };
  }

  public static <T> Transformer<Pair<Object, AccessibleObject>, Closure<T>>
      setterClosureFactory(final boolean setAccessible) {

    return new Transformer<Pair<Object, AccessibleObject>, Closure<T>>() {
      public Closure<T> transform(final Pair<Object, AccessibleObject> param) {
        Object decorated = param.getLeft();
        AccessibleObject member = param.getRight();
        return getSetterClosure(decorated, member, setAccessible);
      }
    };
  }

  //--------------------------------------------------------- Getter Factories.

  /**
   *  Basic getter factory.
   */
  public static <T> Transformer<Pair<Object, AccessibleObject>, Factory<T>>
    getterFactory(final boolean setAccessible) {

      return new Transformer<Pair<Object, AccessibleObject>, Factory<T>>() {

        public Factory<T> transform(Pair<Object, AccessibleObject> param) {

          final Object object = param.getLeft();
          final AccessibleObject member = param.getRight();

          return new Factory<T>() {

            public T create() {
              try {
                return get(object, member, setAccessible);
              } catch (Exception e) {
                throw new CommonException()
                  .setCause(e)
                  .set("message", "Exception while reading from a getter!")
                  .set("object", object)
                  .set("member", member)
                  .set("accessibility", setAccessible);
              }
            }
          };
        }
      };
    }

  /**
   *  Getter factory which convert the value into the string.
   */
  public static Transformer<Pair<Object, AccessibleObject>, Factory<String>>
    stringGetterFactory(final boolean setAccessible) {

      return new
        Transformer<Pair<Object, AccessibleObject>, Factory<String>>() {

        public Factory<String> transform(Pair<Object, AccessibleObject> param) {

          final Object object = param.getLeft();
          final AccessibleObject member = param.getRight();

          return new Factory<String>() {

            public String create() {
              try {
                Object rawValue = get(object, member, setAccessible);
                if (rawValue == null) {
                  return "<null>";
                } else {
                  return rawValue.toString();
                }
              } catch (Exception e) {
                throw new CommonException()
                  .setCause(e)
                  .set("message", "Exception while reading from a getter!")
                  .set("object", object)
                  .set("member", member)
                  .set("accessibility", setAccessible);
              }
            }
          };
        }
      };
    }

  //-------------------------------------------------------- Auxiliary Methods.

  public static <T> void set(
      final Object object,
      final AccessibleObject member,
      final T value,
      final boolean setAccessible) {

    try {
      notNull(member);
      if (member instanceof Field) {
        member.setAccessible(setAccessible);
        ((Field)member).set(object, value);
      } else if (member instanceof Method) {
        member.setAccessible(setAccessible);
        ((Method)member).invoke(object, value);
      } else {
        throw new CommonException()
          .setCode(ExceptionCode.UNSUPPORTED_TYPE)
          .set("message",
              "Only fields and methods are supported as a setters!");
      }
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "Exception while trying to invoke a setter!")
        .set("object", object)
        .set("member", member)
        .set("value", value)
        .set("accessibility", setAccessible);
    }
  }

  public static <T> void set(
      final Object object,
      final String key,
      final T value,
      final boolean setAccessible) {

    try {
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
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "Exception while trying to invoke a setter!")
        .set("object", object)
        .set("value", value)
        .set("accessibility", setAccessible);
    }
  }

  //----------------------------------------------------------------- To Remove

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

  public static <T> Predicate<AccessibleObject> getSetterDataTypeCheckPredicate(
      final Class<T> dataType) {

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
        boolean result = dataType.isAssignableFrom(objectDataType);
        return result;
      }
    };
  }

  public static Predicate<AccessibleObject> getHasAnnotationPredicate(
      final Class<? extends Annotation> annotation) {
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(final AccessibleObject object) {
        boolean result = object.isAnnotationPresent(annotation);
        return result;
      }
    };
  }

  public static Predicate<AccessibleObject> getGetterSignatureCheckPredicate() {
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(AccessibleObject object) {
        return hasGetterSignature(object);
      }
    };
  }

  public static Predicate<AccessibleObject> getSetterSignatureCheckPredicate() {
    return new Predicate<AccessibleObject>() {
      public boolean evaluate(AccessibleObject object) {
        boolean result = hasSetterSignature(object);
        return result;
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

}
