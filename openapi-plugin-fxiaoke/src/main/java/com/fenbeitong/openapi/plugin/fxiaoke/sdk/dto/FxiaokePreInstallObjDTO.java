package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by hanshuqi on 2020/09/18.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokePreInstallObjDTO {
    /**
     * 企业ID
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 纷销客对象id
     */
    @JsonProperty("api_name")
    private String apiName;
    /**
     * 纷销客预置对象ID
     */
    @JsonProperty("pre_install_id")
    private String preInstallId;
    /**
     * 纷销客预置对象名称
     */
    @JsonProperty("pre_install_name")
    private String preInstallName;
    /**
     * 纷销客预置对象状态
     */
    @JsonProperty("pre_install_state")
    private Integer preInstallState;
}
