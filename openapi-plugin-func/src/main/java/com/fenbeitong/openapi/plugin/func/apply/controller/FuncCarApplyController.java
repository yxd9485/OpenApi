package com.fenbeitong.openapi.plugin.func.apply.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncCarApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TaxiApplyAgreeReqDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncTripApplyController</p>
 * <p>Description: 用车审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/7 10:29 AM
 */
@RestController
@RequestMapping("/func/apply/car")
public class FuncCarApplyController {

    @Autowired
    private FuncCarApplyServiceImpl carApplyService;

    @RequestMapping("/create")
    public Object createCarApply(@Valid ApiRequest apiRequest) throws Exception {
        Object result = carApplyService.createCarApply(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/notifyApplyAgree")
    @FuncAuthAnnotation
    public Object notifyApplyAgree(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws Exception {
        TaxiApplyAgreeReqDTO req = JsonUtils.toObj(apiRequest.getData(), TaxiApplyAgreeReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        carApplyService.notifyCarApplyAgree(apiRequest);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

}
