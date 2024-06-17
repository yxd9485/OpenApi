package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhaokechun
 * @date 2018/11/27 14:47
 */
@Data
public class DingtalkTripApplyProcessInfo {


    /**
     * apply : {"type":1,"flow_type":4,"budget":1,"third_id":"59dc7fe62798635263b8414a","third_remark":"approve-openapi-test2"}
     * trip_list : [{"type":7,"start_city_id":"2000002","start_time":"2017-12-13","arrival_city_id":"1000001","end_time":"2017-12-13","estimated_amount":1}]
     */

    // 审批单基础信息
    private ApplyBean apply;
    // 审批单行程信息
    private List<TripListBean> tripList;
    // 审批单自定义字段
    private List<CustomField> customFields;
    // 同行人
    private List<Guest> guestList;

    /**
     * 审批单基础信息
     */
    @Data
    public static class ApplyBean {
        /**
         * type : 1
         * flow_type : 4
         * budget : 1
         * third_id : 59dc7fe62798635263b8414a
         * third_remark : approve-openapi-test2
         */

        private int type;
        private int flowType;
        private int budget;
        private String thirdId;
        private String thirdRemark;
        private String applyReason;


    }

    /**
     * 审批单行程信息
     */
    @Data
    public static class TripListBean {
        /**
         * type : 7
         * start_city_id : 2000002
         * start_time : 2017-12-13
         * arrival_city_id : 1000001
         * end_time : 2017-12-13
         * estimated_amount : 1
         */

        private int type;
        private String startCityId;
        private String startCityName;
        private String startTime;
        private String arrivalCityId;
        private String endTime;
        private int estimatedAmount;
        private String backStartTime;
        private String backEndTime;
        private int tripType;
        private int personCount;

    }

    /**
     * 审批单自定义字段
     */
    @Data
    public static class CustomField {
        // 与配置表字段key匹配
        private String type;
        private String value;
    }

    @Data
    public static class Guest {

        private String id;

        private String name;

        private String phoneNum;

        private Boolean whetherEmployee;

        private Integer employeeType;
    }
}
