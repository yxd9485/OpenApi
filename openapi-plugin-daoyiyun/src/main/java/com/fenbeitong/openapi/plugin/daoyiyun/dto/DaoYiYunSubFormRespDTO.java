package com.fenbeitong.openapi.plugin.daoyiyun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 子表数据resp
 * create on 2022-06-03 14:23:13
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunSubFormRespDTO extends DaoYiYunBaseRespDTO {

    /**
     * 子表数据列表
     */
    private List<SubForm> data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubForm {

        /**
         * 流程实例id
         */
        private String processInstanceId;

        /**
         * 流程定义id
         */
        private String processDefinitionId;

        private String processCurrentApprovers;

        /**
         * 表单字段值集合
         */
        private Map<String, Object> variables;

        private String processStatus;

        private String processState;

        /**
         * 创建人id
         */
        private String author;

        /**
         * 最后修改人id
         */
        private String lastModifier;

        /**
         * 最后修改人名称
         */
        private String lastModifierName;

        /**
         * 表单数据版本号
         */
        private Integer version;

        /**
         * 创建人名称
         */
        private String authorName;

        private String dataStaticUrl;

        /**
         * 表单定义id
         */
        private String formDefinitionId;

        /**
         * 表单id
         */
        private String id;

        /**
         * 应用ID
         */
        private String applicationId;

        /**
         * 最后修改时间
         */
        private Integer lastModifyDate;

        /**
         * 任务id
         */
        private String taskId;

        /**
         * 创建时间
         */
        private Integer createDate;

    }
}
