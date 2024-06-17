package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取jsapi_ticket参数
 * Created by lizhen on 2020/3/19.
 */
@Data
public class WeChatIsvJsapiTicketResponse {

    private Integer errcode;

    private String errmsg;

    private String ticket;

    @JsonProperty("expires_in")
    private Integer expiresIn;

}
