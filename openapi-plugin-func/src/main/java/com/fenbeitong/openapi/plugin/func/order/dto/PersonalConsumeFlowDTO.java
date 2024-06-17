package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PersonalConsumeFlowDTO extends BaseDTO {

    private PersonalConsumeFlowSummaryInfoBean summaryInfoBean;

    private PersonalConsumeFlowOrderInfoBean orderInfoBean;

    private PersonalConsumeFlowUserInfoBean userInfoBean;

    private PersonalConsumeFlowVoucherInfoBean voucherInfoBean;

    @Data
    public static class PersonalConsumeFlowSummaryInfoBean extends BaseDTO {

        private String billId;

        private String billName;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date billBeginDate;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date billEndDate;

    }

    @Data
    public static class PersonalConsumeFlowOrderInfoBean extends BaseDTO {

        private String billDetailId;

        private String orderId;

        private String companyId;

        private Integer orderCategory;

        private BigDecimal orderPrice;

        private BigDecimal thirdPaymentPrice;

        private BigDecimal fbbPayPrice;

        private BigDecimal fbqPayPrice;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date orderCreateTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date orderToBillTime;

    }

    @Data
    public static class PersonalConsumeFlowUserInfoBean extends BaseDTO {

        private String id;

        private String name;

        private String phone;

        private String companyId;

        private String departmentId;

        private String departmentName;

        private String departmentFull;
    }

    @Data
    public static class PersonalConsumeFlowVoucherInfoBean extends BaseDTO {

        private Boolean isGrantVoucherTask;

        private String billDetailId;

        private String billId;

        private String orderId;

        private Integer accountModel;

        private String voucherId;

        private String voucherName;

        private String voucherFlowId;

        private Integer invoiceType;

        private BigDecimal voucherOriginalServiceFee;

        private BigDecimal voucherServiceFee;

        private BigDecimal voucherServiceFeeReduction;

        private BigDecimal voucherAdjustServiceFee;

        private BigDecimal voucherAmount;

        List<KeyValue<String, String>> voucherTypeList;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date voucherEffectiveTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date voucherExpiryTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date voucherConsumeTime;

        private String thirdExtJson;

        private List<PersonalConsumeFlowCostAttributionBean> costAttributionBeanList;

        private String systemExt;

        private String expandField;

        private String expandField2;

        private String expandField3;
    }

    @Data
    public static class PersonalConsumeFlowCostAttributionBean extends BaseDTO {

        private String costAttributionId;

        private String costAttributionName;

        private Integer costAttributionCategory;

        private String costAttributionPath;

        private String expandField;

        private String expandField2;

        private String expandField3;
    }
}
