package com.fenbeitong.openapi.plugin.func.order.service;


import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.noc.api.service.bank.model.dto.req.BankPublicSearchQueryReqDTO;
import com.fenbeitong.noc.api.service.bank.model.vo.BankOrderDetaiVO;
import com.fenbeitong.noc.api.service.bank.model.vo.BankOrderPublicStereoVO;
import com.fenbeitong.noc.api.service.bank.service.IBankOrderSearchService;
import com.fenbeitong.noc.api.service.base.BasePageResDTO;
import com.fenbeitong.noc.api.service.base.BasePageVO;
import com.fenbeitong.openapi.plugin.etl.service.IEtlStrategyService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoReqDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoResDTO;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 对公付款的订单和付款单的姐
 */
@Slf4j
@ServiceAspect
@Service
public class FuncBankOrderServiceImpl extends AbstractOrderService {

    private static final Long PUBLIC_PAY_LIST_CONFIGID = 2450L;

    private static final Long BANK_CHECK_LIST_CONFIGID = 2460L;

    @Value("${host.saas}")
    private String saasUrl;

    @DubboReference(check = false)
    private IBankOrderSearchService iBankOrderSearchService;

    @Autowired
    private RestHttpUtils restHttpUtils;

    @Autowired
    private IEtlStrategyService voucherStrategyService;

    @Autowired
    private UserCenterService userCenterService;

    @DubboReference(check = false)
    private ICommonService commonService;

    /**
     * 对公付款列表查询
     *
     * @param req
     * @return
     */
    public Object publicPayListOrder(PublicPayQueryReqDTO req) {
        Map<String, Object> results = new HashMap<>();
        try {
            BankPublicSearchQueryReqDTO queryReq = new BankPublicSearchQueryReqDTO();
            BeanUtils.copyProperties(req, queryReq);
            BasePageVO basePageVO = new BasePageVO();
            basePageVO.setPage(ObjUtils.toInteger(req.getPageIndex(), 1));
            basePageVO.setPageSize(ObjUtils.toInteger(req.getPageSize(), 10));
            queryReq.setPageInfo(basePageVO);
            BasePageResDTO<BankOrderPublicStereoVO> resultMainData = iBankOrderSearchService.stereoPubliyPayPageSearch(queryReq);
            if (resultMainData != null && resultMainData.getList() != null && resultMainData.getList().size() > 0) {
                List<Map<String, Object>> transform = voucherStrategyService.transfer(PUBLIC_PAY_LIST_CONFIGID, JsonUtils.toObj(JsonUtils.toJson(resultMainData.getList()), new TypeReference<List<Map<String, Object>>>() {
                }));
                results.put("results", transform);
                results.put("page_index", resultMainData.getPageInfo().getCurrentPage());
                results.put("page_size", resultMainData.getPageInfo().getPageSize());
                results.put("total_pages", resultMainData.getPageInfo().getTotalPages());
                results.put("total_count", resultMainData.getPageInfo().getTotalSize());
            }
        } catch (Exception e) {
            log.warn(">>>对公付款列表查询接口>>>{}调用时异常", e);
        }
        return results;
    }


    /**
     * 对公付款详情查询
     *
     * @param orderIds
     * @return
     */
    public List<BankOrderDetaiDTO> publicPayDetail(List<String> orderIds) {
        try {
            List<BankOrderDetaiVO> mainOrderDetailList = iBankOrderSearchService.detailSearch(orderIds);
            log.info("对公付款详情返回:{}", JsonUtils.toJson(mainOrderDetailList));
            if (CollectionUtil.isNotEmpty(mainOrderDetailList)) {
                List<BankOrderDetaiDTO> list = new ArrayList<>();
                mainOrderDetailList.forEach(bankOrderDetaiVO -> {
                    BankOrderDetaiDTO bankOrderDetaiDTO = new BankOrderDetaiDTO();
                    List<OrderSaasInfoDTO.CostAttribution> costInfoList = new ArrayList<>();
                    BeanUtils.copyProperties(bankOrderDetaiVO.getOrderDetaiVO(), bankOrderDetaiDTO);
                    bankOrderDetaiDTO.setUnitId(bankOrderDetaiVO.getUserDetaiVO().getUserUnitId());
                    bankOrderDetaiVO.getCostBaseVoList().stream().forEach(e -> {
                        OrderSaasInfoDTO.CostAttribution costAttrInfo = new OrderSaasInfoDTO.CostAttribution();
                        costAttrInfo.setCostAttributionCategory(e.getCostAttributionType());
                        costAttrInfo.setCostAttributionCustomExt(e.getCustomExt());
                        costAttrInfo.setCostAttributionId(e.getCostAttributionId());
                        costAttrInfo.setCostAttributionName(e.getCostAttributionName());
                        costInfoList.add(costAttrInfo);
                    });
                    bankOrderDetaiDTO.setCostInfo(costInfoList);
                    //设置三方信息
                    setThirdInfo(bankOrderDetaiDTO);
                    list.add(bankOrderDetaiDTO);
                });
                return list;
            }
        } catch (Exception e) {
            log.warn(">>>对公付款详情查询接口>>>{}调用时异常", e);
        }
        return null;

    }


    /**
     * 付款单列表查询
     *
     * @param req
     * @return
     */
    public Object bankCheckListOrder(BankCheckQueryReqDTO req) {
        Map<String, Object> carData = new HashMap<>();
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Integer count = 0;
        try {
            carData = getBankCheckListData(req);
            if (carData != null && !ObjectUtils.isEmpty(carData.get("data"))) {
                data = (Map<String, Object>) carData.get("data");
                count = NumericUtils.obj2int(data.get("totalCount"), 0);
            }
            if (count > 0) {
                dataList = (List<Map<String, Object>>) data.get("results");
                resp.put("results", voucherStrategyService.transfer(BANK_CHECK_LIST_CONFIGID, dataList));
                Integer pageIndex = req.getPageIndex();
                Integer pageSize = req.getPageSize();
                resp.put("total_count", count);
                resp.put("total_pages", (count + pageSize - 1) / pageSize);
                resp.put("page_index", pageIndex);
                resp.put("page_size", pageSize);
            }
        } catch (Exception e) {
            log.warn(">>>付款单列表查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return resp;
    }


    /**
     * 付款单详情查询
     *
     * @param req
     * @return
     */
    public Object bankCheckDetail(BankCheckQueryReqDTO req) {
        Map<String, Object> carData = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            carData = getBankCheckDetailData(req);
            if (carData != null && !ObjectUtils.isEmpty(carData.get("data"))) {
                data = (Map<String, Object>) carData.get("data");
                return data;
            }
        } catch (Exception e) {
            log.warn(">>>付款单详情查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return result;
    }

    /**
     * 付款单列表查询组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getBankCheckListData(BankCheckQueryReqDTO request) throws Exception {
        Integer pageIndex = NumericUtils.obj2int(request.getPageIndex(), 1);
        Integer pageSize = NumericUtils.obj2int(request.getPageSize(), 10);
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageIndex", pageIndex);
        paramMap.put("pageSize", pageSize);
        paramMap.put("proposer", request.getProposer());
        paramMap.put("apply_order_id", request.getApply_order_id());
        paramMap.put("start_time", request.getStart_time());
        paramMap.put("end_time", request.getEnd_time());
        paramMap.put("state", request.getState());
        paramMap.put("department", request.getDepartment());
        paramMap.put("voucherStatus", request.getVoucherStatus());
        paramMap.put("returnTicket", request.getReturnTicket());
        StringBuilder url = new StringBuilder(saasUrl);
        url.append("/apply_trip/writeoff/query_list");
        log.info("付款单列表查询开始，{}", url);
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = userCenterService.getUcSuperAdminToken(request.getCompanyId());
        httpHeaders.add("X-Auth-Token", token);
        String responseBody = restHttpUtils.get(url.toString(), httpHeaders, paramMap);
        log.info("付款单列表查询结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
    }


    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getBankCheckDetailData(BankCheckQueryReqDTO request) throws Exception {
        if (request != null && !StringUtils.isBlank(request.getApply_order_id())) {
            StringBuilder url = new StringBuilder(saasUrl);
            url.append("/apply_trip/writeoff/detail?applyId=").append(request.getApply_order_id());
            log.info("付款单订单详情查询开始，{}", url);
            String token = userCenterService.getUcSuperAdminToken(request.getCompanyId());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-Auth-Token", token);
            String responseBody = restHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
            log.info("付款单订单详情查询结束，返回数据为{}", responseBody);
            return analyzeResponse(responseBody);
        }
        return null;
    }


    /**
     * 校验接口返回数据
     *
     * @param result
     * @return
     */
    public Map<String, Object> analyzeResponse(String result) {
        if (ObjectUtils.isEmpty(result)) {
            log.info("[调用外部接口返回值为NULL]");
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_ERROR));
        }
        Map<String, Object> obj = JsonUtils.toObj(result, HashMap.class);
        if (ObjectUtils.isEmpty(obj)) {
            log.info("[调用外部接口返回值的结果集为]{}", result);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_DATA_ERROR));
        }
        Integer code = NumericUtils.obj2int(obj.get("code"));
        if (code != 0) {
            throw new FinhubException(code, StringUtils.obj2str(obj.get("msg")));
        }
        return obj;
    }

    private void setThirdInfo(BankOrderDetaiDTO bankOrderDetaiDTO) {
        try {
            OrderThirdInfoDTO orderThirdInfoDTO = new OrderThirdInfoDTO();
            CommonInfoReqDTO req = new CommonInfoReqDTO();
            req.setCompanyId(bankOrderDetaiDTO.getCompanyId());
            req.setType(IdTypeEnums.FB_ID.getKey());
            req.setBusinessType(IdBusinessTypeEnums.EMPLOYEE.getKey());
            req.setIdList(Lists.newArrayList(bankOrderDetaiDTO.getUserId()));
            List<CommonInfoResDTO> employeeList = commonService.queryCommonInfoByType(req);
            if (!ObjectUtils.isEmpty(employeeList)) {
                orderThirdInfoDTO.setUserId(employeeList.get(0).getThirdId());
            }

            String deptId = bankOrderDetaiDTO.getUnitId();
            req.setType(IdTypeEnums.FB_ID.getKey());
            req.setBusinessType(IdBusinessTypeEnums.ORG.getKey());
            req.setIdList(Lists.newArrayList(deptId));
            List<CommonInfoResDTO> orgUnitList = commonService.queryCommonInfoByType(req);
            if (!ObjectUtils.isEmpty(orgUnitList)) {
                orderThirdInfoDTO.setUnitId(orgUnitList.get(0).getThirdId());
            }

            //设置三方费用归属信息
            if (!ObjectUtils.isEmpty(bankOrderDetaiDTO.getCostInfo())) {
                Map<Integer, List<OrderSaasInfoDTO.CostAttribution>> categoryMap = bankOrderDetaiDTO.getCostInfo().stream().collect(Collectors.groupingBy(c -> c.getCostAttributionCategory()));
                for (Map.Entry<Integer, List<OrderSaasInfoDTO.CostAttribution>> entry : categoryMap.entrySet()) {
                    Integer category = entry.getKey();
                    List<OrderSaasInfoDTO.CostAttribution> costListByGroup = entry.getValue();
                    List<String> idList = costListByGroup.stream().map(c -> StringUtils.obj2str(c.getCostAttributionId())).collect(Collectors.toList());
                    List<CommonIdDTO> idDtoList = ObjectUtils.isEmpty(idList) ? null : commonService.queryIdDTO(bankOrderDetaiDTO.getCompanyId(), idList, 1, category);
                    Map<String, CommonIdDTO> commonIdDtoMap = ObjectUtils.isEmpty(idDtoList) ? null : idDtoList.stream().collect(Collectors.toMap(CommonIdDTO::getId, Function.identity()));
                    if (!ObjectUtils.isEmpty(commonIdDtoMap)) {
                        idList.forEach(id -> {
                            CommonIdDTO commonIdDto = commonIdDtoMap.get(id);
                            String thirdId = commonIdDto == null ? null : commonIdDto.getThirdId();
                            if (!StringUtils.isBlank(thirdId)) {
                                if (category == 1) {
                                    orderThirdInfoDTO.setCostDeptId(thirdId);
                                } else {
                                    orderThirdInfoDTO.setCostProjectId(thirdId);
                                }
                            }
                        });
                    }
                }
            }
            bankOrderDetaiDTO.setThirdInfo(orderThirdInfoDTO);
        } catch (Exception e) {
            log.info(">>>对公付款订单获取第三方信息接口调用时异常:{}>>>", e);
            bankOrderDetaiDTO.setThirdInfo(null);
        }
    }
}
