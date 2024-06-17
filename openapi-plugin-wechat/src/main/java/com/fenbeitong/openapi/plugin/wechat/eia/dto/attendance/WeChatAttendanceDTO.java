package com.fenbeitong.openapi.plugin.wechat.eia.dto.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.organization.dto.DepartmentReqDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: WeChatAttendance</p>
 * <p>Description: 微信考勤记录</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-02-07 16:35
 */

@Data
public class WeChatAttendanceDTO {

    @JsonProperty("errcode")
    public Integer errcode;

    @JsonProperty("errmsg")
    public String errmsg;

    @JsonProperty("checkindata")
    public List<DataBean> checkindata;

    @Data
    public static class DataBean {

        @JsonProperty("userid")
        public String userId;

        @JsonProperty("groupname")
        public String groupName;

        @JsonProperty("checkin_type")
        public String checkinType;

        @JsonProperty("exception_type")
        public String exceptionType;

        @JsonProperty("checkin_time")
        public Long checkinTime;

        @JsonProperty("location_title")
        public String locationTitle;

        @JsonProperty("location_detail")
        public String locationDetail;

        @JsonProperty("wifiname")
        public String wifiname;

        @JsonProperty("notes")
        public String notes;

        @JsonProperty("wifimac")
        public String wifimac;

        @JsonProperty("mediaids")
        public List<String> mediaids;

        @JsonProperty("lat")
        public Long lat;

        @JsonProperty("lng")
        public Long lng;

        @JsonProperty("deviceid")
        public String deviceId;

        @JsonProperty("sch_checkin_time")
        public Long schCheckinTime;

        @JsonProperty("groupid")
        public Long groupId;

        @JsonProperty("timeline_id")
        public Long timelineId;

    }

}
