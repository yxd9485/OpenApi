package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.altman.service.IFuncAltmanOrderService;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.AltmanOrderRefundDetailDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.AltmanOrderRefundListReqDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>Title: FuncAtmanOrderV1Controller</p>
 * <p>Description: 万能订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author wei.xiao
 * @date 2020/5/25 18:08 PM
 */
@RestController
@RequestMapping("/func/orders/altman")
public class FuncAtmanOrderV1Controller {

    @Autowired
    private IFuncAltmanOrderService altmanOrderService;

    @PostMapping("/list")
    @FuncAuthAnnotation
    public Object listAltmanOrder(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws BindException {
        OpenAltmanOrderListReqDTO req = JsonUtils.toObj(apiRequest.getData(), OpenAltmanOrderListReqDTO.class);
        req.setCompany_id((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(altmanOrderService.listAltmanOrder(req, "v_1.0"));
    }

    @PostMapping("/detail")
    @FuncAuthAnnotation
    public Object getAltmanOrder(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws BindException {
        OpenAltmanOrderDetailDTO req = JsonUtils.toObj(apiRequest.getData(), OpenAltmanOrderDetailDTO.class);
        req.setCompany_id((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(altmanOrderService.getAltmanOrder(req, "v_1.0"));
    }

    @PostMapping("/refundList")
    @FuncAuthAnnotation
    public Object refundList(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws BindException {
        AltmanOrderRefundListReqDTO req = JsonUtils.toObj(apiRequest.getData(), AltmanOrderRefundListReqDTO.class);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(altmanOrderService.refundList(req, "v_1.0"));
    }

    @PostMapping("/refundDetail")
    @FuncAuthAnnotation
    public Object refundDetail(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws BindException {
        AltmanOrderRefundDetailDTO req = JsonUtils.toObj(apiRequest.getData(), AltmanOrderRefundDetailDTO.class);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(altmanOrderService.refundDetail(req, "v_1.0"));
    }
}
