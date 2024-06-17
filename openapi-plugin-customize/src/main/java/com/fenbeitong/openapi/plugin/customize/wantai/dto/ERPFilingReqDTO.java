package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2022-08-09 13:37:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ERPFilingReqDTO {

    private String taskId;

    private String orgCode;

    private String orderType;

    private String period;

    private String beginTime;

    private String endTime;

}
