package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: BeisenEmployeeV5ReqDTO<p>
 * <p>Description: 北森人员请求参数实体（V5版本）<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/9/13 15:09
 */
@Data
public class BeisenEmployeeV5ReqDTO extends BeisenReqBaseDTO{
    /**
     * 人员状态 1:待入职、2:试用、3:正式、4:调出、5:待调入、6:退休、8:离职、12:非正式
     */
    @JsonProperty("empStatus")
    private String[] empStatus;
    /**
     * 0:内部员工、1:外部人员、2:实习生
     */
    @JsonProperty("employType")
    private String[] employType;
    /**
     *  0：主职 1：兼职
     */
    @JsonProperty("serviceType")
    private String[] serviceType;
}
