package cz.lidinsky.tools;

public class Test implements IToStringBuildable {

  @Override
  public String toString() {
    return new ToStringBuilder(new DefaultToStringStyle())
        .append(this)
        .toString();
  }

  public void toString(ToStringBuilder builder) {
    builder.append("integer", 34);
  }

  public static void main(String[] args) {
    System.out.println(new Test().toString());
  }

}
