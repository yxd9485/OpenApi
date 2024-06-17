package com.fenbeitong.openapi.plugin.yiduijie.model.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: YiDuiJieConfigDTO</p>
 * <p>Description: 配置信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 11:00 AM
 */
@Data
public class ConfigDTO implements Serializable {

    private String data;

    private String description;

    private String id;

    private String inputType;

    private String name;

    private List<ConfigOption> options;

    private Boolean required;

    private String value;

    private List<String> values;

    @Data
    public static class ConfigOption implements Serializable {

        private Boolean disabled;

        private Boolean escape;

        private String label;

        private Boolean noSelectionOption;

        private String value;
    }
}
