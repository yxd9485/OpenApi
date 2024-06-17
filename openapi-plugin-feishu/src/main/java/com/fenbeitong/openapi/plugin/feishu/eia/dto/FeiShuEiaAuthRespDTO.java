package com.fenbeitong.openapi.plugin.feishu.eia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * feishu内部应用免登
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuEiaAuthRespDTO {

    /**
     * 企业id
     */
    private String companyId;
    /**
     * 三方id
     */
    private String thirdEmployeeId;

}
