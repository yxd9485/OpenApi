package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 免登参数
 * @Auther zhang.peng
 * @Date 2021/8/25
 */
@Data
@Builder
public class DingtalkFreeLoginDto {

    private String userId;
    /**
     * 脚本配置key
     */
    private String configKey;
    /**
     * 脚本配置value
     */
    private String freeLoginLabelValue;
    private String companyId;
    /**
     * 是否有脚本配置
     */
    private boolean hasScriptConfig;
}
