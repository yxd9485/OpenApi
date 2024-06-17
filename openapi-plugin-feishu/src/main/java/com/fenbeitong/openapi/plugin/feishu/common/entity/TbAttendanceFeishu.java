package com.fenbeitong.openapi.plugin.feishu.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zhangpeng on 2021/09/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_attendance_feishu")
public class TbAttendanceFeishu {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 考勤表id
     */
    @Column(name = "MAIN_ID")
    private Long mainId;

    /**
     * 飞书打卡唯一ID
     */
    @Column(name = "FEISHU_CHECK_ID")
    private Long feishuCheckId;

    /**
     * 打卡记录ID
     */
    @Column(name = "RECORD_ID")
    private Long recordId;

    /**
     * 考勤组ID
     */
    @Column(name = "GROUP_ID")
    private Long groupId;

    /**
     * 排班ID
     */
    @Column(name = "PLAN_ID")
    private Long planId;

    /**
     * 考勤类型 OnDuty:上班;OffDuty:下班
     */
    @Column(name = "CHECK_TYPE")
    private String checkType;

    /**
     * 飞书公司ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 飞书用户ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 工作日
     */
    @Column(name = "WORK_DATE")
    private Date workDate;

    /**
     * 标准(签到/签退)时间
     */
    @Column(name = "BASE_CHECK_TIME")
    private Date baseCheckTime;

    /**
     * 用户(签到/签退)时间
     */
    @Column(name = "USER_CHECK_TIME")
    private Date userCheckTime;

    /**
     * 时间结果
     */
    @Column(name = "TIME_RESULT")
    private Integer timeResult;

    /**
     * 时间结果描述
     */
    @Column(name = "TIME_RESULT_DESC")
    private String timeResultDesc;

    /**
     * 位置结果
     */
    @Column(name = "LOCATION_RESULT")
    private Integer locationResult;

    /**
     * 位置结果描述
     */
    @Column(name = "LOCATION_RESULT_DESC")
    private String locationResultDesc;

    /**
     * 数据来源
     */
    @Column(name = "SOURCE_TYPE")
    private String sourceType;

    /**
     * 关联的审批id
     */
    @Column(name = "APPROVE_ID")
    private String approveId;

    /**
     * 关联的审批实例id
     */
    @Column(name = "PROC_INST_ID")
    private String procInstId;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 打卡地址
     */
    @Column(name = "USER_ADDRESS")
    private String userAddress;


}
