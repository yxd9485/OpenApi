package com.fenbeitong.openapi.plugin.wechat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Data
public class ApprovalInfo {
    protected ApplyBean apply;
    protected List<ApprovalInfo.TripListBean> tripList;
    protected List<GustUser>  guestList;
    // 审批单自定义字段
    private List<CustomField> customFields;

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
        private String applyReasonDesc;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
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
        private String startTime;
        private String arrivalCityId;
        private String endTime;
        private int estimatedAmount;
        private String backStartTime;
        private String backEndTime;
        private int tripType;
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



}
