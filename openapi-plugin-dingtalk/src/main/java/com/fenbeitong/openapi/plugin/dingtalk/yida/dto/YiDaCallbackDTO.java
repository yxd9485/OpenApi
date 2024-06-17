package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;
/**
 * <p>Title: YiDaRespDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 8:45 下午
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YiDaCallbackDTO implements Serializable {

    private String applyType;

    private String creator;

    private String formUuid;

    private String corpId;

    @JsonProperty("__signature")
    private String signature;

    private String createTime;

    private String formInstId;

    private String appId;

    private String appSecret;

}
