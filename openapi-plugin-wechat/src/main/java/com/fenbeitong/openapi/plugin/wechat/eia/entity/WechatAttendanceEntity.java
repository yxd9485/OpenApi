package com.fenbeitong.openapi.plugin.wechat.eia.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 微信考勤记录
 *
 * @author duhui
 * @email ${email}
 * @date 2021-02-07 17:07:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_attendance_wechat")
public class WechatAttendanceEntity {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 企业id
     */
    @Column(name = "CORP_ID")
    private String corp_id;

    /**
     * 考勤表id
     */
    @Column(name = "MAIN_ID")
    private Long mainId;
    /**
     * 用户ID
     */
    @Column(name = "USER_ID")
    private String userId;
    /**
     * 打卡规则名称
     */
    @Column(name = "GROUP_NAME")
    private String groupName;
    /**
     * 打卡类型。字符串，目前有：上班打卡，下班打卡，外出打卡
     */
    @Column(name = "CHECKIN_TYPE")
    private String checkinType;
    /**
     * 异常类型，字符串，包括：时间异常，地点异常，未打卡，wifi异常，非常用设备。如果有多个异常，以分号间隔
     */
    @Column(name = "EXCEPTION_TYPE")
    private String exceptionType;
    /**
     * 打卡时间
     */
    @Column(name = "CHECKIN_TIME")
    private Date checkinTime;
    /**
     * 打卡地点title
     */
    @Column(name = "LOCATION_TITLE")
    private String locationTitle;
    /**
     * 打卡地点详情
     */
    @Column(name = "LOCATION_DETAIL")
    private String locationDetail;
    /**
     * 打卡备注
     */
    @Column(name = "NOTES")
    private String notes;
    /**
     * 标准打卡时间，指此次打卡时间对应的标准上班时间或标准下班时间
     */
    @Column(name = "SCH_CHECKIN_TIME")
    private Date schCheckinTime;
    /**
     * 规则id，表示打卡记录所属规则的id
     */
    @Column(name = "GROUP_ID")
    private Long groupId;
    /**
     * 时段id，表示打卡记录所属规则中，某一班次中的某一时段的id，如上下班时间为9:00-12:00、13:00-18:00的班次中，9:00-12:00为其中一组时段
     */
    @Column(name = "TIMELINE_ID")
    private Long timelineId;
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

}
