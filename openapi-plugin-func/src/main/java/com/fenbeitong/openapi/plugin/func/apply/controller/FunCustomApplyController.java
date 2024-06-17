package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.service.FunCustomServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 自定义报销审批
 * @Auther zhang.peng
 * @Date 2022/1/31
 */
@RestController
@RequestMapping("/func/apply/custom")
public class FunCustomApplyController {

    @Autowired
    private FunCustomServiceImpl customService;

    @RequestMapping("/notifyApplyAgree")
    @FuncAuthAnnotation
    public Object notifyApplyAgree(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws Exception {
        MallApplyApproveReqDTO req = JsonUtils.toObj(apiRequest.getData(), MallApplyApproveReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        req.setEmployeeId(httpRequest.getParameter("employee_id"));
        req.setEmployeeType(httpRequest.getParameter("employee_type"));
        customService.notifyApplyAgree(req);
        return FuncResponseUtils.success(Maps.newHashMap());
    }
}
