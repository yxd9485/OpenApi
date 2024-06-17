package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 部门同步请求DTO
 *
 * @author lizhen
 */
@Data
@ApiModel("部门列表请求体")
public class DepartmentRequestDTO {

    private String companyId;

    @NotNull
    @ApiModelProperty(value = "同步类型", required = true)
    private Integer type;

    @Size(max = 50, message = "departments列表最大50条")
    @NotEmpty(message = "departments列表不能为空")
    @ApiModelProperty(value = "departments列表", required = true)
    private List<DepartmentItem> departments;


    /**
     * 部门实体
     */
    @Data
    @ApiModel("部门实体")
    public static class DepartmentItem {

        @Size(max = 200)
        @NotBlank
        @ApiModelProperty("名称")
        private String name;

        @Size(max = 50)
        @NotBlank
        @JsonProperty("third_parent_id")
        @ApiModelProperty(value = "三方系统直属部门ID", required = true)
        private String thirdParentId;

        @Size(max = 50)
        @NotBlank
        @JsonProperty("third_id")
        @ApiModelProperty(value = "三方系统部门ID", required = true)
        private String thirdId;

        @JsonProperty("custom_field")
        @ApiModelProperty(value = "部门自定义字段")
        private Map<String, Object> customField;

        @Size(max = 200)
        @NotBlank
        @ApiModelProperty(value = "部门编码")
        private String code;

        @JsonProperty("custom_fields")
        @ApiModelProperty(value = "部门自定义字段")
        private List<CustomField> customFields;

        @JsonProperty("finance_department")
        @ApiModelProperty(value = "财务部门信息")
        @Size(max = 1, message = "financeDepartment列表最大1条")
        private List<FinanceDepartment> financeDepartment;

        @JsonProperty("third_manager_ids")
        @ApiModelProperty(value = "部门主管")
        private List<String> thirdManagerIds;
    }


    /**
     * 自定义字段实体
     */
    @Data
    @ApiModel("自定义字段实体")
    public static class CustomField {
        @Size(max = 50)
        @NotBlank
        @JsonProperty("id")
        @ApiModelProperty(value = "自定义字段ID", required = true)
        private String id;

        @Size(max = 50)
        @NotBlank
        @JsonProperty("detail")
        @ApiModelProperty(value = "自定义字段的内容", required = true)
        private String detail;
        /**
         * 字段类型
         * 1文本 2成员 默认文本
         * {@link com.fenbeitong.openapi.plugin.support.organization.constant.CustomFieldTypeEnum}
         */
        private Integer type = 1;

        /**
         * 自定义字段创建类型
         * 1公司创建，2集团创建；万泰定制使用集团自定义字段
         */
        @JsonProperty("create_type")
        private Integer createType = 2;
    }

    @Data
    @ApiModel("财务部门信息实体")
    public static class FinanceDepartment {

        @Size(max = 50)
        @NotBlank
        @JsonProperty("code")
        @ApiModelProperty(value = "财务部门编码", required = true)
        private String code;

        @Size(max = 50)
        @NotBlank
        @JsonProperty("name")
        @ApiModelProperty(value = "财务部门名称", required = true)
        private String name;

        @Size(max = 50)
        @NotBlank
        @JsonProperty("org_code")
        @ApiModelProperty(value = "所属财务组织编码", required = true)
        private String orgCode;

        @Size(max = 50)
        @NotBlank
        @JsonProperty("org_name")
        @ApiModelProperty(value = "所属财务组织名称", required = true)
        private String orgName;
    }
}
