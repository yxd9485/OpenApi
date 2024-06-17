package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuPushMessageDataDTO {
    /**
     * 公司id
     */
    private String companyId;

    /**
     * 飞书公司id
     */
    private String corpId;

//    /**
//     * 授权
//     */
//    private String authorization;

    /**
     * 分贝通的员工ID
     * 批量发送的时候需要，一次不超过200条
     */
    private List<PushEmployee> employees;

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
    private Map<String,Object> content;

    /**
     * 是否是共享卡片（默认是false，如果为true的话更新卡片时所有人的卡片都会被更新）
     */
    @JsonProperty("update_multi")
    private Boolean update_multi;

    /**
     * 卡片的具体内容
     */
    @JsonProperty("card")
    private Map<String,Object> card;

    @Data
    @Builder
    public static class PushEmployee{
        /**
         * 分贝通的员工ID
         */
        private String employeeId;

        /**
         * openid
         */
        private String openId;
    }
}
