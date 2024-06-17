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
 * Created by hanshuqi on 2020/07/05.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeObjApplyReqDTO {
    /**
     * 第三方公司ID
     */
    @NotBlank(message = "第三方公司ID[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 自定义对象API_NAME
     */
    @NotBlank(message = "自定义对象API_NAME[obj_api_name]不可为空")
    @JsonProperty("obj_api_name")
    private String objApiName;
    /**
     * api_name状态，0:可用，1:不可用
     */
    @NotNull(message = "api_name状态，0:可用，1:不可用[obj_state]不可为空")
    @JsonProperty("obj_state")
    private Integer objState;
    /**
     * 对象关联的审批单对象
     */
    @JsonProperty("obj_apply_api_name")
    private String objApplyApiName;
    /**
     * 审批单类型，1:差旅，12:用车
     */
    @NotBlank(message = "审批单类型，1:差旅，12:用车[obj_apply_type]不可为空")
    @JsonProperty("obj_apply_type")
    private String objApplyType;
}
