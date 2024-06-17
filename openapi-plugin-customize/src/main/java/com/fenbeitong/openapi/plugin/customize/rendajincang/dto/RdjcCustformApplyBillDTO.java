package com.fenbeitong.openapi.plugin.customize.rendajincang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName RdjcCustformApplyDetailDTO
 * @Description 人大金仓自定义申请单表单参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/10/12
 **/
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RdjcCustformApplyBillDTO {

    /**
     * 单据编号
     */
    @JsonProperty("apply_id")
    private String applyId;
    /**
     * 申请部门id
     */
    @JsonProperty("third_department_id")
    private String thirdDepartmentId;
    /**
     * 申请人id
     */
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;
    /**
     * 申请理由
     */
    @JsonProperty("app_reason")
    private String appReason;
    /**
     * 补充说明
     */
    @JsonProperty("app_reason_add_desc")
    private String appReasonAddDesc;
    /**
     * 交通方式
     */
    @JsonProperty("transportation")
    private String transportation;

    /**
     * 出差明细
     */
    @JsonProperty("applyDetailDTOList")
    private List<RdjcCustformApplyDetailDTO> applyDetailDTOList;

    @Data
    public static class RdjcCustformApplyDetailDTO {
        /**
         * 出差城市
         */
        @JsonProperty("business_city")
        private String businessCity;
        /**
         * 出差时间
         */
        @JsonProperty("business_time")
        private String businessTime;
    }
}
