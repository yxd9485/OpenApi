package com.fenbeitong.openapi.plugin.wechat.eia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>Title: WeChatEiaUpdatePhoneRequest<p>
 * <p>Description: 微信更新手机号请求参数<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/7/25 01:38
 */
@Data
public class WeChatEiaUpdatePhoneRequest {
    /**
     * 三方企业id
     */
    @NotNull(message = "公司id[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 临时授权码
     */
    @NotNull(message = "临时授权码[temp_code]不可为空")
    @JsonProperty("temp_code")
    private String tempCode;

}
