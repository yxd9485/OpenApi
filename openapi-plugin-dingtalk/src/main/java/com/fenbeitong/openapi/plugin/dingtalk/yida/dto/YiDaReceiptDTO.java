package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 明细中的关联表单
 *
 * @author ctl
 * @date 2022/3/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YiDaReceiptDTO implements Serializable {

    @JsonProperty("formType")
    private String formType;
    @JsonProperty("formUuid")
    private String formUuid;
    @JsonProperty("instanceId")
    private String instanceId;
    @JsonProperty("subTitle")
    private String subTitle;
    @JsonProperty("appType")
    private String appType;
    @JsonProperty("title")
    private String title;

}
