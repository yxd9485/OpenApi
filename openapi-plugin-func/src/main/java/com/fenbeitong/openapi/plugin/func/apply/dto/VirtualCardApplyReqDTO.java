package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 虚拟卡申请单提交请求参数
 * Created by log.chang on 2020/4/28.
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class VirtualCardApplyReqDTO {
    @Valid
    @NotNull(message = "申请说明描述[reason_desc]不可为空")
    @JsonProperty("reason_desc")
    private String reasonDesc; // 申请说明描述
    @Valid
    @NotNull(message = "请额度[budget]不可为空")
    private Integer budget; // 请额度（单位是分）
    @Valid
    @NotNull(message = "审批单id[apply_id]不可为空")
    @JsonProperty("apply_id")
    private String applyId; // 第三方审批单id
    @Valid
//    @NotNull(message = "银行名称[bank_name]不可为空")
    @JsonProperty("bank_name")
    private String bankName;
    @Valid
//    @NotNull(message = "审批单归属id[apply_attribution_id]不可为空")
    @JsonProperty("apply_attribution_id")
    private String applyAttributionId; // 审批单费用归属id
    @Valid
//    @NotNull(message = "审批单归属名称[apply_attribution_name]不可为空")
    @JsonProperty("apply_attribution_name")
    private String applyAttributionName; // 审批单费用归属name
    @Valid
//    @NotNull(message = "审批单归属类型[apply_attribution_category]不可为空")
    @JsonProperty("apply_attribution_category")
    private Integer applyAttributionCategory;// 审批单费用归属类型 1.部门 2.项目

    @Valid
//    @NotNull(message = "费用归属id[cost_attribution_id]不可为空")
    @JsonProperty("cost_attribution_id")
    private String costAttributionId; // 费用归属id
    @Valid
//    @NotNull(message = "费用归属名称[cost_attribution_name]不可为空")
    @JsonProperty("cost_attribution_name")
    private String costAttributionName; // 费用归属名称
    @Valid
//    @NotNull(message = "费用归属类型[cost_attribution_category]不可为空")
    @JsonProperty("cost_attribution_category")
    private Integer costAttributionCategory; // 费用归属类型
    @Valid
//    @NotNull(message = "虚拟卡类型[type]不可为空")
    @JsonProperty("type")
    private Integer type;//虚拟卡类型,1:普通模式，2：备用金模式
    //备用金分类，3是循环备用金
    @JsonProperty("sub_type")
    private Integer subType;
}
