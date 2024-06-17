package com.fenbeitong.openapi.plugin.yiduijie.model.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: CreateAppReqDTO</p>
 * <p>Description: 创建app请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 2:09 PM
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "创建应用", description = "创建应用对象")
public class CreateAppReqDTO implements Serializable {

    /**
     * 公司id
     */
    @NotBlank(message = "公司id(company_id)不可为空")
    @ApiModelProperty(value = "公司id", name = "company_id", required = true, position = 1)
    private String companyId;

    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称(company_name)不可为空")
    @ApiModelProperty(value = "公司名称", name = "company_name", required = true, position = 2)
    private String companyName;

    /**
     * 应用id
     */
    @NotBlank(message = "应用id(app_id)不可为空")
    @ApiModelProperty(value = "应用id", name = "app_id", required = true, position = 3)
    private String appId;

    /**
     * 应用标题
     */
    @NotBlank(message = "应用标题(app_title)不可为空")
    @ApiModelProperty(value = "应用标题", name = "app_title", required = true, position = 4)
    private String appTitle;

}
