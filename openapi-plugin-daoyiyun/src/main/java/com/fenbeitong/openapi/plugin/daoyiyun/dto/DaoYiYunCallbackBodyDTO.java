package com.fenbeitong.openapi.plugin.daoyiyun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * create on 2022-06-03 10:49:13
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunCallbackBodyDTO {

    /**
     * 表单字段值集合
     */
    private Map<String, Object> variables;

    /**
     * 表单id
     */
    private String id;

    /**
     * 表单数据版本号
     */
    private Integer version;

    /**
     * 表单标题
     */
    private String formTitle;

    /**
     * 表单定义id
     */
    private String processInstanceId;

    /**
     * 流程实例id
     */
    private String processDefinitionId;

    /**
     * 流程定义id
     */
    private String formDefinitionId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 创建人id
     */
    private String author;

    /**
     * 创建人名称
     */
    private String authorName;

    /**
     * 创建日期
     */
    private Long createDate;

    /**
     * 最后修改日期
     */
    private Long lastModifyDate;

    /**
     * 最后修改人id
     */
    private String lastModifier;

    /**
     * 最后修改人名称
     */
    private String lastModifierName;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 原单据id对应的属性名
     */
    private String mainApplicationIdKey;

}
