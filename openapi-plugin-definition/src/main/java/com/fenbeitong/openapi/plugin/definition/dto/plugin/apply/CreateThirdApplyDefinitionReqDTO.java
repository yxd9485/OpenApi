package com.fenbeitong.openapi.plugin.definition.dto.plugin.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 添加企业插件集成三方审批配置
 * Created by log.chang on 2019/12/25.
 */
@Data
public class CreateThirdApplyDefinitionReqDTO {

    /**
     * 企业id
     */
    @JsonProperty("app_id")
    private String appId;
    /**
     * 三方审批推送code（钉钉/企业微信）
     */
    @JsonProperty("third_process_code")
    private String thirdProcessCode;
    /**
     * 审批名称
     */
    @JsonProperty("third_process_name")
    private String thirdProcessName;
    /**
     * 企业审批配置类型
     *
     * @see com.fenbeitong.openapi.plugin.support.apply.constant.ProcessType
     */
    @JsonProperty("process_type")
    private Integer processType;

}
