package com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service;


import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeekBudgetDTO;

/**
 * <p>Title: BudgetService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/22 4:59 下午
 */
public interface LYKingDeeBudgetService {

    /**
     * 定时扣减预算
     */
     boolean subtractBudget(String companyId, KingDeekBudgetDTO kingDeekBudgetDTO);

    /**
     * 季度查询预售扣减
     */
     boolean quarterSubtractBudget(String companyId, KingDeekBudgetDTO kingDeekBudgetDTO);
}
