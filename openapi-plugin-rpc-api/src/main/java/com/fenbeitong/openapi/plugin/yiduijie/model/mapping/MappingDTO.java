package com.fenbeitong.openapi.plugin.yiduijie.model.mapping;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Title: MappingDTO</p>
 * <p>Description: 映射信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 6:59 PM
 */
@Data
@ApiModel(value = "映射信息", description = "映射信息对象")
public class MappingDTO implements Serializable {

    @ApiModelProperty(value = "主键", name = "id", required = true, position = 1)
    private String id;

    @ApiModelProperty(value = "被映射信息", name = "src_name", required = true, position = 2)
    private String srcName;

    @ApiModelProperty(value = "映射目标", name = "dest_name", required = true, position = 3)
    private String destName;

    @ApiModelProperty(value = "扩展字段1", name = "ext_value1", required = true, position = 4)
    private String extValue1;

    @ApiModelProperty(value = "扩展字段2", name = "ext_value2", required = true, position = 4)
    private String extValue2;

    @ApiModelProperty(value = "扩展字段3", name = "ext_value3", required = true, position = 4)
    private String extValue3;

    @ApiModelProperty(value = "扩展字段4", name = "ext_value4", required = true, position = 4)
    private String extValue4;

    @ApiModelProperty(value = "扩展字段5", name = "ext_value5", required = true, position = 4)
    private String extValue5;
}
