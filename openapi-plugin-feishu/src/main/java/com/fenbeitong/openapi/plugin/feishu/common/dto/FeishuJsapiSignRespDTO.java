package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书签名
 * @Auther xiaohai
 * @Date 2021/11/12
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeishuJsapiSignRespDTO extends FeiShuRespDTO {

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("time_stamp")
    private Long timeStamp;

    @JsonProperty("nonce_str")
    private String nonceStr;

    private String signature;

}
