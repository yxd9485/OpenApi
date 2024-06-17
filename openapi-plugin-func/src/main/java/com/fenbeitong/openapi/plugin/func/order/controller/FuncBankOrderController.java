package com.fenbeitong.openapi.plugin.func.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.BankCheckQueryReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.BankOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.PublicPayQueryReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncBankOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title: FuncBankOrderController</p>
 * <p>Description: 虚拟卡交易订单信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/8/29 2:37 PM
 */
@RestController
@RequestMapping("/func/orders")
public class FuncBankOrderController {

    @Autowired
    private FuncBankOrderServiceImpl funcBankOrderServiceImpl;


    /**
     * 查询对公付款的列表
     * @param httpRequest
     * @param request
     * @return
     * @throws BindException
     */
    @FuncAuthAnnotation
    @RequestMapping("/public/pay/list")
    public Object publicPayListOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        PublicPayQueryReqDTO req = JsonUtils.toObj(request.getData(), PublicPayQueryReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
            req.setCompanyId((String) httpRequest.getAttribute("companyId"));
            return FuncResponseUtils.success(funcBankOrderServiceImpl.publicPayListOrder(req));
        }
        return FuncResponseUtils.error(1,"请求参数格式不正确");
    }

    /**
     * 查询对公付款详情
     * @param httpRequest
     * @param request
     * @return
     * @throws BindException
     */
    @FuncAuthAnnotation
    @RequestMapping("/public/pay/detail")
    public Object publicPayDetail(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        if(StringUtil.isNotEmpty(request.getData())){
            List<String> orderIds=null;
            try {
                orderIds = JSONArray.parseArray(request.getData()).toJavaList(String.class);
            }catch (Exception e){
                return FuncResponseUtils.error(1,"订单号传参格式不正确");
            }
            if(CollectionUtil.isNotEmpty(orderIds)) {
                return FuncResponseUtils.success(funcBankOrderServiceImpl.publicPayDetail(orderIds));
            }
        }
        return FuncResponseUtils.success(null);
    }

    /**
     * 付款单的列表
     * @param httpRequest
     * @param request
     * @return
     * @throws BindException
     */
    @FuncAuthAnnotation
    @RequestMapping("/bank/check/list")
    public Object bankCheckListOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        BankCheckQueryReqDTO req = JsonUtils.toObj(request.getData(), BankCheckQueryReqDTO.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new BankCheckQueryReqDTO();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(funcBankOrderServiceImpl.bankCheckListOrder(req));
    }

    /**
     * 付款单详情
     * @param httpRequest
     * @param request
     * @return
     * @throws BindException
     */
    @FuncAuthAnnotation
    @RequestMapping("/bank/check/detail")
    public Object detail(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        BankCheckQueryReqDTO req = JsonUtils.toObj(request.getData(), BankCheckQueryReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(funcBankOrderServiceImpl.bankCheckDetail(req));
    }

}
