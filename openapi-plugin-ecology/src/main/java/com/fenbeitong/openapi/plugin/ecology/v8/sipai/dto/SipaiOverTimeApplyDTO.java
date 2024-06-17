package com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: SipaiOverTimeApplyDTO</p>
 * <p>Description: 思派加班申请单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/17 6:53 PM
 */
@Data
public class SipaiOverTimeApplyDTO {

    /**
     * 单据编号
     */
    @JsonProperty("djbh")
    private String applyNo;

    /**
     * 申请人
     */
    private String resourceId;

    /**
     * 部门
     */
    private String departmentId;

    /**
     * 拟加班开始日期时间 2020-04-16 16:35
     */
    @JsonProperty("njbksrqsj")
    private String expectFromDateTime;

    /**
     * 拟加班开始日期 2020-04-18
     */
    @JsonProperty("njbksrq")
    private String expectFromDate;

    /**
     * 拟加班开始时间 19:00
     */
    @JsonProperty("njbkssj")
    private String expectFromTime;

    /**
     * 实际加班开始日期
     */
    private String fromDate;

    /**
     * 实际加班开始时间
     */
    private String fromTime;

    /**
     * 拟加班结束日期时间 2020-04-16 16:35
     */
    @JsonProperty("njbjsrqsj")
    private String expectToDateTime;

    /**
     * 拟加班结束日期 2020-04-18
     */
    @JsonProperty("njbjsrq")
    private String expectToDate;

    /**
     * 拟加班结束时间 19:00
     */
    @JsonProperty("njbjssj")
    private String expectToTime;

    /**
     * 实际加班结束日期
     */
    private String toDate;

    /**
     * 实际加班结束时间
     */
    private String toTime;

    /**
     * 拟加班时长
     */
    @JsonProperty("njbsc")
    private String expectDuration;

    /**
     * 实际加班时长
     */
    private String duration;

    /**
     * 是否法定节假日
     */
    @JsonProperty("sffdjjr")
    private String holiday;

    /**
     * 加班事由
     */
    @JsonProperty("jbsy")
    private String applyRemark;

    /**
     * manager
     */
    private String manager;

    /**
     * 直接上级
     */
    @JsonProperty("zjsj")
    private String directSuperior;

}
