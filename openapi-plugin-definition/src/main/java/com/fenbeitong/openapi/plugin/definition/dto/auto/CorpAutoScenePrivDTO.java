package com.fenbeitong.openapi.plugin.definition.dto.auto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CorpAutoScenePrivDTO {

    /**
     * 公司ID
     */
    private String companyId;
    /**
     * 预定权限
     */
    private int privType;
    /**
     * 权限类型，用于匹配第三方传递具体人员具体规则类型
     */
    private String roleType;
    /**
     * 场景
     */
    private String scene;
    /**
     * 添加乘客信息控制
     */
    private int oneselfLimit;
    /**
     * 行程审批控制
     */
    private boolean verifyFlag;
    /**
     * 订单审批
     */
    @JsonProperty( "order_verify_flag")
    private boolean orderVerifyFlag;

    /**
     * 规则控制
     */
    private boolean ruleLimitFlag;
    /**
     * 普通规则ID
     */
    private String ruleId;
    /**
     * 申请用车规则ID
     */
    private String applyRuleId;
    /**
     * 超规控制
     */
    private int exceedBuyType;
    /**
     * 是否可以超规下单
     */
    @JsonProperty( "exceed_buy_flag")
    private boolean exceedBuyFlag;
    /**
     * 退票控制
     */
    @JsonProperty( "refund_ticket_type")
    private int refundTicketType;
    /**
     * 改签控制
     */
    @JsonProperty( "changes_ticket_type")
    private int changesTicketType;
    /**
     * 用车接送机控制
     */
    @JsonProperty( "allow_shuttle")
    private boolean allowShuttle;
    /**
     * 个人支付控制
     */
    @JsonProperty( "personal_pay")
    private boolean personalPay;

}
