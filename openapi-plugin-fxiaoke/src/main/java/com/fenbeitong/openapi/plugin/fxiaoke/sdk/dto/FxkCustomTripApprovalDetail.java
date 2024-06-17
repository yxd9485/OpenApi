package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxkCustomTripApprovalDetail {
    /**
     * 审批单名称
     */
    @JsonProperty("name")
    private String name;
    /**
     * 申请事由
     */

    @JsonProperty("fbt_apply_reason__c")
    private String fbtApplyReasonC;
    /**
     * 交通工具
     */
    @JsonProperty("fbt_trip_type__c")
    private List<String> fbtTripTypeC;

    /**
     * 出发城市
     */
    @JsonProperty("fbt_departure_city__c")
    private String fbtDepartureCityC;

    /**
     * 目的城市
     */
    @JsonProperty("fbt_destination_city__c")
    private String fbtDestinationCityC;
    /**
     * 开始日期
     */
    @JsonProperty("fbt_begin_date__c")
    private long fbtBeginDateC;
    /**
     * 结束日期
     */
    @JsonProperty("fbt_end_date__c")
    private long fbtEndDateC;
    /**
     * 单程往返标识
     */
    @JsonProperty("fbt_is_single__c")
    private String fbtIsSingleC;
    /**
     * 出行人ID
     */
    @JsonProperty("fbt_companion__c")
    private List<String> fbtCompanionC;

    /**
     * 审批单ID
     */
    @JsonProperty("_id")
    private  String id;
    /**
     * 是否删除
     */
    @JsonProperty("is_deleted")
    private boolean isDeleted;
    /**
     * 部门名称
     */
    @JsonProperty("owner_department")
    private String ownerDepartment;

    /**
     * 部门ID
     */
    @JsonProperty("owner_department_id")
    private String ownerDepartmentId;

    /**
     * 创建人ID集合
     */
    @JsonProperty("owner")
    private List<String> owner;
    @JsonProperty("create_time")
    private long createTime;

}
