package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 北森的审批单数据结构
 *
 * @author xiaowei
 * @date 2020/07/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeisenApplyListParam {

    @JsonProperty("StartDate")
    private String startDate;
    @JsonProperty("EndDate")
    private String endDate;
    @JsonProperty("TenantId")
    private String tenantId;
    @JsonProperty("PageIndex")
    private int pageIndex;
    @JsonProperty("PageSize")
    private int pageSize;


}
