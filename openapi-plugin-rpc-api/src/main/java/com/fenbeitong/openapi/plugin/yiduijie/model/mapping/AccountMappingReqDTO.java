package com.fenbeitong.openapi.plugin.yiduijie.model.mapping;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: AccountMappingReqDTO</p>
 * <p>Description: 科目映射请求</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 12:02 PM
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "科目映射", description = "科目映射请求对象")
public class AccountMappingReqDTO implements Serializable {

    @NotBlank(message = "三方映射ID(id)不可为空", groups = UpdateAccountGroup.class)
    @ApiModelProperty(value = "三方映射ID", name = "id", required = true)
    private String id;

    /**
     * 财务部门
     */
    @NotBlank(message = "财务部门(finance_department)不可为空")
    @ApiModelProperty(value = "财务部门", name = "finance_department", required = true)
    private String financeDepartment;

    /**
     * 费用类别
     */
    @NotBlank(message = "费用类别(fee_type)不可为空")
    @ApiModelProperty(value = "费用类别", name = "fee_type", required = true)
    private String feeType;

    /**
     * 高级映射字段 事由
     */
    @ApiModelProperty(value = "高级映射字段", name = "super_mapping_field")
    private String superMappingField;

    /**
     * 高级映射字段1 项目
     */
    @ApiModelProperty(value = "高级映射字段1", name = "super_mapping_field1")
    private String superMappingField1;

    /**
     * 会计科目
     */
    @NotBlank(message = "会计科目(account)不可为空")
    @ApiModelProperty(value = "会计科目", name = "account", required = true)
    private String account;

    public interface UpdateAccountGroup {

    }
}
