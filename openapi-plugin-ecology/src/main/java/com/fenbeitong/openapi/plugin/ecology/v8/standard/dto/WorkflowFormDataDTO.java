package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: WorkflowFormDataDTO</p>
 * <p>Description: 工作流表单DTO</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/22 4:49 PM
 */
@Data
public class WorkflowFormDataDTO {

    @JsonProperty("field_order")
    private Integer fieldOrder;

    @JsonProperty("field_id")
    private String fieldId;

    @JsonProperty("field_name")
    private String fieldName;

    @JsonProperty("field_value")
    private String fieldValue;

    @JsonProperty("field_show_name")
    private String fieldShowName;

    @JsonProperty("field_show_value")
    private String fieldShowValue;

    @JsonProperty("filed_html_show")
    private String filedHtmlShow;
}
