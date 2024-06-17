package com.fenbeitong.openapi.plugin.seeyon.utils;

import com.fenbeitong.openapi.plugin.seeyon.constant.DingTalkConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TextMsg
 *
 * @author ivan
 * @version 1.0 Created by ivan on 4/8/19 - 3:36 PM.
 */
@Builder
@EqualsAndHashCode(callSuper = false)
public class TextMsg extends RobotMsg {

  private static final long serialVersionUID = -2720141497364879915L;

  public TextMsg() {
    super.setMsgtype(DingTalkConstants.MSG_TYPE_TEXT);
  }

  public TextMsg(TextBean text, AtBean at) {
    super.setMsgtype(DingTalkConstants.MSG_TYPE_TEXT);
    this.text = text;
    this.at = at;
  }

  /**
   * text : {"content":"我就是我, 是不一样的烟火@156xxxx8827"} at :
   * {"atMobiles":["156xxxx8827","189xxxx8325"],"isAtAll":false}
   */
  @ApiModelProperty(value = "消息内容", example = "", required = false)
  private TextBean text;

  @ApiModelProperty(value = "用户提醒", example = "", required = false)
  private AtBean at;

  public TextBean getText() {
    return text;
  }

  public void setText(TextBean text) {
    this.text = text;
  }

  public AtBean getAt() {
    return at;
  }

  public void setAt(AtBean at) {
    this.at = at;
  }

  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TextBean {

    /** content : 我就是我, 是不一样的烟火@156xxxx8827 */
    @ApiModelProperty(value = "消息内容", example = "我就是我, 是不一样的烟火@156xxxx8827", required = false)
    private String content;

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }

  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AtBean {

    /** atMobiles : ["156xxxx8827","189xxxx8325"] isAtAll : false */
    @ApiModelProperty(value = "提醒所有人", example = "false", required = false)
    private boolean isAtAll;

    @ApiModelProperty(
        value = "提醒指定用户",
        example = "[\"156xxxx8827\",\"189xxxx8325\"]",
        required = false)
    private List<String> atMobiles;

    public boolean isIsAtAll() {
      return isAtAll;
    }

    public void setIsAtAll(boolean isAtAll) {
      this.isAtAll = isAtAll;
    }

    public List<String> getAtMobiles() {
      return atMobiles;
    }

    public void setAtMobiles(List<String> atMobiles) {
      this.atMobiles = atMobiles;
    }
  }
}
