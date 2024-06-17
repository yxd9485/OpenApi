package com.fenbeitong.openapi.plugin.func.altman.service;

import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.altman.dto.OpenAltmanOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.AltmanOrderRefundDetailDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.AltmanOrderRefundListReqDTO;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import org.springframework.validation.BindException;

import java.util.List;

/**
 * openapi 万能订单服务接口
 * Created by wei.xiao on 2020/05/26.
 */
public interface IFuncAltmanOrderService {

    /**
     * 新增altmanOrder
     */
    String saveAltmanOrder(ApiRequest apiRequest) throws BindException;

    /**
     * altmanOrder列表数据
     */
    Object listAltmanOrder(OpenAltmanOrderListReqDTO req, String apiVersion) throws BindException;

    /**
     * altmanOrder详情数据
     */
    Object getAltmanOrder(OpenAltmanOrderDetailDTO req, String apiVersion) throws BindException;

    /**
     * altmanOrder退款列表数据
     */
    Object refundList(AltmanOrderRefundListReqDTO req, String apiVersion) throws BindException;

    /**
     * 退款详情数据
     * @return
     */
    Object refundDetail(AltmanOrderRefundDetailDTO req, String apiVersion) throws BindException;
}
