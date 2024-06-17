package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillOpenApi;
import com.fenbeitong.fenbei.settlement.external.api.dto.MqBusinessDetailDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.ThirdExtFieldsUpdateDTO;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.settlement.dto.MqBusinessDetailEvent;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenBillExtInfoDao;
import com.fenbeitong.openapi.plugin.func.company.dto.CostAttributionDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenBillExtInfo;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Title: MqBusinessDetailEventHandler</p>
 * <p>Description: 清结算商务消费账单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/1 3:44 PM
 */
@Component
@Slf4j
public class MqBusinessDetailEventHandler extends EventHandler<MqBusinessDetailEvent> {

    @Autowired
    private IFuncCompanyBillExtService funcCompanyBillExtService;

    @Autowired
    private OpenBillExtInfoStatusServiceImpl openBillExtInfoStatusService;

    @Autowired
    private OpenBillExtInfoDao openBillExtInfoDao;

    @DubboReference(check = false)
    private IBillOpenApi billOpenApi;

    @Override
    public boolean process(MqBusinessDetailEvent businessDetailEvent, Object... args) {
        Boolean reduceOrder = businessDetailEvent.getReducedOrder().getKey();
        if (reduceOrder != null && !reduceOrder) {
            log.info("清结算获取商务消费扩展字段,settleOrderId={}", businessDetailEvent.getSettleOrderId());
            Map<String, Object> extInfoMap = getExtInfoMap(businessDetailEvent);
            Map<String, Object> resultMap = getMergeResult(businessDetailEvent.getSettleOrderId(), extInfoMap);
            saveOrUpdateExtInfo(businessDetailEvent, resultMap);
            log.info("清结算获取商务消费扩展字段,settleOrderId={},result={}", businessDetailEvent.getSettleOrderId(), JsonUtils.toJson(resultMap));
        }
        return true;
    }

    private void saveOrUpdateExtInfo(MqBusinessDetailEvent businessDetailEvent, Map<String, Object> resultMap) {
        boolean checkFields = openBillExtInfoStatusService.isCheckFields(businessDetailEvent.getCompanyId(), businessDetailEvent.getAccountType());
        boolean emptyValue = ObjectUtils.isEmpty(resultMap) || "{\"extMap\":{}}".equals(JsonUtils.toJson(resultMap)) || "{}".equals(JsonUtils.toJson(resultMap));
        if (!checkFields && emptyValue) {
            return;
        }
        OpenBillExtInfo extInfo = openBillExtInfoDao.getById(businessDetailEvent.getSettleOrderId());
        if (extInfo == null) {
            extInfo = new OpenBillExtInfo();
            extInfo.setId(businessDetailEvent.getSettleOrderId());
            extInfo.setCompanyId(businessDetailEvent.getCompanyId());
            extInfo.setAccountType(businessDetailEvent.getAccountType());
            extInfo.setOrderId(businessDetailEvent.getOrderId());
            extInfo.setProductId(businessDetailEvent.getProductId());
            extInfo.setCategory(businessDetailEvent.getCategory().getKey());
            extInfo.setCategoryName(businessDetailEvent.getCategory().getValue());
            extInfo.setSrcData(JsonUtils.toJson(businessDetailEvent));
            extInfo.setExtData(JsonUtils.toJson(resultMap));
            openBillExtInfoStatusService.setStatus(extInfo);
            extInfo.setCreateTime(new Date());
            openBillExtInfoDao.saveSelective(extInfo);
        } else {
            extInfo.setExtData(JsonUtils.toJson(resultMap));
            openBillExtInfoStatusService.setStatus(extInfo);
            extInfo.setUpdateTime(new Date());
            openBillExtInfoDao.updateById(extInfo);
        }
        ThirdExtFieldsUpdateDTO updateDto = new ThirdExtFieldsUpdateDTO();
        updateDto.setSettleId(businessDetailEvent.getSettleOrderId());
        updateDto.setAccountType(businessDetailEvent.getAccountType());
        updateDto.setThirdExtFieldsJson(extInfo.getExtData());
        List<ThirdExtFieldsUpdateDTO> updateList = Lists.newArrayList();
        updateList.add(updateDto);
        billOpenApi.refreshSettleThirdExtFieldsByIds(updateList);
    }

    private Map<String, Object> getMergeResult(String settleOrderId, Map<String, Object> extInfoMap) {
        if (ObjectUtils.isEmpty(extInfoMap)) {
            return null;
        }
        OpenBillExtInfo extInfo = openBillExtInfoDao.getById(settleOrderId);
        String srcExt = extInfo == null || ObjectUtils.isEmpty(extInfo.getExtData()) ? null : extInfo.getExtData();
        Map<String, Object> srcExtMap = ObjectUtils.isEmpty(srcExt) ? Maps.newHashMap() : JsonUtils.toObj(srcExt, Map.class);
        return merge(srcExtMap, extInfoMap);
    }

    private Map<String, Object> merge(Map<String, Object> m1, Map<String, Object> m2) {
        if (ObjectUtils.isEmpty(m1)) {
            return m2;
        }
        if (ObjectUtils.isEmpty(m2)) {
            return m1;
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        Set<String> keySet = new HashSet<>();
        keySet.addAll(m1.keySet());
        keySet.addAll(m2.keySet());
        for (String key : keySet) {
            Object v1 = m1.get(key);
            Object v2 = m2.get(key);
            Object v;
            if (v1 instanceof Map || v2 instanceof Map) {
                v = merge((Map) v1, (Map) v2);
            } else {
                v = ObjectUtils.isEmpty(v1) ? v2 : v1;
            }
            resultMap.put(key, v);
        }
        return resultMap;
    }

    private Map<String, Object> getExtInfoMap(MqBusinessDetailEvent businessDetailEvent) {
        FuncBillExtInfoTransformDTO funcBillExtInfoTransformDto = new FuncBillExtInfoTransformDTO();
        funcBillExtInfoTransformDto.setType(StringUtils.obj2str(businessDetailEvent.getCategory().getKey()));
        //预订人信息
        MqBusinessDetailDTO.OrganizationBean personBean = businessDetailEvent.getPersonBean();
        if (personBean != null) {
            funcBillExtInfoTransformDto.setEmployeeId(personBean.getPersonId());
            funcBillExtInfoTransformDto.setDeptId(personBean.getDepartmentId());
        }
        //使用人信息
        List<MqBusinessDetailDTO.OrganizationBean> consumerBeanList = businessDetailEvent.getConsumerBeanList();
        if (!ObjectUtils.isEmpty(consumerBeanList)) {
            String userIds = consumerBeanList.stream().filter(c -> !ObjectUtils.isEmpty(c.getPersonId())).map(MqBusinessDetailDTO.OrganizationBean::getPersonId).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setUserId(userIds);
            String userDeptIds = consumerBeanList.stream().filter(c -> !ObjectUtils.isEmpty(c.getDepartmentId())).map(MqBusinessDetailDTO.OrganizationBean::getDepartmentId).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setUserDeptId(userDeptIds);
            String userPhones = consumerBeanList.stream().filter(c -> !ObjectUtils.isEmpty(c.getPhone())).map(MqBusinessDetailDTO.OrganizationBean::getPhone).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setUserPhone(userPhones);
        }
        //同住人信息
        List<MqBusinessDetailDTO.OrganizationBean> cohabitantList = businessDetailEvent.getCohabitantList();
        if (!ObjectUtils.isEmpty(cohabitantList)) {
            String cohabitantUserIds = cohabitantList.stream().filter(c -> !ObjectUtils.isEmpty(c.getPersonId())).map(MqBusinessDetailDTO.OrganizationBean::getPersonId).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setLiveWithUserId(cohabitantUserIds);
            String cohabitantUserDeptIds = cohabitantList.stream().filter(c -> !ObjectUtils.isEmpty(c.getDepartmentId())).map(MqBusinessDetailDTO.OrganizationBean::getDepartmentId).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setLiveWithDeptId(cohabitantUserDeptIds);
            String cohabitantUserPhones = cohabitantList.stream().filter(c -> !ObjectUtils.isEmpty(c.getPhone())).map(MqBusinessDetailDTO.OrganizationBean::getPhone).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setLiveWithUserPhone(cohabitantUserPhones);
        }
        //费用归属信息
        List<MqBusinessDetailDTO.CostAttributionBean> costAttributionBeanList = businessDetailEvent.getCostAttributionBeanList();
        if (!ObjectUtils.isEmpty(costAttributionBeanList)) {
            funcBillExtInfoTransformDto.setCostAttributionList(costAttributionBeanList.stream().map(c -> {
                CostAttributionDTO costAttributionDto = new CostAttributionDTO();
                costAttributionDto.setId(c.getCostAttributionId());
                costAttributionDto.setCategory(StringUtils.obj2str(c.getCostAttributionCategory()));
                costAttributionDto.setName(c.getCostAttributionName());
                return costAttributionDto;
            }).collect(Collectors.toList()));
        }
        funcBillExtInfoTransformDto.setApplyId(businessDetailEvent.getTripApplyId());
        funcBillExtInfoTransformDto.setOrderApplyId(businessDetailEvent.getDuringApplyId());
        Map<String, Object> srcDataMap = Maps.newHashMap();
        setSrcData(srcDataMap, businessDetailEvent);
        return funcCompanyBillExtService.getExtInfo(businessDetailEvent.getCompanyId(), srcDataMap, funcBillExtInfoTransformDto);
    }

    private void setSrcData(Map<String, Object> srcDataMap, MqBusinessDetailEvent businessDetailEvent) {
        Map<String, Object> dataMap = Maps.newHashMap();
        srcDataMap.put("data", dataMap);
        String customFieldsJson = businessDetailEvent.getCustomFieldsJson();
        if (!ObjectUtils.isEmpty(customFieldsJson)) {
            dataMap.put("customRemark", JsonUtils.toObj(customFieldsJson, List.class));
        }
    }
}
