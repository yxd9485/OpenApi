package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by lizhen on 2020/3/28.
 */
@Data
public class KafkaSendMessageMsg {

    private String title;

    private String content;

    private String desc;

    private boolean alert;

    @JsonProperty("msg_type")
    private String msgType;

    private JSONObject msg;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("company_id")
    private String companyId;

    private Integer badge;

}
