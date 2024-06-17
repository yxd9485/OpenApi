package com.fenbeitong.openapi.plugin.customize.wawj.dto;

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
public class WawjAuthRespDTO {

    /**
     * 企业id
     */
    private String companyId;
    /**
     * 企业微信用户三方id
     */
    private String thirdEmployeeId;

}
