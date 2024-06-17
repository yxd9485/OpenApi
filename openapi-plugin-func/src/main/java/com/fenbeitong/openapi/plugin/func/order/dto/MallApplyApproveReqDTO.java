package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>Title: ExpressDeliveryOrderListQueryReq</p>
 * <p>Description: 快递订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/3 7:59 PM
 */
@Data
public class MallApplyApproveReqDTO {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;


    /**
     * 员式Id
     */
    @JsonProperty("employee_id")
    private String employeeId;


    /**
     * 员工类型
     */
    @JsonProperty("employee_type")
    private String employeeType;


    /**
     * 申请单Id
     */
    @JsonProperty("apply_id")
    @NotEmpty(message = "分贝通申请单ID不能为空")
    private String applyId;


    /**
     * 三方的Id
     */
    @JsonProperty("third_id")
    @NotEmpty(message = "third_id不能为空")
    private String thirdApplyId;



}
