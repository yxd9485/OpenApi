package com.fenbeitong.openapi.plugin.customize.yixin.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.customize.yixin.service.YiXinOrderService;
import com.fenbeitong.openapi.plugin.support.apply.dto.ChangeOrRefundApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.OrderApplyDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 宜信退改推送
 * @Auther zhang.peng
 * @Date 2022/1/15
 */
@RestController
@RequestMapping("/customize/yixin/changeOrRefund/order")
public class YiXinOrderChangeOrRefundPushController {

    @Autowired
    private YiXinOrderService yiXinOrderService;

    @RequestMapping("/push")
    @ResponseBody
    public Object pushOrderData(HttpServletRequest request){
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        Map orderApplyDTO = JsonUtils.toObj(requestBody, Map.class);
        Object result = yiXinOrderService.buildExceedApplyInfo(orderApplyDTO);
        if ( null != result ){
            return OpenapiResponseUtils.success(result);
        } else {
            return OpenapiResponseUtils.error(-1,"退改通知推送三方失败");
        }
    }

}
