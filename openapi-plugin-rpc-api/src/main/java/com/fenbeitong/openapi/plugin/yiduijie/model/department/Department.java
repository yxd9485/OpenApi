package com.fenbeitong.openapi.plugin.yiduijie.model.department;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: Department</p>
 * <p>Description: 财务部门</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 6:49 PM
 */
@Data
@ApiModel(value = "财务部门", description = "财务部门对象")
public class Department implements Serializable {

    /**
     * 记录id
     */
    @NotBlank(message = "记录id(local_id)不可为空")
    @ApiModelProperty(value = "记录id", name = "local_id", required = true, position = 1)
    private String localId;

    /**
     * 部门编号
     */
    @NotBlank(message = "部门编号(local_number)不可为空")
    @ApiModelProperty(value = "部门编号", name = "local_number", required = true, position = 2)
    private String localNumber;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称(name)不可为空")
    @ApiModelProperty(value = "部门名称", name = "name", required = true, position = 3)
    private String name;

    /**
     * 部门类型
     */
    @ApiModelProperty(value = "部门类型", name = "department_type", position = 4)
    private String departmentType;

    /**
     * 上级部门唯一编码
     */
    @ApiModelProperty(value = "上级部门唯一编码", name = "parent_id", position = 5)
    private String parentId;

    /**
     * 部门扩展信息（预留字段）1-5
     */
    @ApiModelProperty(value = "部门扩展信息（预留字段）1", name = "ext1", position = 6)
    private String ext1;

    @ApiModelProperty(value = "部门扩展信息（预留字段）2", name = "ext2", position = 6)
    private String ext2;

    @ApiModelProperty(value = "部门扩展信息（预留字段）3", name = "ext3", position = 6)
    private String ext3;

    @ApiModelProperty(value = "部门扩展信息（预留字段）4", name = "ext4", position = 6)
    private String ext4;

    @ApiModelProperty(value = "部门扩展信息（预留字段）5", name = "ext5", position = 6)
    private String ext5;
}
