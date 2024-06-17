package com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName OptBillDetailQueryResDTO
 * @Description 新版报销单详情返回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/6 下午2:28
 **/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenBillDetailQueryResDTO {
    /**
     * 自定义档案项目一
     */
    @JsonProperty("archive_code1")
    private String archiveCode1;

    /**order_category
     * 自定义档案项目二
     */
    @JsonProperty("archive_code2")
    private String archiveCode2;

    /**
     * 补充事由
     */
    private String remark;
    /**
     * 事由
     */
    private String reason;
    @JsonProperty("seat_type")
    private String seatType;
    @JsonProperty("user_dept")
    private String useDept;
    @JsonProperty("start_address_name")
    private String startAddressName;
    @JsonProperty("trip_number")
    private String tripNumber;
    @JsonProperty("order_create_time")
    private String orderCreateTime;
    @JsonProperty("fuel_fee")
    private String fuelFee;
    @JsonProperty("exceed_buy_info")
    private String exceedBuyInfo;
    @JsonProperty("bill_type")
    private String billType;
    @JsonProperty("trip_name")
    private String tripName;
    @JsonProperty("proof_name")
    private String proofName;
    @JsonProperty("cost_category")
    private String costCategory;
    @JsonProperty("payment_account")
    private String paymentAccount;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("taxi_price")
    private String taxiPrice;
    @JsonProperty("trip_apply_id")
    private String trip_apply_id;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("goods_price")
    private String goodsPrice;
    @JsonProperty("bus_refund_price")
    private String busRefundPrice;
    @JsonProperty("insurance_fee")
    private String insuranceFee;
    @JsonProperty("actual_price")
    private String actualPrice;
    @JsonProperty("cost_attribution_category2")
    private String costAttributionCategory2;
    @JsonProperty("cost_attribution_category1")
    private String costAttributionCategory1;
    @JsonProperty("cost_attribution_name2")
    private String costAttributionName2;
    @JsonProperty("mall_refund_price")
    private String mallRefundPrice;
    @JsonProperty("cost_attribution_name1")
    private String costAttributionName1;
    @JsonProperty("goods_number")
    private String goodsNumber;
    @JsonProperty("public_payment_reason")
    private String publicPaymentReason;
    @JsonProperty("start_city_name")
    private String startCityName;
    @JsonProperty("payer_dept_all_path")
    private String payerDeptAllPath;
    @JsonProperty("refund_diff_price")
    private String refundDiffPrice;
    @JsonProperty("re_apply_id")
    private String reApplyId;
    @JsonProperty("goods_name")
    private String goodsName;
    @JsonProperty("re_apply")
    private String reApply;
    @JsonProperty("payer_dept")
    private String payerDept;
    @JsonProperty("project_code")
    private String projectCode;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("airport_fee")
    private String airportFee;
    @JsonProperty("trip_info")
    private String tripInfo;
    @JsonProperty("contract_info")
    private String contractInfo;
    @JsonProperty("order_apply_id")
    private String orderApplyId;
    @JsonProperty("refund_price")
    private String refundPrice;
    @JsonProperty("end_city_name")
    private String endCityName;
    @JsonProperty("user_phone")
    private String userPhone;

    @JsonProperty("total_price")
    private String totalPrice;
    @JsonProperty("fbb_price")
    private String fbbPrice;
    @JsonProperty("end_address_name")
    private String endAddressName;
    @JsonProperty("air_discount")
    private String airDiscount;
    private String inmateName;

    @JsonGetter("roommate_name")
    public String getInmateName() {
        return inmateName;
    }

    @JsonSetter("inmate_name")
    public void setInmateName(String inmateName) {
        this.inmateName = inmateName;
    }

    private String inmatePhone;

    @JsonGetter("roommate_phone")
    public String getInmatePhone() {
        return inmatePhone;
    }

    @JsonSetter("inmate_phone")
    public void setInmatePhone(String inmatePhone) {
        this.inmatePhone = inmatePhone;
    }

    private String inmateDept;

    @JsonGetter("roommate_dept")
    public String getInmateDept() {
        return inmateDept;
    }

    @JsonSetter("inmate_dept")
    public void setInmateDept(String inmateDept) {
        this.inmateDept = inmateDept;
    }

    private String inmateDeptAllPath;

    @JsonGetter("roommate_dept_all_path")
    public String getInmateDeptAllPath() {
        return inmateDeptAllPath;
    }

    @JsonSetter("inmate_dept_all_path")
    public void setInmateDeptAllPath(String inmateDeptAllPath) {
        this.inmateDeptAllPath = inmateDeptAllPath;
    }

    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("order_category")
    private int orderCategory;
    @JsonProperty("upgrade_fee")
    private String upgradeFee;
    @JsonProperty("payer_custom_field1")
    private String payerCustomField1;
    @JsonProperty("payer_custom_field2")
    private String payerCustomField2;
    @JsonProperty("payer_custom_field3")
    private String payerCustomField3;
    @JsonProperty("payer_custom_field4")
    private String payerCustomField4;
    @JsonProperty("payer_custom_field5")
    private String payerCustomField5;
    @JsonProperty("payer_custom_field6")
    private String payerCustomField6;
    @JsonProperty("payer_custom_field7")
    private String payerCustomField7;
    @JsonProperty("payer_custom_field8")
    private String payerCustomField8;
    @JsonProperty("payer_custom_field9")
    private String payerCustomField9;
    @JsonProperty("payer_custom_field10")
    private String payerCustomField10;
    @JsonProperty("payer_custom_field11")
    private String payerCustomField11;
    @JsonProperty("payer_custom_field12")
    private String payerCustomField12;
    @JsonProperty("payer_custom_field13")
    private String payerCustomField13;
    @JsonProperty("payer_custom_field14")
    private String payerCustomField14;
    @JsonProperty("payer_custom_field15")
    private String payerCustomField15;
    @JsonProperty("payer_custom_field16")
    private String payerCustomField16;
    @JsonProperty("payer_custom_field17")
    private String payerCustomField17;
    @JsonProperty("payer_custom_field18")
    private String payerCustomField18;
    @JsonProperty("payer_custom_field19")
    private String payerCustomField19;
    @JsonProperty("payer_custom_field20")
    private String payerCustomField20;
    @JsonProperty("user_custom_field1")
    private String userCustomField1;
    @JsonProperty("user_custom_field2")
    private String userCustomField2;
    @JsonProperty("user_custom_field3")
    private String userCustomField3;
    @JsonProperty("user_custom_field4")
    private String userCustomField4;
    @JsonProperty("user_custom_field5")
    private String userCustomField5;
    @JsonProperty("user_custom_field6")
    private String userCustomField6;
    @JsonProperty("user_custom_field7")
    private String userCustomField7;
    @JsonProperty("user_custom_field8")
    private String userCustomField8;
    @JsonProperty("user_custom_field9")
    private String userCustomField9;
    @JsonProperty("user_custom_field10")
    private String userCustomField10;
    @JsonProperty("user_custom_field11")
    private String userCustomField11;
    @JsonProperty("user_custom_field12")
    private String userCustomField12;
    @JsonProperty("user_custom_field13")
    private String userCustomField13;
    @JsonProperty("user_custom_field14")
    private String userCustomField14;
    @JsonProperty("user_custom_field15")
    private String userCustomField15;
    @JsonProperty("user_custom_field16")
    private String userCustomField16;
    @JsonProperty("user_custom_field17")
    private String userCustomField17;
    @JsonProperty("user_custom_field18")
    private String userCustomField18;
    @JsonProperty("user_custom_field19")
    private String userCustomField19;
    @JsonProperty("user_custom_field20")
    private String userCustomField20;
    @JsonProperty("payer_phone")
    private String payerPhone;
    @JsonProperty("order_custom_field3")
    private String orderCustomField3;
    @JsonProperty("order_custom_field2")
    private String orderCustomField2;
    @JsonProperty("order_custom_field1")
    private String orderCustomField1;
    @JsonProperty("account_type")
    private String accountType;
    private String taxes;
    private String discount;
    @JsonProperty("cost_attribution_all_path")
    private String costAttributionAllPath;

    private String inmateCustomField1;
    //1:正向 2：逆向
    private Integer paymentDirect;

    @JsonGetter("roommate_custom_field1")
    public String getInmateCustomField1() {
        return inmateCustomField1;
    }

    @JsonSetter("inmate_custom_field1")
    public void setInmateCustomField1(String inmateCustomField1) {
        this.inmateCustomField1 = inmateCustomField1;
    }

    private String inmateCustomField2;

    @JsonGetter("roommate_custom_field2")
    public String getInmateCustomField2() {
        return inmateCustomField2;
    }

    @JsonSetter("inmate_custom_field2")
    public void setInmateCustomField2(String inmateCustomField2) {
        this.inmateCustomField2 = inmateCustomField2;
    }

    private String inmateCustomField3;

    @JsonGetter("roommate_custom_field3")
    public String getInmateCustomField3() {
        return inmateCustomField3;
    }

    @JsonSetter("inmate_custom_field3")
    public void setInmateCustomField3(String inmateCustomField3) {
        this.inmateCustomField3 = inmateCustomField3;
    }

    private String inmateCustomField4;

    @JsonGetter("roommate_custom_field4")
    public String getInmateCustomField4() {
        return inmateCustomField4;
    }

    @JsonSetter("inmate_custom_field4")
    public void setInmateCustomField4(String inmateCustomField4) {
        this.inmateCustomField4 = inmateCustomField4;
    }

    private String inmateCustomField5;

    @JsonGetter("roommate_custom_field5")
    public String getInmateCustomField5() {
        return inmateCustomField5;
    }

    @JsonSetter("inmate_custom_field5")
    public void setInmateCustomField5(String inmateCustomField5) {
        this.inmateCustomField5 = inmateCustomField5;
    }

    private String inmateCustomField6;

    @JsonGetter("roommate_custom_field6")
    public String getInmateCustomField6() {
        return inmateCustomField6;
    }

    @JsonSetter("inmate_custom_field6")
    public void setInmateCustomField6(String inmateCustomField6) {
        this.inmateCustomField6 = inmateCustomField6;
    }

    private String inmateCustomField7;

    @JsonGetter("roommate_custom_field7")
    public String getInmateCustomField7() {
        return inmateCustomField7;
    }

    @JsonSetter("inmate_custom_field7")
    public void setInmateCustomField7(String inmateCustomField7) {
        this.inmateCustomField7 = inmateCustomField7;
    }

    private String inmateCustomField8;

    @JsonGetter("roommate_custom_field8")
    public String getInmateCustomField8() {
        return inmateCustomField8;
    }

    @JsonSetter("inmate_custom_field8")
    public void setInmateCustomField8(String inmateCustomField8) {
        this.inmateCustomField8 = inmateCustomField8;
    }

    private String inmateCustomField9;

    @JsonGetter("roommate_custom_field9")
    public String getInmateCustomField9() {
        return inmateCustomField9;
    }

    @JsonSetter("inmate_custom_field9")
    public void setInmateCustomField9(String inmateCustomField9) {
        this.inmateCustomField9 = inmateCustomField9;
    }

    private String inmateCustomField10;

    @JsonGetter("roommate_custom_field10")
    public String getInmateCustomField10() {
        return inmateCustomField10;
    }

    @JsonSetter("inmate_custom_field10")
    public void setInmateCustomField10(String inmateCustomField10) {
        this.inmateCustomField10 = inmateCustomField10;
    }

    private String inmateCustomField11;

    @JsonGetter("roommate_custom_field11")
    public String getInmateCustomField11() {
        return inmateCustomField11;
    }

    @JsonSetter("inmate_custom_field11")
    public void setInmateCustomField11(String inmateCustomField11) {
        this.inmateCustomField1 = inmateCustomField1;
    }

    private String inmateCustomField12;

    @JsonGetter("roommate_custom_field12")
    public String getInmateCustomField12() {
        return inmateCustomField12;
    }

    @JsonSetter("inmate_custom_field12")
    public void setInmateCustomField12(String inmateCustomField12) {
        this.inmateCustomField12 = inmateCustomField12;
    }

    private String inmateCustomField13;

    @JsonGetter("roommate_custom_field13")
    public String getInmateCustomField13() {
        return inmateCustomField13;
    }

    @JsonSetter("inmate_custom_field13")
    public void setInmateCustomField13(String inmateCustomField13) {
        this.inmateCustomField13 = inmateCustomField13;
    }

    private String inmateCustomField14;

    @JsonGetter("roommate_custom_field14")
    public String getInmateCustomField14() {
        return inmateCustomField14;
    }

    @JsonSetter("inmate_custom_field14")
    public void setInmateCustomField14(String inmateCustomField14) {
        this.inmateCustomField14 = inmateCustomField14;
    }

    private String inmateCustomField15;

    @JsonGetter("roommate_custom_field15")
    public String getInmateCustomField15() {
        return inmateCustomField15;
    }

    @JsonSetter("inmate_custom_field15")
    public void setInmateCustomField15(String inmateCustomField15) {
        this.inmateCustomField15 = inmateCustomField15;
    }

    private String inmateCustomField16;

    @JsonGetter("roommate_custom_field16")
    public String getInmateCustomField16() {
        return inmateCustomField16;
    }

    @JsonSetter("inmate_custom_field16")
    public void setInmateCustomField16(String inmateCustomField16) {
        this.inmateCustomField16 = inmateCustomField16;
    }

    private String inmateCustomField17;

    @JsonGetter("roommate_custom_field17")
    public String getInmateCustomField17() {
        return inmateCustomField17;
    }

    @JsonSetter("inmate_custom_field17")
    public void setInmateCustomField17(String inmateCustomField17) {
        this.inmateCustomField17 = inmateCustomField17;
    }

    private String inmateCustomField18;

    @JsonGetter("roommate_custom_field18")
    public String getInmateCustomField18() {
        return inmateCustomField18;
    }

    @JsonSetter("inmate_custom_field18")
    public void setInmateCustomField18(String inmateCustomField18) {
        this.inmateCustomField18 = inmateCustomField18;
    }

    private String inmateCustomField19;

    @JsonGetter("roommate_custom_field19")
    public String getInmateCustomField19() {
        return inmateCustomField19;
    }

    @JsonSetter("inmate_custom_field19")
    public void setInmateCustomField19(String inmateCustomField19) {
        this.inmateCustomField19 = inmateCustomField19;
    }

    private String inmateCustomField20;

    @JsonGetter("roommate_custom_field20")
    public String getInmateCustomField20() {
        return inmateCustomField20;
    }

    @JsonSetter("inmate_custom_field20")
    public void setInmateCustomField20(String inmateCustomField20) {
        this.inmateCustomField20 = inmateCustomField20;
    }


    @JsonProperty("company_total_price")
    private String companyTotalPrice;
    @JsonProperty("order_category_type")
    private String orderCategoryType;
    @JsonProperty("personal_price")
    private String personalPrice;
    @JsonProperty("reduction_price")
    private String reductionPrice;
    @JsonProperty("rebook_fee")
    private String rebookFee;
    @JsonProperty("public_payment_use")
    private String publicPaymentUse;

    @JsonProperty("company_price")
    private String companyPrice;
    @JsonProperty("public_payment_project")
    private String publicPaymentProject;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("payer_name")
    private String payerName;
    @JsonProperty("re_apply_reason")
    private String reApplyReason;
    @JsonProperty("coupon_price")
    private String couponPrice;
    private String tag;
    @JsonProperty("order_apply")
    private String orderApply;
    @JsonProperty("trip_apply")
    private String tripApply;
    @JsonProperty("root_order_id")
    private String rootOrderId;
    @JsonProperty("old_cost_attribution_name1")
    private String oldCostAttributionName1;
    @JsonProperty("old_cost_attribution_name2")
    private String oldCostAttributionName2;
    @JsonProperty("exceed_buy_reason")
    private String exceedBuyReason;
    @JsonProperty("exceed_buy_apply")
    private String exceedBuyApply;
    @JsonProperty("repayment_price")
    private String repaymentPrice;
    @JsonProperty("rebook_diff_price")
    private String rebookDiffPrice;
    @JsonProperty("service_fee")
    private String serviceFee;
    @JsonProperty("goods_type")
    private String goodsType;
    @JsonProperty("order_state")
    private String orderState;
    @JsonProperty("user_dept_all_path")
    private String userDeptAllPath;
    @JsonProperty("company_custom_filed")
    private Map companyCustomFiled;
    @JsonProperty("third_fields_json")
    private Map thirdFieldsJson;
    @JsonProperty("subject_name")
    private String subjectName;
    @JsonProperty("subject_code")
    private String subjectCode;
    @JsonProperty("subject_bank")
    private String subjectBank;
    @JsonProperty("subject_account")
    private String subjectAccount;
    @JsonProperty("subject_address")
    private String subjectAddress;
    @JsonProperty("subject_phone")
    private String subjectPhone;

    @JsonProperty("payer_code")
    private String payerCode;

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("manual_price")
    private String manualPrice;

}
