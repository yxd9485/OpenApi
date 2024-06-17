package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权登录响应信息
 * @author lizhen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DingtalkIsvAuthRespDTO {

    /**
     * 企业id
     */
    private String companyId;
    /**
     * 企业微信用户三方id
     */
    private String thirdEmployeeId;

    //主企业id
    private String corpId;

    /**
     * 类型：1、个人 2、企业
     */
    private String type;

}
