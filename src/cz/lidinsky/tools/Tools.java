/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jilm
 */
public class Tools {


  public static void close(java.io.Closeable object)
  {
    if (object != null)
      try
      {
	object.close();
      }
      catch (java.io.IOException e)
      {
      }
  }



  public static boolean equals(Object object1, Object object2) {
    if (object1 == null || object2 == null) {
      return false;
    } else {
      return object1.equals(object2);
    }
  }


  public static void sleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException e)
    {
    }
  }


  public static Object parse(Class _class, String value) {
    if (Double.class.isAssignableFrom(_class)
            || double.class.isAssignableFrom(_class)) {
      return new Double(value);
    } else if (Integer.class.isAssignableFrom(_class)
            || int.class.isAssignableFrom(_class)) {
      return new Integer(value);
    } else if (Boolean.class.isAssignableFrom(_class)
            || boolean.class.isAssignableFrom(_class)) {
      return Boolean.valueOf(value);
    } else if (String.class.isAssignableFrom(_class)) {
      return value;
    } else {
      throw new CommonException()
              .setCode(ExceptionCode.PARSE)
              .set("message", "Given datatype is not supported!")
              .set("class", _class.getName())
              .set("value", value);
    }
  }

  /**
   *
   */
  public static final Properties properties = new Properties();

  static {
    try {
      properties.load(
              Tools.class.getClassLoader().getResourceAsStream("cz/lidinsky/tools/configuration.properties"));
    } catch (IOException ex) {
      Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
