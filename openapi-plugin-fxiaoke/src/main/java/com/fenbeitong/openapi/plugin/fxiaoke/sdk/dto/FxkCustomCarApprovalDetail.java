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
public class FxkCustomCarApprovalDetail {
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
     * 用车城市
     */
    @JsonProperty("fbt_car_city__c")
    private String fbtCarCityC;
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
     * 用车次数
     */
    @JsonProperty("fbt_car_count__c")
    private String fbtCarCountC;
    /**
     * 用车费用
     */
    @JsonProperty("fbt_car_cost__c")
    private String fbtCarCostC;

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
