package cz.lidinsky.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ToStringMultilineStyleTest implements IToStringBuildable {

  protected static class EmptyClass extends ToStringMultilineStyleTest {
    public void toString(ToStringBuilder builder) {
    }
  }

  protected static class IntField extends ToStringMultilineStyleTest {
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

  protected static class ObjectField extends ToStringMultilineStyleTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("Object field", new Object());
    }
  }

  protected static class NestedObject extends ToStringMultilineStyleTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      builder.append("Nested object", new IntField());
    }
  }

  protected static class FloatList extends ToStringMultilineStyleTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      ArrayList<Float> list = new ArrayList<Float>();
      for (int i=0; i<30; i++) list.add((float)i * 0.1f);
      builder.append("Float list", list);
    }
  }

  protected static class SetField extends ToStringMultilineStyleTest {
    public void toString(ToStringBuilder builder) {
      super.toString(builder);
      Set<Object> set = new HashSet<Object>();
      for (int i=0; i<30; i++) set.add(new Object());
      builder.append("Set field", set);
    }
  }

  @Override
  public String toString() {
    return new ToStringMultilineStyle()
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

}
