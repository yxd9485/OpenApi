package com.fenbeitong.openapi.plugin.customize.yixin.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.customize.yixin.service.YiXinOrderService;
import com.fenbeitong.openapi.plugin.support.apply.dto.OrderApplyDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 宜信超规推送
 * @Auther zhang.peng
 * @Date 2021/12/29
 */
@RestController
@RequestMapping("/customize/yixin/order")
public class YiXinOrderPushController {

    @Autowired
    private YiXinOrderService yiXinOrderService;

    @RequestMapping("/push")
    @ResponseBody
    public Object pushOrderData(HttpServletRequest request){
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        OrderApplyDTO orderApplyDTO = JsonUtils.toObj(requestBody,OrderApplyDTO.class);
        Object result = yiXinOrderService.buildCostAndThirdApplyInfo(orderApplyDTO);
        if ( null != result ){
            return OpenapiResponseUtils.success(result);
        } else {
            return OpenapiResponseUtils.error(-1,"超归通知推送三方失败");
        }
    }

}
