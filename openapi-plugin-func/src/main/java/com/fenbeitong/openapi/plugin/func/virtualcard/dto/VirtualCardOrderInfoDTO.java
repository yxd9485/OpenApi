package com.fenbeitong.openapi.plugin.func.virtualcard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName VirtualCardOrderInfoDTO
 * @Description 虚拟卡订单信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/22 上午10:22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCardOrderInfoDTO {
    //商户名称
    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("total_price")
    //交易金额、
    private BigDecimal totalPrice;

    //交易类型、
    @JsonProperty("transaction_type")
    private Integer transactionType;
    //交易标号、
    @JsonProperty("order_id")
    private String orderId;

    //关联消费编号、
    @JsonProperty("fb_order_id")
    private String fbOrderId;
    //卡号、
    @JsonProperty("bank_account_no")
    private String bankAccountNo;

    //开户行、
    @JsonProperty("bank_name")
    private String bankName;
    //交易人分贝通ID、
    @JsonProperty("employee_id")
    private String employeeId;

    //交易人三方ID、
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;

    //交易时间、
    @JsonProperty("create_time")
    private Date createTime;

    //状态、
    @JsonProperty("check_status")
    private Integer checkStatus;

    //备注
    @JsonProperty("remarks")
    private String remarks;
}
