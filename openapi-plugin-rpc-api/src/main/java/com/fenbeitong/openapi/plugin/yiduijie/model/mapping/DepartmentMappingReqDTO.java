package com.fenbeitong.openapi.plugin.yiduijie.model.mapping;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: DepartmentMappingReqDTO</p>
 * <p>Description: 部门映射请求</p>
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
@ApiModel(value = "部门映射", description = "部门映射请求对象")
public class DepartmentMappingReqDTO implements Serializable {

    @NotBlank(message = "三方映射ID(id)不可为空", groups = UpdateDepartmentGroup.class)
    @ApiModelProperty(value = "三方映射ID", name = "id", required = true)
    private String id;

    /**
     * 财务部门
     */
    @NotBlank(message = "财务部门(finance_department)不可为空")
    @ApiModelProperty(value = "财务部门", name = "finance_department", required = true)
    private String financeDepartment;

    /**
     * 业务部门
     */
    @NotBlank(message = "业务部门(business_department)不可为空")
    @ApiModelProperty(value = "业务部门", name = "business_department", required = true)
    private String businessDepartment;

    /**
     * 是否是公司
     */
    private Boolean isCompany;

    public interface UpdateDepartmentGroup {

    }

}
