package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatCreateApprovalReqDTO {
    @JsonProperty("creator_userid")
    private String creatorUserid;
    @JsonProperty("template_id")
    private String templateId;
    @JsonProperty("use_template_approver")
    private int useTemplateApprover;
    @JsonProperty("approver")
    private List<Approver> approver;
    @JsonProperty("notifyer")
    private List<String> notifyer;
    @JsonProperty("notify_type")
    private Integer notifyType;
    @JsonProperty("apply_data")
    private ApplyData applyData;
    @JsonProperty("summary_list")
    private List<SummaryInfo> summaryList;

    @Data
    public class Approver {
        @JsonProperty("attr")
        private String attr;
        @JsonProperty("userid")
        private List<String> userid;
    }

    @Data
    @Builder
    public static class ApplyData {
        @JsonProperty("contents")
        private List<Content> contens;
    }

    /**
     * 审批具体数据,每个content内容都一样
     */
    @Data
    @Builder
    public static class Content {
        @JsonProperty("control")
        private String control;
        @JsonProperty("id")
        private String id;
        @JsonProperty("value")
        private Title value;
        /**
         * 每个对象的value值不相同
         */

    }

    @Data
    @Builder
    public static class Title {
        @JsonProperty("text")
        private String text;
        @JsonProperty("new_number")
        private String newNumber;
        @JsonProperty("new_money")
        private String newMoney;
        @JsonProperty("date")
        private Date date;
        @JsonProperty("lang")
        private String lang;
    }

    @Data
    @Builder
    public static class Date {
        @JsonProperty("type")
        private String type;
        @JsonProperty("s_timestamp")
        private String ssTimestamp;
    }

    @Data
    @Builder
    public static class SummaryInfo {
        private List<Title> summaryInfo;
    }


}
