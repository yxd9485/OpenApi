package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by lizhen on 2020/4/13.
 */
@Data
public class JsapiSignRequest {

    @NotNull(message = "公司id[company_id]不可为空")
    @JsonProperty("company_id")
    private String companyId;

    private String data;

}
