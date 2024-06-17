package com.fenbeitong.openapi.plugin.kingdee.customize.laiye.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service.LYBudgetService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>Title: BudgetController</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/22 6:07 下午
 */
@Controller
@RequestMapping("/laiye/pushData")
public class BudgetController {

    @Autowired
    LYBudgetService LYBudgetService;


    /**
     * @Description 订单预算扣减
     * @Author duhui
     * @Date 2021/6/22
     **/
    @RequestMapping("/budget/reduce")
    @ResponseBody
    public Object subtractBudget(@RequestParam("jobConfig") String jobConfig) {

        Map map = JsonUtils.toObj(jobConfig, Map.class);
        String companyId = map.get("companyId").toString();
        String callbackType = map.get("callbackType").toString();
        boolean b = LYBudgetService.subtractBudget(companyId, callbackType);
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "来也订单扣减失败");
        }
    }

    /**
     * @Description 订单季度预算扣减
     * @Author duhui
     * @Date 2021/6/22
     **/
    @RequestMapping("/quarter/budget/reduce/{companyId}")
    @ResponseBody
    public Object quarterSubtractBudget(HttpServletRequest request, @PathVariable("companyId") String companyId) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        boolean b = LYBudgetService.quarterSubtractBudget(companyId, requestBody);
        if (b) {
            return OpenapiResponseUtils.success(b);
        } else {
            return OpenapiResponseUtils.error(-1, "来也订单扣减失败");
        }
    }


    /**
     * @Description 订单拆单
     * @Author duhui
     * @Date 2021/6/22
     **/
    @RequestMapping("/budget/separate")
    @ResponseBody
    public void separate(HttpServletRequest request, @RequestParam("callbackType") String callbackType, @RequestParam("companyId") String companyId) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        LYBudgetService.separateOrder(requestBody, callbackType, companyId);
    }


}
