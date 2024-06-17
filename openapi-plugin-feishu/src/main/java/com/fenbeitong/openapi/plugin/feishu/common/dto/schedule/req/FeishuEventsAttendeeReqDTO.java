package com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:日程参与人员
 * @Author: xiaohai
 * @Date: 2021/12/26 下午10:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeishuEventsAttendeeReqDTO {

    private List<Attendees> attendees;
    @JsonProperty("need_notification")
    private boolean needNotification;

    @Data
    public static class Options{
        @JsonProperty("option_key")
        private String optionKey;
        @JsonProperty("others_content")
        private String othersContent;
    }

    @Data
    public static class ResourceCustomization{
        @JsonProperty("index_key")
        private String indexKey;
        @JsonProperty("input_content")
        private String inputContent;
        private List<Options> options;
    }

    @Data
    public static class Attendees{
        private String type;
        @JsonProperty("is_optional")
        private boolean isOptional;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("chat_id")
        private String chatId;
        @JsonProperty("room_id")
        private String roomId;
        @JsonProperty("third_party_email")
        private String thirdPartyEmail;
        @JsonProperty("operate_id")
        private String operateId;
        @JsonProperty("resource_customization")
        private List<ResourceCustomization> resourceCustomization;
    }


}
