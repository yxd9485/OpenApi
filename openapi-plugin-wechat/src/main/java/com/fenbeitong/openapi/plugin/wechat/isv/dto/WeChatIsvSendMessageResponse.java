package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 消息推送
 * Created by lizhen on 2020/3/28.
 */
@Data
public class WeChatIsvSendMessageResponse {

    private Integer errcode;

    private String errmsg;

}
