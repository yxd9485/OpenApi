package com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/12/26 下午10:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeishuEventsRespDTO {

    private int code;
    private String msg;
    private EventData data;

    @Data
    public static class TimeInfo{
        private String date;
        private String timestamp;
        private String timezone;
    }

    @Data
    public static class Vchat{
        @JsonProperty("vc_type")
        private String vcType;
        @JsonProperty("icon_type")
        private String iconType;
        private String description;
        @JsonProperty("meeting_url")
        private String meetingUrl;
    }

    @Data
    public static class Location{
        private String name;
        private String address;
        private double latitude;
        private double longitude;
    }

    @Data
    public static class Reminders{
        private int minutes;
    }

    @Data
    public static class Schemas{
        @JsonProperty("ui_name")
        private String uiName;
        @JsonProperty("ui_status")
        private String uiStatus;
        @JsonProperty("app_link")
        private String appLink;
    }

    @Data
    public static class Event{
        @JsonProperty("event_id")
        private String eventId;
        private String summary;
        private String description;
        @JsonProperty("need_notification")
        private boolean needNotification;
        @JsonProperty("start_time")
        private TimeInfo startTime;
        @JsonProperty("end_time")
        private TimeInfo endTime;
        private Vchat vchat;
        private String visibility;
        @JsonProperty("attendee_ability")
        private String attendeeAbility;
        @JsonProperty("free_busy_status")
        private String freeBusyStatus;
        private Location location;
        private int color;
        private List<Reminders> reminders;
        private String recurrence;
        private String status;
        @JsonProperty("is_exception")
        private boolean isException;
        @JsonProperty("recurring_event_id")
        private String recurringEventId;
        private List<Schemas> schemas;
    }

    @Data
    public static class EventData{
        private Event event;
    }

}
