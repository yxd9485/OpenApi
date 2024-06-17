package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 北森的组织数据结构
 *
 * @author xiaowei
 * @date 2020/06/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeisenEmployeeListParam {

    @JsonProperty("EmpStatus")
    private int[] empStatus;
    @JsonProperty("EmployType")
    private int[] employType;
    @JsonProperty("ServiceType")
    private int[] serviceType;
    @JsonProperty("Ids")
    private int[] ids;
    private boolean isGetLatestRecord;
    @JsonProperty("StartTime")
    private String startTime;
    @JsonProperty("StopTime")
    private String stopTime;
    @JsonProperty("WithDisabled")
    private boolean withDisabled;
    @JsonProperty("WithDeleted")
    private boolean withDeleted;
    @JsonProperty("PageIndex")
    private int pageIndex;
    @JsonProperty("PageSize")
    private int pageSize;
    @JsonProperty("Columns")
    private String[] columns;

}
