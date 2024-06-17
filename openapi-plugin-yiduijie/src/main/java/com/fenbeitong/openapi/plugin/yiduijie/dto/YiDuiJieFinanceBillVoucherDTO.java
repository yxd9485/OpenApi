package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: YiDuiJieFinanceBillVoucherDTO</p>
 * <p>Description: 易对接账单数据</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/11 11:22 AM
 */
@Data
public class YiDuiJieFinanceBillVoucherDTO {

    private String summary;

    private List<YiDuiJieBillDTO> billList;

    @JsonIgnore
    public List<YiDuiJieBillDTO> getBillDtoList() {
        return billList;
    }

    public List<Map<String, Object>> getBillList() {
        if (ObjectUtils.isEmpty(billList)) {
            return Lists.newArrayList();
        }
        return billList.stream().map(bill -> {
            Map<String, Object> billMap = JsonUtils.toObj(JsonUtils.toJson(bill), Map.class);
            List<String> customFields = Lists.newArrayList(bill.getCustomField1(), bill.getCustomField2(), bill.getCustomField3()).stream().filter(customField -> !ObjectUtils.isEmpty(customField)).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(customFields)) {
                for (String customField : customFields) {
                    int index = customField.indexOf("-");
                    if (index < 0) {
                        continue;
                    }
                    billMap.put(customField.substring(0, index), customField.substring(index + 1));
                }
            }
            return billMap;
        }).collect(Collectors.toList());
    }

    @Data
    public static class YiDuiJieBillDTO {

        private String id;

        @JsonProperty("company_id")
        private String companyId;

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("business_name")
        private String businessName;

        @JsonProperty("employee_name")
        private String employeeName;

        @JsonProperty("employee_type")
        private Integer employeeType;

        @JsonProperty("org_unit_full_name")
        private String orgUnitFullName;

        @JsonProperty("cost_center_name")
        private String costCenterName;

        @JsonProperty("cost_center_code")
        private String costCenterCode;

        @JsonProperty("company_pay_price")
        private BigDecimal companyPayPrice;

        @JsonProperty("free")
        private BigDecimal free;

        @JsonProperty("total_price")
        private BigDecimal totalPrice;

        @JsonProperty("ticket_price")
        private BigDecimal ticketPrice;

        @JsonProperty("airrax")
        private BigDecimal airrax;

        @JsonProperty("fuel_tax")
        private BigDecimal fuelTax;

        @JsonProperty("refund_fee")
        private BigDecimal refundFee;

        private String reasons;

        @JsonProperty("order_create_date")
        private String orderCreateDate;

        @JsonIgnore
        private String customField1;

        @JsonIgnore
        private String customField2;

        @JsonIgnore
        private String customField3;
    }
}
