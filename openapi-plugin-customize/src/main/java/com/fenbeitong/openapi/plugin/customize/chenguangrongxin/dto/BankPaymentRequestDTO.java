package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName BankPaymentRequestDTO
 * @Description 对公付款申请单参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/20
 **/
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class BankPaymentRequestDTO {
    /**
     * 申请单状态
     */
    @JsonProperty("payment_state")
    private Integer paymentState;
    /**
     * 申请单ID
     */
    @JsonProperty("apply_id")
    private String applyId;
}
