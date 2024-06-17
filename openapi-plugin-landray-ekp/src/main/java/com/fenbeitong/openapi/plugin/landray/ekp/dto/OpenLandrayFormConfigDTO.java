package com.fenbeitong.openapi.plugin.landray.ekp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by zhangpeng on 2021/08/05.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenLandrayFormConfigDTO {
    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 审批类型
     */
    @JsonProperty("appprove_type")
    private String appproveType;
    /**
     * 蓝凌表单ID
     */
    @JsonProperty("template_id")
    private String templateId;
    /**
     * 
     */
    @JsonProperty("is_ding")
    private Integer isDing;
}
