package cz.lidinsky.tools.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AccessibleObject;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Closure;
import cz.lidinsky.tools.CommonException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ObjectMapDecoratorTest {

  private int intValue = 0;

  @Setter("int-value")
  public void setIntValue(int value) {
    intValue = value;
  }

  @Getter("int-value")
  public int getIntValue() {
    return intValue;
  }

  public void empty() {}

  //----------------------------------------------------------- Initialization.

  private ObjectMapDecorator<String> map;
  private Method setIntMethod;
  private Method getIntMethod;
  private Method emptyMethod;

  @Before
  public void init() throws Exception {
    map = new ObjectMapDecorator<String>(String.class)
      .setSetterFilter(ObjectMapUtils.hasAnnotationPredicate(Setter.class))
      .setGetterFilter(null)
      .setSetterFactory(
          ObjectMapUtils.stringSetterClosureFactory(this, false))
      .setSetterKeyTransformer(ObjectMapUtils.getSetterValueTransformer());
    map.setDecorated(this);
  }

  //-------------------------------------------------------------------- Tests.

  @Test
  public void test1() {
    map.put("int-value", "67");
    assertEquals(67, intValue);
  }

}
