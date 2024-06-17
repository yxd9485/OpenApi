package com.fenbeitong.openapi.plugin.func.order.controller;

import com.fenbeitong.fenbei.settlement.external.api.query.ReimburseSummaryDataQuery;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.EimburseReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncReimburseServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Description 报销汇总订单详情查询
 * @Author duhui
 * @Date 2021/9/6
 **/
@RestController
@RequestMapping("/func/orders/reimburse")
public class FuncReimburseOrderController {

    @Autowired
    FuncReimburseServiceImpl funcReimburseService;

    private void checkReq(Object req) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(req);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
    }

    /**
     * @Description 报销汇总订单详情
     * @Author duhui
     * @Date 2021/9/7
     **/
    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/infoByApplyCode")
    public Object infoByApplyCode(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        EimburseReqDTO eimburseReqDTO = JsonUtils.toObj(request.getData(), EimburseReqDTO.class);
        eimburseReqDTO.setCompanyId((String) httpRequest.getAttribute("companyId"));
        checkReq(eimburseReqDTO);
        return FuncResponseUtils.success(funcReimburseService.findReimburseByApplyCode(eimburseReqDTO));
    }
    /**
     * @Description 报销汇总订单列表
     * @Author duhui
     * @Date 2021/9/7
     **/
    @SuppressWarnings("all")
    @FuncAuthAnnotation
    @RequestMapping("/list")
    public Object list(HttpServletRequest httpRequest, @Valid ApiRequest request) {
        ReimburseSummaryDataQuery reimburseSummaryDataQuery = JsonUtils.toObj(request.getData(), ReimburseSummaryDataQuery.class);
        reimburseSummaryDataQuery.setCompanyId((String) httpRequest.getAttribute("companyId"));
        checkReq(reimburseSummaryDataQuery);
        return FuncResponseUtils.success(funcReimburseService.findReimburseSummaryData(reimburseSummaryDataQuery));
    }


}
