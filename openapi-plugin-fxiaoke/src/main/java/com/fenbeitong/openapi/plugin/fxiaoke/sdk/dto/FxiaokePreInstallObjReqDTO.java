package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by hanshuqi on 2020/09/18.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokePreInstallObjReqDTO {
    /**
     * 企业ID
     */
    @NotBlank(message = "企业ID[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 纷销客对象id
     */
    @NotBlank(message = "纷销客对象id[api_name]不可为空")
    @JsonProperty("api_name")
    private String apiName;
    /**
     * 纷销客预置对象ID
     */
    @NotBlank(message = "纷销客预置对象ID[pre_install_id]不可为空")
    @JsonProperty("pre_install_id")
    private String preInstallId;
    /**
     * 纷销客预置对象名称
     */
    @NotBlank(message = "纷销客预置对象名称[pre_install_name]不可为空")
    @JsonProperty("pre_install_name")
    private String preInstallName;
    /**
     * 纷销客预置对象状态
     */
    @NotNull(message = "纷销客预置对象状态[pre_install_state]不可为空")
    @JsonProperty("pre_install_state")
    private Integer preInstallState;
}
