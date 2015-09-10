package cz.lidinsky.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ToStringBuilderTest implements IToStringBuildable {

  protected static class EmptyClass extends ToStringBuilderTest {
    public void toString(ToStringBuilder builder) {
    }
  }

  protected static class IntField extends ToStringBuilderTest {
    public void toString(ToStringBuilder builder) {
      builder.append("integer", 34);
    }
  }

  protected static class StringField extends IntField {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("string", "str value");
    }
  }

  protected static class EmptyArray extends StringField {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("empty array", new Object[0]);
    }
  }

  protected static class DoubleArray extends IntField {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("double array", new double[] {1.2, 2.3, 3.4, 4.5});
    }
  }

  protected static class NullField extends IntField {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("Null field", (Object)null);
    }
  }

  protected static class LongArray extends IntField {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      float[] array = new float[50];
      for (int i=0; i<50; i++) array[i] = i;
      builder.append("Long array", array);
    }
  }

  protected static class ObjectField extends ToStringBuilderTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("Object field", new Object());
    }
  }

  protected static class NestedObject extends ToStringBuilderTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("Nested object", new IntField());
    }
  }

  protected static class FloatList extends ToStringBuilderTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      ArrayList<Float> list = new ArrayList<Float>();
      for (int i=0; i<30; i++) list.add((float)i * 0.1f);
      builder.append("Float list", list);
    }
  }

  protected static class SetField extends ToStringBuilderTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      Set<Object> set = new HashSet<Object>();
      for (int i=0; i<30; i++) set.add(new Object());
      builder.append("Set field", set);
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder()
        .append(this)
        .toString();
  }

  public void toString(ToStringBuilder builder) {
  }

  @Test
  public void test1() {
    System.out.println(new EmptyClass().toString());
  }

  @Test
  public void test2() {
    System.out.println(new IntField().toString());
  }

  @Test
  public void test3() {
    System.out.println(new StringField().toString());
  }

  @Test
  public void test4() {
    System.out.println(new EmptyArray().toString());
  }

  @Test
  public void test5() {
    System.out.println(new DoubleArray().toString());
  }

  @Test
  public void test6() {
    System.out.println(new NullField().toString());
  }

  @Test
  public void test7() {
    System.out.println(new LongArray().toString());
  }

  @Test
  public void test8() {
    System.out.println(new ObjectField().toString());
  }

  @Test
  public void test9() {
    System.out.println(new NestedObject().toString());
  }

  @Test
  public void test10() {
    System.out.println(new FloatList().toString());
  }

  @Test
  public void test11() {
    System.out.println(new SetField().toString());
  }

  @Test
  public void testEmptyIntArray() {
    System.out.println(new ToStringBuilder().append(new int[0]).toString());
  }

  @Test
  public void testIntArray1() {
    System.out.println(new ToStringBuilder().append(new int[] {1}).toString());
  }

  @Test public void testIntArray2() {
    System.out.println(new ToStringBuilder().append(new int[20]).toString());
  }

  @Test public void testIntArray3() {
    System.out.println(new ToStringBuilder()
        .append(new int[] {1, 2, 3, 3, 3, 4, 3, 5, 5, 6})
        .toString());
  }

  @Test public void testIntArray4() {
    System.out.println(new ToStringBuilder()
        .append(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21})
        .toString());
  }

  @Test public void testIntArray5() {
    int[] intArray = new int[100];
    for (int i = 0; i < intArray.length; i++) {
      intArray[i] = (int)Math.round(
          (Math.random() * 2d - 1d) * (double)Integer.MAX_VALUE);
    }
    System.out.println(new ToStringBuilder().append(intArray).toString());
  }

  @Test public void testIntArray6() {
    int[] intArray = new int[30];
    fillRandomly(intArray);
    for (int i = 5; i < 20; i++) {
      intArray[i] = i * -3;
    }
    System.out.println(new ToStringBuilder().append(intArray).toString());
  }

  private void fillRandomly(int[] intArray) {
    for (int i = 0; i < intArray.length; i++) {
      intArray[i] = (int)Math.round(
          (Math.random() * 2d - 1d) * (double)Integer.MAX_VALUE);
    }
  }

}
