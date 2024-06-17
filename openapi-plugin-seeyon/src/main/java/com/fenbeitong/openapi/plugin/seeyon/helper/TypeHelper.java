package com.fenbeitong.openapi.plugin.seeyon.helper;


import com.fenbeitong.openapi.plugin.seeyon.exceptions.FrameworkException;

import java.util.Objects;

/**
 * TypeHelper
 *
 * <p>For Type Convent
 *
 * @author ivan
 * @version 1.0 Created by ivan on 12/15/18 - 4:09 PM.
 */
public class TypeHelper {

  /**
   * @param object :
   * @return java.lang.String
   * @author Created by ivan on 3:32 PM 12/24/18.
   *     <p>//Cast Object 2 String
   */
  public static String castToString(Object object) {
    if (Objects.isNull(object)) {
      return null;
    } else {
      return object.toString();
    }
  }

  public static <T> T notNull(final T object, final String message) {
    if (object == null) {
      throw new FrameworkException("Argument Can not be null. " + message);
    }
    return object;
  }
}
