package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: YiDuiJieNotifyMsgResultReq</p>
 * <p>Description: 易对接通知消息结果请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 7:24 PM
 */
@Data
public class YiDuiJieNotifyMsgResultReq {

    private Integer status;

    @JsonProperty("documentType")
    private String documentType;

    private String id;

    @JsonProperty("localId")
    private String localId;

    private String message;
}
