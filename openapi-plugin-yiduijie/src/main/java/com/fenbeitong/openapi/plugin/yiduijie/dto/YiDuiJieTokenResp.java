package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: YiDuiJieTokenResp</p>
 * <p>Description: 易对接token响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:13 PM
 */
@Data
public class YiDuiJieTokenResp {

    /**
     * 获取到的访问码
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 有效期，以秒为单位
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * 访问码的使用范围
     */
    private String scope;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 访问码类型
     */
    @JsonProperty("token_type")
    private String tokenType;

    private String message;

    public boolean success() {
        return status != null && status == 0;
    }
}
