package com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/12/26 下午10:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeishuCalendarRespDTO {

    private Integer code;
    private String msg;
    private CalendarData data;

    @Data
    public static class Calendar{
        @JsonProperty("calendar_id")
        private String calendarId;
        private String summary;
        private String description;
        private String permissions;
        private int color;
        private String type;
        @JsonProperty("summary_alias")
        private String summaryAlias;
        @JsonProperty("is_deleted")
        private boolean isDeleted;
        @JsonProperty("is_third_party")
        private boolean isThirdParty;
        private String role;
    }

    @Data
    public static class CalendarData{
        private Calendar calendar;
    }

}
