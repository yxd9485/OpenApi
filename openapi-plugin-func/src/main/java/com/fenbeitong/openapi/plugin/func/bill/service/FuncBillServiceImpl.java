package com.fenbeitong.openapi.plugin.func.bill.service;

import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillQueryApi;
import com.fenbeitong.fenbei.settlement.external.api.query.BillDataQuery;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.bill.dto.*;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.fenbei.settlement.base.dto.BasePageDTO;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillOpenApi;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillPersonalApi;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillNoDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.BillPersonalConsumeDTO;
import com.fenbeitong.fenbei.settlement.external.api.query.BillPersonalConsumeQuery;
import com.fenbeitong.openapi.plugin.func.order.dto.PersonalConsumeFlowDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.PersonalConsumeFlowDTO.PersonalConsumeFlowOrderInfoBean;
import com.fenbeitong.openapi.plugin.func.order.dto.PersonalConsumeFlowDTO.PersonalConsumeFlowSummaryInfoBean;
import com.fenbeitong.openapi.plugin.func.order.dto.PersonalConsumeFlowDTO.PersonalConsumeFlowUserInfoBean;
import com.fenbeitong.openapi.plugin.func.order.dto.PersonalConsumeFlowDTO.PersonalConsumeFlowVoucherInfoBean;
import com.fenbeitong.openapi.plugin.func.order.dto.PersonalConsumeFlowQuery;
import com.fenbeitong.openapi.plugin.func.sign.dto.ApiRequestNoEmployee;
import com.fenbeitong.openapi.plugin.support.bill.service.AbstractBillService;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by lizhen on 2020/2/7.
 */
@ServiceAspect
@Service
@Slf4j
public class FuncBillServiceImpl extends AbstractBillService {


    @Autowired
    private CommonAuthService signService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService baseEmployeeExtService;

    @DubboReference(check = false)
    private IBillPersonalApi billPersonalService;

    @DubboReference(check = false)
    private IBillOpenApi billOpenApi;

    @DubboReference(check = false)
    IBillQueryApi iBillQueryApi;

    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    private final String EXPORT_COLUMN_FIELD_MAPPING = "OPEN_PLUGIN_EXPORT_COLUMN_FIELD_MAPPING";
    private final String EXPORT_COLUMN_FIELD_OPENAPI_MAPPING = "OPEN_PLUGIN_EXPORT_COLUMN_FIELD_OPENAPI_MAPPING";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    OpenSysConfigDao openSysConfigDao;


    @Override
    protected void beforeQueryBillNoList(Object... queryBillNoListParams) throws Exception {

    }

    @Override
    protected String checkSign(Object... queryBillNoListParams) throws Exception {
        ApiRequestBase request = (ApiRequestBase) queryBillNoListParams[0];
        return signService.checkSign(request);
    }

    @Override
    protected QueryBillNoListReqDTO getQueryBillNoListReq(Object... queryBillNoListParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) queryBillNoListParams[0];
        QueryBillNoListReqDTO queryBillNoListReqDTO = JsonUtils.toObj(request.getData(), QueryBillNoListReqDTO.class);
        return queryBillNoListReqDTO;
    }

    @Override
    protected Object rebuildQueryBillNoList(BasePageDTO<BillNoDTO> queryBillNoListRes) {
        SupportBasePageResDTO supportBasePageRes = new SupportBasePageResDTO();
        supportBasePageRes.setPageInfo(new BasePageVO(queryBillNoListRes.getPageIndex(), queryBillNoListRes.getPageSize(), queryBillNoListRes.getCount()));
        supportBasePageRes.setList(queryBillNoListRes.getDtoList());
        return supportBasePageRes;
    }

    @Override
    protected void beforeQueryOrderDetailList(Object... queryOrderDetailParams) throws Exception {

    }

    @Override
    protected QueryOrderDetailReqDTO getQueryOrderDetailReq(Object... queryOrderDetailParams) throws Exception {
        ApiRequestNoEmployee request = (ApiRequestNoEmployee) queryOrderDetailParams[0];
        QueryOrderDetailReqDTO queryOrderDetailReqDTO = JsonUtils.toObj(request.getData(), QueryOrderDetailReqDTO.class);
        return queryOrderDetailReqDTO;
    }

    @Override
    protected IBaseEmployeeExtService getBaseEmployeeExtService() {
        return baseEmployeeExtService;
    }

    /**
     * 个人消费流水查询
     *
     * @param req
     * @return
     */
    public Object queryPersonalConsumeFlow(PersonalConsumeFlowQuery req) {
        SupportBasePageResDTO pageResDTO = new SupportBasePageResDTO();
        try {
            BillPersonalConsumeQuery queryReq = new BillPersonalConsumeQuery();
            BeanUtils.copyProperties(req, queryReq);
            queryReq.setPageIndex(ObjUtils.toInteger(req.getPageIndex(), 1));
            queryReq.setPageSize(ObjUtils.toInteger(req.getPageSize(), 20));
            BasePageDTO<BillPersonalConsumeDTO> resultMainData = billPersonalService.queryPersonalConsumeFlowDetail(queryReq);
            if (resultMainData != null && resultMainData.getDtoList() != null && resultMainData.getDtoList().size() > 0) {
                List<PersonalConsumeFlowDTO> personalConsumeFlowDTOList = new ArrayList<>();
                for (BillPersonalConsumeDTO billPersonalConsumeDTO : resultMainData.getDtoList()) {
                    PersonalConsumeFlowDTO personalConsumeFlowDTO = new PersonalConsumeFlowDTO();
                    PersonalConsumeFlowSummaryInfoBean summaryInfoBean = new PersonalConsumeFlowSummaryInfoBean();
                    BeanUtils.copyProperties(billPersonalConsumeDTO.getSummaryInfoBean(), summaryInfoBean);
                    personalConsumeFlowDTO.setSummaryInfoBean(summaryInfoBean);
                    PersonalConsumeFlowOrderInfoBean flowOrderInfoBean = new PersonalConsumeFlowOrderInfoBean();
                    BeanUtils.copyProperties(billPersonalConsumeDTO.getOrderInfoBean(), flowOrderInfoBean);
                    personalConsumeFlowDTO.setOrderInfoBean(flowOrderInfoBean);
                    PersonalConsumeFlowUserInfoBean flowUserInfoBean = new PersonalConsumeFlowUserInfoBean();
                    BeanUtils.copyProperties(billPersonalConsumeDTO.getUserInfoBean(), flowUserInfoBean);
                    personalConsumeFlowDTO.setUserInfoBean(flowUserInfoBean);
                    PersonalConsumeFlowVoucherInfoBean flowVoucherInfoBean = new PersonalConsumeFlowVoucherInfoBean();
                    List<BillPersonalConsumeDTO.CostAttributionBean> costAttributionBeanList = billPersonalConsumeDTO.getVoucherInfoBean().getCostAttributionBeanList();
                    List<PersonalConsumeFlowDTO.PersonalConsumeFlowCostAttributionBean> costAttributionBeans = costAttributionBeanList == null ? Lists.newArrayList() : costAttributionBeanList.stream().map(c -> {
                        PersonalConsumeFlowDTO.PersonalConsumeFlowCostAttributionBean costAttributionBean = new PersonalConsumeFlowDTO.PersonalConsumeFlowCostAttributionBean();
                        BeanUtils.copyProperties(c, costAttributionBean);
                        return costAttributionBean;
                    }).collect(Collectors.toList());
                    flowVoucherInfoBean.setCostAttributionBeanList(costAttributionBeans);
                    BeanUtils.copyProperties(billPersonalConsumeDTO.getVoucherInfoBean(), flowVoucherInfoBean);
                    personalConsumeFlowDTO.setVoucherInfoBean(flowVoucherInfoBean);
                    personalConsumeFlowDTOList.add(personalConsumeFlowDTO);
                }
                BasePageVO pageVO = new BasePageVO(resultMainData.getPageIndex(), resultMainData.getPageSize(), resultMainData.getCount());
                pageResDTO.setPageInfo(pageVO);
                pageResDTO.setList(personalConsumeFlowDTOList);
            }
        } catch (Exception e) {
            log.warn(">>>个人消费流水查询>>>{}调用时异常", e);
        }
        return pageResDTO;
    }

    public void refreshThirdInfo(String billNo) {
        billOpenApi.refreshSettleThirdExtFieldsByBillNo(billNo);
    }

    public void refreshBillThirdInfoByOrderIds(List<String> orderIdList, boolean isPublic) {
        billOpenApi.refreshSettleThirdExtFieldsByOrderIds(orderIdList, isPublic);
    }

    /**
     * 获取订单明细v2 版本
     *
     * @param queryOrderDetailReqV2DTO
     * @return
     * @throws Exception
     */
    public Object queryOrderDetail_v2(QueryOrderDetailReqV2DTO queryOrderDetailReqV2DTO) throws Exception {
        String companyId = queryOrderDetailReqV2DTO.getCompanyId();
        ValidatorUtils.validateBySpring(queryOrderDetailReqV2DTO);
        BillDataQuery billDataQuery = new BillDataQuery();
        BeanUtils.copyProperties(queryOrderDetailReqV2DTO, billDataQuery);
        BasePageDTO<Map<String, Object>> queryOrderDetailRes = iBillQueryApi.queryExportConsumeDetailDataList(billDataQuery);
        List<Map<String, Object>> endList = new ArrayList<>();
        OpenThirdScriptConfig billConfig = openThirdScriptConfigDao.getCommonScriptConfig("bill_all", EtlScriptType.BILL_INFO_CHANGE);
        if (!ObjectUtils.isEmpty(queryOrderDetailRes) && !ObjectUtils.isEmpty(queryOrderDetailRes.getDtoList())) {
            Map<String, String> openApiOrderDeetailMapping = queryOrderDetailOpenApiMappingConfig();
            for (Map<String, Object> t : queryOrderDetailRes.getDtoList()) {
                String type = StringUtils.obj2str(t.get("settleOrder_baseBean_category_key"));
                Map<String, String> map = queryOrderDetailMappingConfig(companyId, type);
                Map<String, Object> commonData = new HashMap<>(512);
                Map<String, Object> thirdData = new HashMap<>(256);
                Map<String, Object> companyData = new HashMap<>(512);
                Map<String, Object> fileData_0 = new LinkedHashMap<>(512);
                Map<String, Object> fileDataType = new LinkedHashMap<>(512);
                Map<String, Object> fileData = new LinkedHashMap<>(512);
                Map<String, Object> scrMaping = new LinkedHashMap<>(512);
                if (!ObjectUtils.isEmpty(map)) {
                    map.forEach((k, v) -> {
                        if (type.equals(StringUtils.obj2str(v))) {
                            fileDataType.put(k, "");
                        } else if ("0".equals(StringUtils.obj2str(v))) {
                            fileData_0.put(k, "");
                        } else {
                            fileData.put(k, "");
                        }
                    });

                    t.forEach((k, v) -> {
                        if (map.containsKey(k)) {
                            if (type.equals(map.get(k))) {
                                fileDataType.put(k, v);
                            } else if ("0".equals(map.get(k))) {
                                fileData_0.put(k, v);
                            } else {
                                fileData.put(k, v);
                            }
                            scrMaping.put(k, v);
                        }
                        if (k.contains("settleOrder_thirdExtFieldsJson")) {
                            thirdData.put(k, v);
                        }

                    });
                    openApiOrderDeetailMapping.forEach((k1, v1) -> {
                        Arrays.asList(StringUtils.obj2str(v1).split(",")).forEach(key -> {
                            commonData.put(key, "");
                        });
                    });
                    packageMap(openApiOrderDeetailMapping, fileData, commonData);
                    packageMap(openApiOrderDeetailMapping, fileData_0, commonData);
                    packageMap(openApiOrderDeetailMapping, fileDataType, commonData);
                    MapDifference<String, Object> difference = Maps.difference(scrMaping, openApiOrderDeetailMapping);
                    difference.entriesOnlyOnLeft().forEach((k, v) -> {
                        companyData.put(k, v);
                    });
                    commonData.put("third_fields_json", thirdData);
                    commonData.put("company_custom_filed", companyData);
                    if (!ObjectUtils.isEmpty(billConfig)) {
                        Map<String, Object> finalT = t;
                        EtlUtils.etlFilter(billConfig, new HashMap<String, Object>(8) {
                            {
                                put("commonData", commonData);
                                put("type", type);
                                put("scrData", finalT);
                            }
                        });
                    }
                } else {
                    log.info("账单数据异常:{}", JsonUtils.toJson(t));
                }
                endList.add(commonData);
            }

        }
        queryOrderDetailRes.setDtoList(endList);
        return queryOrderDetailRes;
    }

    private void packageMap(Map<String, String> map1, Map<String, Object> map2, Map<String, Object> commonData) {
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        set2.retainAll(set1);
        set2.forEach(s -> {
            String value = StringUtils.obj2str(map2.get(s));
            if (value != null) {
                Arrays.asList(map1.get(s).split(",")).forEach(key -> {
                    commonData.put(key, value);
                });
            }
        });
    }

    public Map<String, String> queryOrderDetailMappingConfig(String companyId, String category) {
        if (ObjectUtils.isEmpty(companyId) || ObjectUtils.isEmpty(category)) {
            return null;
        }
        String redisKey = EXPORT_COLUMN_FIELD_MAPPING + "_" + companyId + "_" + category;
        String redisFieldMapping = (String) redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isBlank(redisFieldMapping)) {
            return JsonUtils.toObj(redisFieldMapping, Map.class);
        }
        Map<String, List<Map<String, Object>>> fieldMapping = iBillQueryApi.getExportColumnFieldMapping(1, companyId);
        if (ObjectUtils.isEmpty(fieldMapping)) {
            throw new FinhubException(500, "获取账单映射配置失败");
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, String> resMap = new HashMap<>(512);
        fieldMapping.forEach((k, v) -> {
            mapList.addAll(v.stream().filter(t -> "0".equals(StringUtils.obj2str(t.get("category"))) || "1".equals(StringUtils.obj2str(t.get("category"))) || StringUtils.obj2str(category).equals(StringUtils.obj2str(t.get("category"))))
                    .collect(Collectors.toList()));
        });
        resMap.putAll(mapList.stream().collect(Collectors.toMap(t -> StringUtils.obj2str(t.get("key")), t -> StringUtils.obj2str(t.get("category")), (o, n) -> n)));
        redisTemplate.opsForValue().set(redisKey, JsonUtils.toJson(resMap), 2, TimeUnit.HOURS);
        return resMap;
    }

    public Map<String, String> queryOrderDetailOpenApiMappingConfig() {
        String redisFieldMapping = (String) redisTemplate.opsForValue().get(EXPORT_COLUMN_FIELD_OPENAPI_MAPPING);
        if (!StringUtils.isBlank(redisFieldMapping)) {
            return JsonUtils.toObj(redisFieldMapping, Map.class);
        }
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(new HashMap<String, Object>(4) {{
            put("type", OpenSysConfigType.ORDER_DETAIL_MAPPING_CONFIG.getType());
        }});
        if (ObjectUtils.isEmpty(openSysConfig)) {
            throw new FinhubException(500, "获取账单openApi映射配置失败");
        }
        redisTemplate.opsForValue().set(EXPORT_COLUMN_FIELD_OPENAPI_MAPPING, openSysConfig.getValue(), 1, TimeUnit.DAYS);
        return JsonUtils.toObj(openSysConfig.getValue(), LinkedHashMap.class);

    }

    public Map<String, List<Map<String, Object>>> getExportColumnFieldMapping(String companyId) {
        return iBillQueryApi.getExportColumnFieldMapping(1, companyId);
    }

    public BasePageDTO<Map<String, Object>> queryExportConsumeDetailDataList(QueryOrderDetailReqV2DTO queryOrderDetailReqV2DTO) throws BindException {
        ValidatorUtils.validateBySpring(queryOrderDetailReqV2DTO);
        BillDataQuery billDataQuery = new BillDataQuery();
        BeanUtils.copyProperties(queryOrderDetailReqV2DTO, billDataQuery);
        return iBillQueryApi.queryExportConsumeDetailDataList(billDataQuery);
    }

    public void deleteRedis() {
        Set<String> keys = redisTemplate.keys(EXPORT_COLUMN_FIELD_MAPPING + "*");
        keys.add(EXPORT_COLUMN_FIELD_OPENAPI_MAPPING);
        redisTemplate.delete(keys);

    }
}
