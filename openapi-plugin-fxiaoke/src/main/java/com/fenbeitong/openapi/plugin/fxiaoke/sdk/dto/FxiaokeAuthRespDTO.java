package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxiaokeAuthRespDTO {

    /**
     * 企业id
     */
    private String companyId;
    /**
     * 企业微信用户三方id
     */
    private String thirdEmployeeId;

}
