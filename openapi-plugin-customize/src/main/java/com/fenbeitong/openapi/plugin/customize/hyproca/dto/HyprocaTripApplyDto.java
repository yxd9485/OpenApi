package com.fenbeitong.openapi.plugin.customize.hyproca.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ThipApplyDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-03 15:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HyprocaTripApplyDto {


    // 出发城市
    @JsonProperty("OUTCITY")
    private String outCity;

    // 到达城市
    @JsonProperty("ARRIVECITY")
    private String arriveCity;

    // 员工ID
    @JsonProperty("EMPID")
    private String empId;

    // 员工编号
    @JsonProperty("EMPNO")
    private String empNo;

    // 出差类型 2593国内 2594国外
    @JsonProperty("REGTYPE")
    private String regType;

    // 开始时间
    @JsonProperty("BEGINTIME")
    private String beginTime;


    // 结束时间
    @JsonProperty("ENDTIME")
    private String endTime;

    @JsonProperty("CERATEDATE")
    private String cerateDate;

    // 部门ID
    @JsonProperty("ORGID")
    private String orgId;


    // 申请人姓名
    @JsonProperty("TITLE")
    private String title;

    // 审批单ID
    @JsonProperty("WFINSTANCEID")
    private String wfinstanceId;

    // 申请事由
    @JsonProperty("REMARK")
    private String remark;

    //行程开始时间
    @JsonProperty("TRIPBEGINTIME")
    private String tripBeginTime;

    //行程结束时间
    @JsonProperty("TRIPENDTIME")
    private String tripEndTime;

    //新审批单号
    @JsonProperty("TRIPID")
    private String tripId;

    //同住人
    @JsonProperty("TZR")
    private String tzr;
}
