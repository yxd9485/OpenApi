package com.fenbeitong.openapi.plugin.definition.dto.auto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加企业插件集成三方审批配置
 * Created by log.chang on 2019/12/25.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CorpAutoAccountConfigReqDTO {

    /**
     * 分贝通公司ID
     */
    private String fbtCompId;
    /**
     * 分贝通公司名称
     */
    private String fbtCompName;
    /**
     * 开通虚拟手机号
     */
    private String virtualPhone;
    /**
     * 备注
     */
    private String remark;

}
