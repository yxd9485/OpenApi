package com.fenbeitong.openapi.plugin.func.altman.controller;

import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.altman.service.IFuncAltmanOrderService;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
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
 * <p>Title: FuncAltmanOrderController</p>
 * <p>Description: 万能订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author wei.xiao
 * @date 2020/5/25 18:08 PM
 */
@RestController
@RequestMapping("/func/altman/order")
public class FuncAltmanOrderController {

    @Autowired
    private IFuncAltmanOrderService altmanOrderService;

    /**
     * 第三方推送万能订单进行保存
     *
     * @param
     * @return
     */
    @PostMapping("/create")
    @FuncAuthAnnotation
    public Object createOrder(@Valid ApiRequest apiRequest) throws BindException {
        return FuncResponseUtils.success(altmanOrderService.saveAltmanOrder(apiRequest));
    }

    @PostMapping("/list")
    @FuncAuthAnnotation
    public Object listAltmanOrder(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws BindException {
        OpenAltmanOrderListReqDTO req = JsonUtils.toObj(apiRequest.getData(), OpenAltmanOrderListReqDTO.class);
        req.setCompany_id((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(altmanOrderService.listAltmanOrder(req, null));
    }

    @PostMapping("/detail")
    @FuncAuthAnnotation
    public Object getAltmanOrder(HttpServletRequest httpRequest, @Valid ApiRequest apiRequest) throws BindException {
        OpenAltmanOrderDetailDTO req = JsonUtils.toObj(apiRequest.getData(), OpenAltmanOrderDetailDTO.class);
        req.setCompany_id((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(altmanOrderService.getAltmanOrder(req,null));
    }
}
