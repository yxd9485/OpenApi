package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncMallApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncOrderApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.ExpressDeliveryOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
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
 * <p>Title: FuncMallApplyController</p>
 * <p>Description: 采购审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/7 4:08 PM
 */
@RestController
@RequestMapping("/func/apply/mall")
public class FuncMallApplyController {

    @Autowired
    private FuncMallApplyServiceImpl mallApplyService;

    /**
     * saas通知 采购订单审批已创建
     *
     * @param data 数据
     * @return
     */
    @PostMapping("/notifyApplyCreated")
    public Object notifyApplyCreated(@RequestBody String data) {
        mallApplyService.notifyApplyCreated(data);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/notifyApplyAgree")
    @FuncAuthAnnotation
    public Object notifyApplyAgree(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws Exception {
        MallApplyApproveReqDTO req = JsonUtils.toObj(apiRequest.getData(), MallApplyApproveReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        req.setEmployeeId(httpRequest.getParameter("employee_id"));
        req.setEmployeeType(httpRequest.getParameter("employee_type"));
        mallApplyService.notifyApplyAgree(req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
