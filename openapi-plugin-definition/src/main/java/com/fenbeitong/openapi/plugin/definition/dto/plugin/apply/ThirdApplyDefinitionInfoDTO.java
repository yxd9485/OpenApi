package com.fenbeitong.openapi.plugin.definition.dto.plugin.apply;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.employee.entity.OpenEmployeeRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 插件集成三方审批配置
 * Created by log.chang on 2019/12/25.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdApplyDefinitionInfoDTO {

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
     * @see OpenEmployeeRoleType roleType
     */
    @JsonProperty("process_type")
    private Integer processType;

}
