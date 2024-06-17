package com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveGuest;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: SipaiTripApplyDetailDTO</p>
 * <p>Description: 思派出差信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 7:04 PM
 */
@Data
public class SipaiTripApplyDetailDTO {

    /**
     * 出发城市
     */
    @JsonProperty("cfcs1")
    private String cfcs;

    /**
     * 出差地点
     */
    @JsonProperty("cccs1")
    private String ccdd;

    /**
     * 姓名
     */
    @JsonProperty("detail_resourceId")
    private String detailResourceId;

    /**
     * 部门
     */
    @JsonProperty("detail_departmentId")
    private String detailDepartmentId;

    /**
     * 开始日期
     */
    @JsonProperty("detail_fromDate")
    private String detailFromdate;

    /**
     * 开始时间
     */
    @JsonProperty("detail_fromTime")
    private String detailFromtime;

    /**
     * 结束日期
     */
    @JsonProperty("detail_toDate")
    private String detailToDate;

    /**
     * 结束时间
     */
    @JsonProperty("detail_toTime")
    private String detailToTime;

    /**
     * 时长
     */
    @JsonProperty("detail_duration")
    private String detailDuration;

    /**
     * 同行人
     */
    @JsonProperty("detail_companion")
    private String detailCompanion;


    /**
     * 同行人用户编号
     */
    @JsonProperty("detail_companion_user_code_list")
    private List<String> detailCompanionUserCodeList;

    /**
     * 出行人列表
     */
    @JsonProperty("guest_list")
    private List<TripApproveGuest> guestList;

}
