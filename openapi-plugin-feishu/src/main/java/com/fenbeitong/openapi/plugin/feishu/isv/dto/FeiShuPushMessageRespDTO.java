package com.fenbeitong.openapi.plugin.feishu.isv.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class FeiShuPushMessageRespDTO {

    /**
     * 发送的人员集合
     */
    @JsonProperty("sendCompanyList")
    private List<SendCompany> sendCompanyList;

    /**
     * 消息类型
     * interactive卡片类型 text文本类型
     */
    @JsonProperty("msg_type")
    private String msgType;

    /**
     * 文本类型消息的内容
     */
    @JsonProperty("content")
    private  Map<String,Object> content;

    /**
     * 是否是共享卡片（默认是false，如果为true的话更新卡片时所有人的卡片都会被更新）
     */
    @JsonProperty("update_multi")
    private Boolean updateMulti;

    /**
     * 卡片的具体内容
     */
    @JsonProperty("card")
    private  Map<String,Object> card;

    /**
     * 公司的加入时间
     * 定时任务
     */
    @JsonProperty("join_day")
    private Integer join_day;

    /**
     * 是否只给管理员发送
     * 定时任务
     */
    @JsonProperty("admin_only_whether")
    private Integer adminOnlyWhether;


//    /**
//     * 文本类型消息的内容
//     */
//    @JsonProperty("contentMap")
//    private Map<String,Object> contentMap;



    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendCompany {

        /**
         * 公司id
         */
        @JsonProperty("company_id")
        private String companyId;

        /**
         * 分贝通的员工ID
         */
        @JsonProperty("employee_id")
        private String employeeIds;
    }
}

