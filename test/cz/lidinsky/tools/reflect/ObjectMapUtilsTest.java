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

public class ObjectMapUtilsTest {

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

  private Method setIntMethod;
  private Method getIntMethod;
  private Method emptyMethod;

  @Before
  public void init() throws Exception {
    setIntMethod = this.getClass().getMethod("setIntValue", int.class);
    getIntMethod = this.getClass().getMethod("getIntValue");
    emptyMethod = this.getClass().getMethod("empty");

  }

  //----------------------------------------------- Annotation Predicate Tests.

  @Test
  public void test1() throws Exception {
    Predicate<AnnotatedElement> predicate
      = ObjectMapUtils.hasAnnotationPredicate(Setter.class);
    assertTrue(predicate.evaluate(setIntMethod));
  }

  @Test
  public void test2() throws Exception {
    Predicate<AnnotatedElement> predicate
      = ObjectMapUtils.hasAnnotationPredicate(Getter.class);
    assertFalse(predicate.evaluate(setIntMethod));
  }

  @Test
  public void Test3() throws Exception {
    Predicate<AnnotatedElement> predicate
      = ObjectMapUtils.hasAnnotationPredicate(Getter.class);
    assertFalse(predicate.evaluate(emptyMethod));
  }

  @Test(expected=CommonException.class)
  public void test4() throws Exception {
    Predicate<AnnotatedElement> predicate
      = ObjectMapUtils.hasAnnotationPredicate(Getter.class);
    assertFalse(predicate.evaluate(null));
  }

  @Test(expected=CommonException.class)
  public void test5() throws Exception {
    Predicate<AnnotatedElement> predicate
      = ObjectMapUtils.hasAnnotationPredicate(null);
  }

  //--------------------------------------------------------- Key Transformers.

  @Test
  public void testKT1() {
     Transformer<AccessibleObject, String> transformer
       = ObjectMapUtils.getGetterValueTransformer();
     assertEquals("int-value", transformer.transform(getIntMethod));
  }

  @Test
  public void testKT2() {
     Transformer<AccessibleObject, String> transformer
       = ObjectMapUtils.getSetterValueTransformer();
     assertEquals("int-value", transformer.transform(setIntMethod));
  }

  //------------------------------------------------------------- Set Closures.

  @Test
  public void testSC1() {
    Closure<String> sc
      = ObjectMapUtils.stringSetterClosure(this, setIntMethod, false);
    sc.execute("25");
    assertEquals(25, intValue);
  }

}
