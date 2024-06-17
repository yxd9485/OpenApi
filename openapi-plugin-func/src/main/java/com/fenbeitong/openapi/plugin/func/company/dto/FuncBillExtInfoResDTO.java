package com.fenbeitong.openapi.plugin.func.company.dto;

import lombok.Data;

import java.util.Map;

/**
 * <p>Title: FuncBillExtInfoResDTO</p>
 * <p>Description: 账单扩展字段结果</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/8 2:35 PM
 */
@Data
public class FuncBillExtInfoResDTO {

    /**
     * 预订人第三方人员ID
     */
    private String bookerUserId;

    /**
     * 预订人第三方部门ID
     */
    private String bookerDeptId;

    /**
     * 预订人工号
     */
    private String bookerEmployeeNumber;

    /**
     * 旅客第三方人员ID
     */
    private String passengerUserId;

    /**
     * 旅客第三方部门ID
     */
    private String passengerDeptId;

    /**
     * 旅客工号
     */
    private String passengerEmployeeNumber;

    /**
     * 同住人第三方人员ID
     */
    private String cohabitantUserId;

    /**
     * 同住人第三方部门ID
     */
    private String cohabitantDeptId;

    /**
     * 同住人工号
     */
    private String cohabitantEmployeeNumber;

    /**
     * 费用归属(第三方项目ID)
     */
    private String costAttributioncostId;

    /**
     * 费用归属(第三方项目code)
     */
    private String costAttributioncostCode;

    /**
     * 费用归属(第三方项目名称)
     */
    private String costAttributioncostName;

    /**
     * 费用归属(第三方项目描述)
     */
    private String costAttributioncostDesp;


    /**
     * 费用归属(第三方项目code)
     */
    private String costAttributioncost;

    /**
     * 费用归属(第三方部门ID)
     */
    private String costAttributionDeptId;

    /**
     * 费用归属(第三方部门名称)
     */
    private String costAttributionDeptName;

    /**
     * 第三方行程审批单ID
     */
    private String travelApprovalID;

    /**
     * 第三方行程审批单自定义字段
     */
    private String travelApprovalFields;

    /**
     * 第三方用车审批单单ID
     */
    private String taxiApprovalID;

    /**
     * 第三方订单审批单ID
     */
    private String orderApprovalID;

    /**
     * 第三方外卖审批单ID
     */
    private String takeawayApprovalID;

    /**
     * 第三方用餐审批单ID
     */
    private String dinnerApprovalID;

    /**
     * 扩展属性
     */
    private Map extMap;
}
