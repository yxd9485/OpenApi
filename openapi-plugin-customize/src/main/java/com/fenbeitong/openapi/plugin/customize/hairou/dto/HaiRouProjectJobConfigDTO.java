package com.fenbeitong.openapi.plugin.customize.hairou.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :zhiqiang.zhang
 * @title: HaiRouProjectJobConfigDTO
 * @projectName openapi-plugin
 * @description: 海柔项目数据同步请求实体类
 * @date 2022/5/20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "海柔项目数据同步请求实体")
public class HaiRouProjectJobConfigDTO {

    @ApiModelProperty(value = "接口账号",required=true)
    private String systemId;

    @ApiModelProperty(value = "接口密码",required=true)
    private String systemPassword;

    @ApiModelProperty(value = "接口请求主机,如http://210.21.218.14:8066",required=true)
    private String urlHost;

    @ApiModelProperty(value = "当前数据操作者,目前默认为1",required=true)
    private String operator;

    @ApiModelProperty(value = "是否执行批量更新,初始化值给false,需要全量更新时再给true",required=true)
    public boolean isForceUpdate;

    @ApiModelProperty(value = "公司id",required=true)
    public String companyId;

}
