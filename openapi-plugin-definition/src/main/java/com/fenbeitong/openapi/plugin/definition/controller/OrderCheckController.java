package com.fenbeitong.openapi.plugin.definition.controller;/**
 * <p>Title: OrderCheckController</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/9/9 11:20 上午
 */

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.service.OrderCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by lizhen on 2021/9/9.
 */
@RestController
@RequestMapping("/definitions/function/orderCheck")
public class OrderCheckController {

    @Autowired
    private OrderCheckService orderCheckService;

    @RequestMapping("/checkOrder")
    public Object checkOrder(@RequestParam("bill_no") String billNo, @RequestParam("company_id") String companyId) {
        Map<String, Object> stringObjectMap = orderCheckService.checkOrder(companyId, billNo);
        return DefinitionResultDTO.success(stringObjectMap);

    }
}
