package com.fenbeitong.openapi.plugin.kingdee.common.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: KingDeekBudgetDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/26 10:19 下午
 */

@Data
public class KingDeekBudgetDTO {

    /**
     * 预算周期 年、月、季
     */
    public String FCYCLEID;

    /**
     * 预算期间 当前、季的期间 例:当前季度是第一季 对应 1
     */
    public String FPeriod;

    /**
     * 调整日期
     */
    public String FAdjustDate;

    /**
     * 编号
     */
    public String number;

    /**
     * 预算调整额
     */
    public BigDecimal money;

    /**
     * 组织编码
     */
   public String orgCode;

    /**
     * 组织名称
     */
    public String orgName;


}
