package com.fenbeitong.openapi.plugin.yiduijie.model.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: Account</p>
 * <p>Description: 科目</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 6:39 PM
 */
@Data
@ApiModel(value = "科目", description = "科目对象")
public class Account implements Serializable {

    /**
     * 记录id
     */
    @NotBlank(message = "记录id(local_id)不可为空")
    @ApiModelProperty(value = "记录id", name = "local_id", required = true, position = 1)
    private String localId;

    /**
     * 科目编号
     */
    @NotBlank(message = "科目编号(local_number)不可为空")
    @ApiModelProperty(value = "科目编号", name = "local_number", required = true, position = 2)
    private String localNumber;

    /**
     * 科目名称
     */
    @NotBlank(message = "科目名称(name)不可为空")
    @ApiModelProperty(value = "科目名称", name = "name", required = true, position = 3)
    private String name;

    /**
     * 科目类型
     */
    @ApiModelProperty(value = "科目类型", name = "account_type", position = 4)
    private String accountType;

    /**
     * 上级科目唯一编码
     */
    @ApiModelProperty(value = "上级科目唯一编码", name = "parent_id", position = 5)
    private String parentId;

    /**
     * 是否员工核算
     */
    @ApiModelProperty(value = "是否员工核算", name = "employee", position = 6)
    private Boolean employee;

    /**
     * 是否部门核算
     */
    @ApiModelProperty(value = "是否部门核算", name = "department", position = 7)
    private Boolean department;

    /**
     * 是否项目核算
     */
    @ApiModelProperty(value = "是否项目核算", name = "project", position = 8)
    private Boolean project;

    /**
     * 是否客户核算
     */
    @ApiModelProperty(value = "是否客户核算", name = "customer", position = 7)
    private Boolean customer;

    /**
     * 是否供应商核算
     */
    @ApiModelProperty(value = "是否供应商核算", name = "supplier", position = 9)
    private Boolean supplier;

    /**
     * 是否客户辅助核算 1-20
     */
    @ApiModelProperty(value = "是否客户辅助核算1", name = "custom1", position = 10)
    private Boolean custom1;

    @ApiModelProperty(value = "是否客户辅助核算2", name = "custom2", position = 10)
    private Boolean custom2;

    @ApiModelProperty(value = "是否客户辅助核算3", name = "custom3", position = 10)
    private Boolean custom3;

    @ApiModelProperty(value = "是否客户辅助核算4", name = "custom4", position = 10)
    private Boolean custom4;

    @ApiModelProperty(value = "是否客户辅助核算5", name = "custom5", position = 10)
    private Boolean custom5;

    @ApiModelProperty(value = "是否客户辅助核算6", name = "custom6", position = 10)
    private Boolean custom6;

    @ApiModelProperty(value = "是否客户辅助核算7", name = "custom7", position = 10)
    private Boolean custom7;

    @ApiModelProperty(value = "是否客户辅助核算8", name = "custom8", position = 10)
    private Boolean custom8;

    @ApiModelProperty(value = "是否客户辅助核算9", name = "custom9", position = 10)
    private Boolean custom9;

    @ApiModelProperty(value = "是否客户辅助核算10", name = "custom10", position = 11)
    private Boolean custom10;

    @ApiModelProperty(value = "是否客户辅助核算11", name = "custom11", position = 11)
    private Boolean custom11;

    @ApiModelProperty(value = "是否客户辅助核算12", name = "custom12", position = 11)
    private Boolean custom12;

    @ApiModelProperty(value = "是否客户辅助核算13", name = "custom13", position = 11)
    private Boolean custom13;

    @ApiModelProperty(value = "是否客户辅助核算14", name = "custom14", position = 11)
    private Boolean custom14;

    @ApiModelProperty(value = "是否客户辅助核算15", name = "custom15", position = 11)
    private Boolean custom15;

    @ApiModelProperty(value = "是否客户辅助核算16", name = "custom16", position = 11)
    private Boolean custom16;

    @ApiModelProperty(value = "是否客户辅助核算17", name = "custom17", position = 11)
    private Boolean custom17;

    @ApiModelProperty(value = "是否客户辅助核算18", name = "custom18", position = 11)
    private Boolean custom18;

    @ApiModelProperty(value = "是否客户辅助核算19", name = "custom19", position = 11)
    private Boolean custom19;

    @ApiModelProperty(value = "是否客户辅助核算20", name = "custom20", position = 11)
    private Boolean custom20;

    /**
     * 科目扩展信息（预留字段）1-5
     */
    @ApiModelProperty(value = "科目扩展信息（预留字段）1", name = "ext1", position = 12)
    private String ext1;

    @ApiModelProperty(value = "科目扩展信息（预留字段）2", name = "ext2", position = 12)
    private String ext2;

    @ApiModelProperty(value = "科目扩展信息（预留字段）3", name = "ext3", position = 12)
    private String ext3;

    @ApiModelProperty(value = "科目扩展信息（预留字段）4", name = "ext4", position = 12)
    private String ext4;

    @ApiModelProperty(value = "科目扩展信息（预留字段）5", name = "ext5", position = 12)
    private String ext5;
}
