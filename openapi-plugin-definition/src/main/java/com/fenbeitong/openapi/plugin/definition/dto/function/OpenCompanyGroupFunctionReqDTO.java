package com.fenbeitong.openapi.plugin.definition.dto.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 企业功能配置
 * Created by lizhen on 2020/01/13.
 */
@Data
public class OpenCompanyGroupFunctionReqDTO {
    /**
     * 公司id
     */
    @NotBlank(message = "公司ID[app_id]不可为空")
    @JsonProperty("app_id")
    private String appId;
    /**
     * 功能簇子功能编码
     */
    @NotNull(message = "功能簇子功能ID[group_function_code]不可为空")
    @Size(min = 1, message = "功能簇子功能ID[group_function_code]不可为空")
    @JsonProperty("group_function_code")
    private List<String> groupFunctionCode;

}
