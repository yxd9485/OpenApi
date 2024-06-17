package com.fenbeitong.openapi.plugin.func.bill.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.bill.service.FuncBillServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.order.dto.PersonalConsumeFlowQuery;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.bill.dto.QueryOrderDetailReqV2DTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * 账单开放
 * Created by lizhen  on 2020/2/6.
 */
@RestController
@RequestMapping("/func/bill")
@Api(value = "账单", tags = "账单", description = "账单")
public class FuncBillController {

    @Autowired
    private FuncBillServiceImpl funcBillService;

    @RequestMapping("/queryBillNo")
    @ApiOperation(value = "查询账单单号", notes = "查询账单单号", httpMethod = "POST", response = FuncResultEntity.class)
    public Object queryBillNo(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcBillService.queryBillNoList(apiRequest);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/queryOrderDetail")
    @ApiOperation(value = "查询订单明细", notes = "查询订单明细", httpMethod = "POST", response = FuncResultEntity.class)
    @ResponseBody
    public Object queryOrderDetail(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        Object result = funcBillService.queryOrderDetail(apiRequest);
        return FuncResponseUtils.success(result);
    }

    /**
     * 个人消费流水查询
     *
     * @param request
     * @return
     */
    @FuncAuthAnnotation
    @RequestMapping("/person/consume/flow")
    public Object publicPayListOrder(HttpServletRequest httpRequest, @Valid ApiRequest request) throws BindException {
        PersonalConsumeFlowQuery req = JsonUtils.toObj(request.getData(), PersonalConsumeFlowQuery.class);
        if (req != null) {
            ValidatorUtils.validateBySpring(req);
        } else {
            req = new PersonalConsumeFlowQuery();
        }
        req.setCompanyId((String) httpRequest.getAttribute("companyId"));
        return FuncResponseUtils.success(funcBillService.queryPersonalConsumeFlow(req));
    }

    @RequestMapping("/refreshBillThirdInfo")
    public Object refreshBillThirdInfo(@RequestParam("bill_no") String billNo) {
        funcBillService.refreshThirdInfo(billNo);
        return FuncResponseUtils.success(new HashMap<>());
    }

    @RequestMapping("/refreshBillThirdInfoByOrderIds/{orderType}")
    public Object refreshBillThirdInfoByOrderIds(@PathVariable("orderType") int orderType, @RequestBody List<String> orderIdList) {
        funcBillService.refreshBillThirdInfoByOrderIds(orderIdList, orderType == 1);
        return FuncResponseUtils.success(new HashMap<>());
    }

    @FuncAuthAnnotation
    @RequestMapping("/queryOrderDetail/v2")
    @ApiOperation(value = "查询账单明细", notes = "查询账单明细", httpMethod = "POST", response = FuncResultEntity.class)
    @ResponseBody
    public Object queryOrderDetail_v2(HttpServletRequest httpRequest, @Valid ApiRequestNoEmployee apiRequest) throws Exception {
        String companyId = StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        QueryOrderDetailReqV2DTO queryOrderDetailReqV2DTO = JsonUtils.toObj(apiRequest.getData(), QueryOrderDetailReqV2DTO.class);
        queryOrderDetailReqV2DTO.setCompanyId(companyId);
        Object result = funcBillService.queryOrderDetail_v2(queryOrderDetailReqV2DTO);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/queryOrderDetailMappingConfig")
    @ApiOperation(value = "查询账单明细映射配置", notes = "查询账单明细映射配置", httpMethod = "POST", response = FuncResultEntity.class)
    @ResponseBody
    public Object queryOrderDetailMappingConfig(String companyId, String category) {
        return FuncResponseUtils.success(funcBillService.queryOrderDetailMappingConfig(companyId, category));
    }

    @RequestMapping("/getExportColumnFieldMapping")
    @ApiOperation(value = "查询账单明细配置", notes = "查询账单明细配置", httpMethod = "POST", response = FuncResultEntity.class)
    @ResponseBody
    public Object getExportColumnFieldMapping(String companyId) {
        Object result = funcBillService.getExportColumnFieldMapping(companyId);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/queryExportConsumeDetailDataList")
    @ApiOperation(value = "查询原始账单明细", notes = "查询原始账单明细", httpMethod = "POST", response = FuncResultEntity.class)
    @ResponseBody
    public Object queryExportConsumeDetailDataList(@Valid ApiRequestNoEmployee apiRequest) throws Exception {
        QueryOrderDetailReqV2DTO queryOrderDetailReqV2DTO = JsonUtils.toObj(apiRequest.getData(), QueryOrderDetailReqV2DTO.class);
        Object result = funcBillService.queryExportConsumeDetailDataList(queryOrderDetailReqV2DTO);
        return FuncResponseUtils.success(result);
    }

    @RequestMapping("/deleteDetailMapping")
    @ResponseBody
    public Object deleteDetailMapping() {
        funcBillService.deleteRedis();
        return FuncResponseUtils.success(new HashMap<>());
    }
}


