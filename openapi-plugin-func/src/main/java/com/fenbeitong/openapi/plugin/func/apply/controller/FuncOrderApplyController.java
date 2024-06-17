package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncOrderApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderApplyApproveChangeAndRefundReqDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncOrderApplyController</p>
 * <p>Description: 订单审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/24 2:08 PM
 */
@RestController
@RequestMapping("/func/apply/order")
public class FuncOrderApplyController {

    @Autowired
    private FuncOrderApplyServiceImpl orderApplyService;

    /**
     * saas通知 订单审批已创建
     *
     * @param data 数据
     * @return
     */
    @PostMapping("/notifyApplyCreated")
    public Object notifyApplyCreated(@RequestBody String data) {
        orderApplyService.notifyApplyCreated(data);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/notifyApplyAgree")
    @FuncAuthAnnotation
    public Object notifyApplyAgree(@Valid ApiRequest apiRequest) throws Exception {
        orderApplyService.notifyApplyAgree(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/changeAndRefund/notifyApplyAgree")
    @FuncAuthAnnotation
    public Object changeAndRefundNotifyApplyAgree(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws Exception {
        OrderApplyApproveChangeAndRefundReqDTO req = JsonUtils.toObj(apiRequest.getData(), OrderApplyApproveChangeAndRefundReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        req.setEmployeeId(httpRequest.getParameter("employee_id"));
        req.setEmployeeType(httpRequest.getParameter("employee_type"));
        orderApplyService.changeAndRefundNotifyApplyAgree(req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
