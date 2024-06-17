package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.noc.api.service.base.BasePageReqDTO;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PublicPayQueryReqDTO extends BasePageReqDTO {

    /**
     * 交易类型: 1付款2退款3冲正4撤销冲正 （银行业务类型）
     *  @see com.fenbeitong.finhub.common.constant.BankHuPoTxnType
     */
    @NotNull(message = "交易类型[bankHupoTransType]不可为空")
    private Integer bankHupoTransType;

    /**
     * @see com.fenbeitong.noc.api.service.constant.enums.BankFSMStatus
     * 状态
     */
    private Integer orderStatus;

    /**
     * 交易流水创建时间---开始时间 yyyy-MM-dd HH:mm:ss
     */
    private String createBegin;

    /**
     * 交易流水创建时间---结束时间 yyyy-MM-dd HH:mm:ss
     */
    private String createEnd;

    /**
     * 公司ID
     */
    private String companyId;

    /**
     * 付款银行卡号
     */
    private String bankAccountNo;

    /**
     * 收款方名称
     */
    private String receiverName;

    /**
     * 收款方开户行
     */
    private String receiverBank;

    /**
     * 收款方账户
     */
    private String receiverAccount;

    /**
     * 收款方户名
     */
    private String receiverAccountName;

    /**
     * 付款人
     */
    private String userName;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 付款账户名称
     */
    private String bankAccountAcctName;

    private String orderId;


    /**
     * 起始页
     */
    @JsonProperty("page_index")
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
