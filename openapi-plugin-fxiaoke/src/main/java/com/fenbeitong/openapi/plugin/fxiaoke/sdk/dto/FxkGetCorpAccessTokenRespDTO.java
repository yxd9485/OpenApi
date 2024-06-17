package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

/**
 * <p>Title: FxkGetCorpAccessTokenRespDTO</p>
 * <p>Description: 纷享销客获取公司token响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 6:56 PM
 */
@Data
public class FxkGetCorpAccessTokenRespDTO {

    /**
     * 返回码
     */
    private Integer errorCode;

    /**
     * 对返回码的文本描述内容
     */
    private String errorMessage;

    /**
     * 企业应用访问公司合法性凭证
     */
    private String corpAccessToken;

    /**
     * 开放平台派发的公司帐号
     */
    private String corpId;

    /**
     * 企业应用访问公司合法性凭证的过期时间，单位为秒，取值在0~7200之间
     */
    private Integer expiresIn;
}
