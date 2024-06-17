package com.fenbeitong.openapi.plugin.customize.zhiou.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ApplyRequestDTO
 * @Description 北森参数配置类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeisenParamConfig {

    /**
     * 公司Id
     */
    private String companyId;

    /**
     * 权限顶级Id
     */
    private String parentId;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * app_id
     */
    private String appId;

    /**
     * 密钥
     */
    private String secret;

    /**
     * key
     */
    private String key;
    /**
     * 权限类型
     */
    private String grantType;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 是否新的tokenUrl
     */
    private Boolean tokenUrlIsNew = false;

}
