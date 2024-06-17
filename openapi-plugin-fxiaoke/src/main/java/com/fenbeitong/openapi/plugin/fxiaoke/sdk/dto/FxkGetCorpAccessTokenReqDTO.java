package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: FxkGetCorpAccessTokenReqDTO</p>
 * <p>Description: 纷享销客获取公司token请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 6:56 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxkGetCorpAccessTokenReqDTO {

    /**
     * 企业应用ID
     */
    private String appId;

    /**
     * 企业应用凭证密钥
     */
    private String appSecret;

    /**
     * 企业应用永久授权码
     */
    private String permanentCode;
}
