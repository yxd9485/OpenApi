package com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaTicketSelfReqDto {

    /**
     * 轻应用id
     */
    @JsonProperty(value = "appId")
    private String appid;

    /**
     * 云之家APP会传递ticket参数给轻应用（把ticket参数追加到轻应用对应的url中）
     * 时效为1小时
     */
    private String ticket;

    /**
     * 云之家授权token
     * 有效时间为7200秒
     */
    @JsonProperty(value = "accessToken")
    private String accessToken;
}
