package com.fenbeitong.openapi.plugin.definition.dto.plugin.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三方对接用户权限信息
 * Created by log.chang on 2019/12/14.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDefinitionInfoDTO {

    /**
     * 配置id
     */
    private Integer id;
    /**
     * 企业id
     */
    @JsonProperty("app_id")
    private String appId;
    /**
     * 场景
     */
    private String scene;
    /**
     * 用户角色（三方定义，用于匹配人员对应的权限）
     */
    @JsonProperty("role_type")
    private Integer roleType;
    /**
     * 权限描述
     */
    @JsonProperty("permission_json")
    private String permissionJson;

}
