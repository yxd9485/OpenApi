package com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service;

/**
 * <p>Title: LYBudgetService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/27 1:55 下午
 */
public interface LYBudgetService {

    /**
     * 定时扣减预算
     */
    boolean subtractBudget(String companyId,String object);

    /**
     * 季度查询预售扣减
     */
    boolean quarterSubtractBudget(String companyId, String object);

    /**
     * 拆单
     */
    void separateOrder(String data,String callbackType,String companyId);

}
