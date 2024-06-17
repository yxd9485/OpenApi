package com.fenbeitong.openapi.plugin.func.payment.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.payment.service.FuncPaymentService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 对公付款业务
 *
 * @author ctl
 * @date 2022/3/8
 */
@RestController
@RequestMapping("/func/payment")
public class FuncPaymentController {

    @Autowired
    private FuncPaymentService paymentService;

    /**
     * 对公付款申请单创建
     *
     * @param apiRequest
     * @param request
     * @return
     */
    @PostMapping("/apply/create")
    @FuncAuthAnnotation
    public FuncResultEntity<?> createPaymentApply(@Valid ApiRequestBase apiRequest, HttpServletRequest request) {
        return FuncResponseUtils.success(paymentService.createPaymentApply(StringUtils.obj2str(request.getAttribute("companyId")), apiRequest.getData()));
    }

    /**
     * 供应商列表查询
     *
     * @param apiRequest
     * @param request
     * @return
     */
    @PostMapping("/supplier/list")
    @FuncAuthAnnotation
    public FuncResultEntity<?> listSuppliersByPage(@Valid ApiRequestBase apiRequest, HttpServletRequest request) {
        return FuncResponseUtils.success(paymentService.listSuppliersByPage(StringUtils.obj2str(request.getAttribute("companyId")), apiRequest.getData()));
    }

    /**
     * 根据付款单id查询付款结果
     *
     * @param apiRequest
     * @param request
     * @return
     */
    @FuncAuthAnnotation
    @PostMapping("/getResultByPaymentId")
    public FuncResultEntity<?> getPaymentResultByPaymentId(@Validated ApiRequestBase apiRequest, HttpServletRequest request) {
        String data = apiRequest.getData();
        Map<String, String> param = JsonUtils.toObj(data, new TypeReference<Map<String, String>>() {
        });
        if (ObjectUtils.isEmpty(param)) {
            throw new FinhubException(9999, "参数格式异常");
        }
        return FuncResponseUtils.success(paymentService.getPaymentResultByPaymentId(param.get("payment_id"), StringUtils.obj2str(request.getAttribute("companyId"))));
    }

    /**
     * 根据付款单id查询电子回单列表
     *
     * @param apiRequest
     * @return
     */
    @FuncAuthAnnotation
    @PostMapping("/getElectronicListByPaymentId")
    public FuncResultEntity<?> getElectronicListByPaymentId(@Validated ApiRequestBase apiRequest) {
        String data = apiRequest.getData();
        Map<String, String> param = JsonUtils.toObj(data, new TypeReference<Map<String, String>>() {
        });
        if (ObjectUtils.isEmpty(param)) {
            throw new FinhubException(9999, "参数格式异常");
        }
        return FuncResponseUtils.success(paymentService.getElectronicListByPaymentId(param.get("payment_id")));
    }
}
