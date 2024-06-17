package com.fenbeitong.openapi.plugin.daoyiyun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * create on 2022-06-06 20:38:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunMainFormRespDTO {


    private MainForm data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MainForm {

        /**
         * 应用id
         */
        private String applicationId;

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
        private String createDate;

        /**
         * 表单定义id
         */
        private String formDefinitionId;

        /**
         * 表单id
         */
        private String id;

        /**
         * 最后修改人名称
         */
        private String lastModifierName;

        /**
         * 最后修改日期
         */
        private String lastModifyDate;

        private Map<String, Object> prettyValue;

        /**
         * 表单字段值集合
         */
        private Map<String, Object> variables;

        /**
         * 表单数据版本号
         */
        private Integer version;


    }



}
