package com.fenbeitong.openapi.plugin.wechat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业微信授权登录响应信息
 * Created by log.chang on 2020/3/23.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeChatAuthRespDTO {

    /**
     * 企业id
     */
    private String companyId;
    /**
     * 企业微信用户三方id
     */
    private String thirdEmployeeId;

}
