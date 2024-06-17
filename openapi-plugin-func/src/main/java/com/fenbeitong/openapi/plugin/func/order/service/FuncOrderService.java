package com.fenbeitong.openapi.plugin.func.order.service;

import com.alibaba.fastjson.JSONArray;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.func.sign.service.FunctionAuthService;
import com.fenbeitong.openapi.plugin.support.order.dto.SupportOrderParamReqDTO;
import com.fenbeitong.openapi.plugin.support.order.dto.SupportOrderParamSaveReqDTO;
import com.fenbeitong.openapi.plugin.support.order.service.AbstractOrderService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.order.OrderParamSaveRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 订单访问功能集成实现
 * Created by log.chang on 2019/12/3.
 */
@ServiceAspect
@Service
public class FuncOrderService extends AbstractOrderService {

    @Autowired
    private FunctionAuthService signService;

    public FuncOrderService() {
        registerProcessor(new FuncOrderProcessor());
    }

    @Override
    protected String checkSign(Object... params) throws Exception {
        ApiRequestBase request = (ApiRequestBase) params[0];
        return signService.checkSign(request);
    }

    @Override
    protected void beforeOrderParam(Object... orderParams) throws Exception {

    }

    @Override
    protected SupportOrderParamReqDTO getOrderParamReq(Object... orderParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) orderParams[0];
        SupportOrderParamReqDTO orderParamReqDTO = JsonUtils.toObj(request.getData(), SupportOrderParamReqDTO.class);
        return orderParamReqDTO;
    }

    @Override
    protected Object rebuildOrderParam(JSONArray orderParamRes) {
        return orderParamRes;
    }

    @Override
    protected void beforeOrderParamSave(Object... orderParamSaveParams) throws Exception {

    }

    @Override
    protected SupportOrderParamSaveReqDTO getOrderParamSaveReq(Object... orderParamSaveParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) orderParamSaveParams[0];
        SupportOrderParamSaveReqDTO orderParamSaveReqDTO = JsonUtils.toObj(request.getData(), SupportOrderParamSaveReqDTO.class);
        return orderParamSaveReqDTO;
    }

    @Override
    protected Object rebuildOrderParamSave(OrderParamSaveRespDTO orderParamSaveRes) {
        return orderParamSaveRes;
    }
    public Object findAirPlaneOrder(ApiRequestNoEmployee apiRequest, String orderId) throws Exception {
        String companyId = signService.checkSign(apiRequest);
        return findAirPlaneOrder(companyId, orderId);
    }

    public Object findCarOrder(ApiRequestNoEmployee apiRequest, String orderId) throws Exception {
        String companyId = signService.checkSign(apiRequest);
        return findCarOrder(companyId, orderId);
    }

    public Object findTrainOrder(ApiRequestNoEmployee apiRequest, String orderId) throws Exception {
        String companyId = signService.checkSign(apiRequest);
        return findTrainOrder(companyId, orderId);
    }

    public Object findHotelOrder(ApiRequestNoEmployee apiRequest, String orderId) throws Exception {
        String companyId = signService.checkSign(apiRequest);
        return findHotelOrder(companyId, orderId);
    }

}
