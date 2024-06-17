package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * welink授权登录响应信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeLinkIsvAuthRespDTO {

    /**
     * 企业id
     */
    private String companyId;
    /**
     * 企业微信用户三方id
     */
    private String thirdEmployeeId;

}
