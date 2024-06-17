package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * @Description 审批单详情信息
 * @Author xiaohai
 * @Date 2022-01-24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ApplyDetailInfoDTO {


    private Apply apply;
    @JsonProperty("guest_list")
    private List<GuestList> guestList;
    @JsonProperty("trip_list")
    private List<TripList> tripList;

    @Data
    public static class Apply{
        private String id;
        private int type;
        private int state;
        private int budget;
        @JsonProperty("apply_reason")
        private String applyReason;
        @JsonProperty("apply_reason_desc")
        private String applyReasonDesc;
        @JsonProperty("cost_attribution_id")
        private String costAttributionId;
        @JsonProperty("cost_attribution_name")
        private String costAttributionName;
        @JsonProperty("cost_attribution_category")
        private int costAttributionCategory;
        @JsonProperty("city_range")
        private String cityRange;
        @JsonProperty("is_change_apply")
        private boolean isChangeApply;
        @JsonProperty("root_apply_order_id")
        private String rootApplyOrderId;
        @JsonProperty("employee_id")
        private String employeeId;
        @JsonProperty("approver_id")
        private String approverId;
        @JsonProperty("company_id")
        private String companyId;
        @JsonProperty("travel_price_detail")
        private String travelPriceDetail;
        @JsonProperty("create_time")
        private String createTime;
        @JsonProperty("user_name")
        private String userName;
        @JsonProperty("user_dept")
        private String userDept;
        @JsonProperty("operate_auth")
        private int operateAuth;
        @JsonProperty("flow_type")
        private int flowType;
        @JsonProperty("third_remark")
        private String thirdRemark;
        @JsonProperty("apply_order_type")
        private int applyOrderType;
        @JsonProperty("repulse_desc")
        private String repulseDesc;
        @JsonProperty("current_time")
        private String currentTime;
        @JsonProperty("exceed_buy_type")
        private int exceedBuyType;
        private String overtime;
        @JsonProperty("real_price")
        private int realPrice;
        @JsonProperty("applicant_name")
        private String applicantName;
        @JsonProperty("travel_day")
        private int travelDay;
        @JsonProperty("force_sumbit")
        private boolean forceSumbit;
        @JsonProperty("org_unit_id")
        private String orgUnitId;
        @JsonProperty("returnTicket")
        private int returnticket;
        @JsonProperty("returnDownload")
        private int returndownload;
        @JsonProperty("snap_content")
        private String snapContent;
        @JsonProperty("processInstanceId")
        private String processinstanceid;
        @JsonProperty("flow_process_id")
        private String flowProcessId;
        @JsonProperty("flow_process_type")
        private int flowProcessType;
        @JsonProperty("is_custom_form")
        private boolean isCustomForm;
        @JsonProperty("apply_order_type_name")
        private String applyOrderTypeName;
        @JsonProperty("form_name")
        private String formName;
        @JsonProperty("allow_apply_download")
        private int allowApplyDownload;
        @JsonProperty("bill_no")
        private String billNo;
        @JsonProperty("whether_travel_time")
        private boolean whetherTravelTime;
        @JsonProperty("travelDetails")
        private List<String> traveldetails;
        @JsonProperty("supplyCanReminder")
        private boolean supplycanreminder;
        @JsonProperty("update_time")
        private String updateTime;
    }

    @Data
    public static class GuestList{

        private String id;
        private String name;
        @JsonProperty("id_number")
        private String idNumber;
        private boolean exist;
        @JsonProperty("is_employee")
        private boolean isEmployee;
        private String desc;
        @JsonProperty("phone_num")
        private String phoneNum;
    }

    @Data
    public static class TripList{
        private String id;
        private int type;
        @JsonProperty("start_time")
        private String startTime;
        @JsonProperty("end_time")
        private String endTime;
        @JsonProperty("start_city_id")
        private String startCityId;
        @JsonProperty("start_city_name")
        private String startCityName;
        @JsonProperty("estimated_amount")
        private int estimatedAmount;
        private boolean avaliable;
        private String content;
        @JsonProperty("start_city_name_list")
        private List<String> startCityNameList;
        @JsonProperty("multi_trip_scene")
        private  List<String> multiTripScene;
        @JsonProperty("multi_trip_city")
        private List<MultiTripCity> multiTripCity;
    }

    @Data
    public static class MultiTripCity{
        private String key;
        private String value;
    }


}
