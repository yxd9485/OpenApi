package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * Created by zhang on 2021/01/14.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushMessageResultReqDTO {
    /**
     * 场景类型
     */
    @JsonProperty("open_type")
    private String openType;
    /**
     * 用户的三方平台id
     */
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;
    /**
     * 用户在分贝通的ID
     */
    @JsonProperty("employee_id")
    private String employeeId;
    /**
     * 用户在分贝通的公司ID
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 用户在三方平台的ID
     */
    @JsonProperty("corp_id")
    private String corpId;

    /**
     * 发送的json内容
     */
    @JsonProperty("send_content")
    private String sendContent;
    /**
     * 发送消息的类型
     */
    @JsonProperty("msg_type")
    private String msgType;
    /**
     * 是否发送成功  1是成功 0是失败
     */
    @JsonProperty("send_success")
    private Integer sendSuccess;
    /**
     * 发送失败次数
     */
    @JsonProperty("fail_num")
    private Integer failNum;

    /**
     * 消息ID
     */
    @JsonProperty("message_id")
    private Integer messageId;
}
