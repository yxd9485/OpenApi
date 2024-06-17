package com.fenbeitong.openapi.plugin.seeyon.utils;

/**
 * RobotMsg
 *
 * <p>Robot Base Msg
 *
 * @author ivan
 * @version 1.0 Created by ivan on 4/8/19 - 3:38 PM.
 */
public class RobotMsg implements java.io.Serializable {

  private static final long serialVersionUID = -6881367095976668602L;
  private String msgtype;

  public String getMsgtype() {
    return msgtype;
  }

  public void setMsgtype(String msgtype) {
    this.msgtype = msgtype;
  }
}
