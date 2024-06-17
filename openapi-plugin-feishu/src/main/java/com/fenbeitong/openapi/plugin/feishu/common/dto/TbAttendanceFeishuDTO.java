package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * Created by zhangpeng on 2021/09/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbAttendanceFeishuDTO {
    /**
     * 考勤表id
     */
    @JsonProperty("main_id")
    private Long mainId;
    /**
     * 飞书打卡唯一ID
     */
    @JsonProperty("feishu_check_id")
    private Long feishuCheckId;
    /**
     * 打卡记录ID
     */
    @JsonProperty("record_id")
    private Long recordId;
    /**
     * 考勤组ID
     */
    @JsonProperty("group_id")
    private Long groupId;
    /**
     * 排班ID
     */
    @JsonProperty("plan_id")
    private Long planId;
    /**
     * 考勤类型 OnDuty:上班;OffDuty:下班
     */
    @JsonProperty("check_type")
    private String checkType;
    /**
     * 飞书公司ID
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 飞书用户ID
     */
    @JsonProperty("user_id")
    private String userId;
    /**
     * 工作日
     */
    @JsonProperty("work_date")
    private Date workDate;
    /**
     * 标准(签到/签退)时间
     */
    @JsonProperty("base_check_time")
    private Date baseCheckTime;
    /**
     * 用户(签到/签退)时间
     */
    @JsonProperty("user_check_time")
    private Date userCheckTime;
    /**
     * 时间结果
     */
    @JsonProperty("time_result")
    private Integer timeResult;
    /**
     * 时间结果描述
     */
    @JsonProperty("time_result_desc")
    private String timeResultDesc;
    /**
     * 位置结果
     */
    @JsonProperty("location_result")
    private Integer locationResult;
    /**
     * 位置结果描述
     */
    @JsonProperty("location_result_desc")
    private String locationResultDesc;
    /**
     * 数据来源
     */
    @JsonProperty("source_type")
    private String sourceType;
    /**
     * 关联的审批id
     */
    @JsonProperty("approve_id")
    private String approveId;
    /**
     * 关联的审批实例id
     */
    @JsonProperty("proc_inst_id")
    private String procInstId;
    /**
     * 打卡地址
     */
    @JsonProperty("user_address")
    private String userAddress;
}
