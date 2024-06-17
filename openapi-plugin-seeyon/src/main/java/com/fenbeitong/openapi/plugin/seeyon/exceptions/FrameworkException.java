package com.fenbeitong.openapi.plugin.seeyon.exceptions;

/**
 * FrameworkException
 *
 * <p>Exception throw from Framework
 *
 * @author ivan
 * @version 1.0 Created by ivan on 12/13/18 - 5:45 PM.
 */
public class FrameworkException extends RuntimeException {

  private static final long serialVersionUID = 8188554792053824006L;

  public FrameworkException(String message) {
    super(message);
  }

  public FrameworkException(Throwable throwable) {
    super(throwable);
  }

  public FrameworkException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
