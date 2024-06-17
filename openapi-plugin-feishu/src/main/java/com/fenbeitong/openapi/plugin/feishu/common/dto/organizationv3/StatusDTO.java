package com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书新版人员状态DTO
 * @author zhangpeng
 * @date 2022/4/20 11:47 上午
 */
@Data
public class StatusDTO {

    @JsonProperty("is_frozen")
    private Boolean isFrozen;
    @JsonProperty("is_resigned")
    private Boolean isResigned;
    @JsonProperty("is_activated")
    private Boolean isActivated;
    @JsonProperty("is_exited")
    private Boolean isExited;
    @JsonProperty("is_unjoin")
    private Boolean isUnjoin;
}
