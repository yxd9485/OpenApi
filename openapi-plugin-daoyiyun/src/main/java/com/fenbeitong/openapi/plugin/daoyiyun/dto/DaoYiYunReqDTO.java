package com.fenbeitong.openapi.plugin.daoyiyun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 道一云回调
 * @author xiaohai
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunReqDTO {

    /**
     * 表单id
     */
    private String applicationId;
    /**
     * 审批状态key（字段中文名称）
     */
    private String isApprovedKey;
    /**
     * 原单信息key（字段中文名称）
     */
    private String mainApplicationIdKey;
    /**
     * 审批通过对应值
     */
    private String isApprovedValue;
    /**
     * 审批作废对应值
     */
    private String isCancelValue;
    /**
     * 非空字段
     */
    private String notNullKey;


}
