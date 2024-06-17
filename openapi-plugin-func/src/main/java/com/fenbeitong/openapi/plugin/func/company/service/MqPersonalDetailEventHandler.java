package com.fenbeitong.openapi.plugin.func.company.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.fenbei.settlement.base.KeyValue;
import com.fenbeitong.fenbei.settlement.external.api.api.bill.IBillOpenApi;
import com.fenbeitong.fenbei.settlement.external.api.dto.MqPersonalDetailDTO;
import com.fenbeitong.fenbei.settlement.external.api.dto.ThirdExtFieldsUpdateDTO;
import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VoucherCostAttributionRPCDTO;
import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VouchersOperationFlowRespRPCDTO;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.settlement.dto.MqPersonalDetailEvent;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenBillExtInfoDao;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenBillExtInfo;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Title: MqPersonalDetailEventHandler</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/1 3:48 PM
 */
@Component
@Slf4j
public class MqPersonalDetailEventHandler extends EventHandler<MqPersonalDetailEvent> {

    @Autowired
    private FuncVoucherBillExtServiceImpl funcVoucherBillExtService;

    @Autowired
    private OpenBillExtInfoStatusServiceImpl openBillExtInfoStatusService;

    @Autowired
    private OpenBillExtInfoDao openBillExtInfoDao;

    @DubboReference(check = false)
    private IBillOpenApi billOpenApi;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Override
    public boolean process(MqPersonalDetailEvent personalDetailEvent, Object... args) {
        log.info("清结算获取个人消费流水扩展字段,settleFlowId={}", personalDetailEvent.getSettleFlowId());
        Map<String, Object> extInfoMap = getExtInfoMap(personalDetailEvent);
        Map<String, Object> resultMap = getMergeResult(personalDetailEvent.getSettleFlowId(), extInfoMap);
        saveOrUpdateExtInfo(personalDetailEvent, resultMap);
        log.info("清结算获取个人消费流水扩展字段,settleFlowId={},result={}", personalDetailEvent.getSettleFlowId(), JsonUtils.toJson(resultMap));
        return true;
    }

    private void saveOrUpdateExtInfo(MqPersonalDetailEvent personalDetailEvent, Map<String, Object> resultMap) {
        boolean checkFields = openBillExtInfoStatusService.isCheckFields(personalDetailEvent.getCompanyId(), personalDetailEvent.getAccountType());
        boolean emptyValue = ObjectUtils.isEmpty(resultMap) || "{}".equals(JsonUtils.toJson(resultMap));
        if (!checkFields && emptyValue) {
            return;
        }
        OpenBillExtInfo extInfo = openBillExtInfoDao.getById(personalDetailEvent.getSettleFlowId());
        if (extInfo == null) {
            extInfo = new OpenBillExtInfo();
            extInfo.setId(personalDetailEvent.getSettleFlowId());
            extInfo.setCompanyId(personalDetailEvent.getCompanyId());
            extInfo.setAccountType(personalDetailEvent.getAccountType());
            extInfo.setOrderId(personalDetailEvent.getOrderId());
            extInfo.setProductId(personalDetailEvent.getProductId());
            KeyValue<Integer, String> category = personalDetailEvent.getCategory();
            extInfo.setCategory(category == null ? null : category.getKey());
            extInfo.setCategoryName(category == null ? null : category.getValue());
            extInfo.setSrcData(JsonUtils.toJson(personalDetailEvent));
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
        updateDto.setSettleId(personalDetailEvent.getSettleFlowId());
        updateDto.setAccountType(personalDetailEvent.getAccountType());
        updateDto.setThirdExtFieldsJson(extInfo.getExtData());
        List<ThirdExtFieldsUpdateDTO> updateList = Lists.newArrayList();
        updateList.add(updateDto);
        billOpenApi.refreshSettleThirdExtFieldsByIds(updateList);
    }

    private Map<String, Object> getMergeResult(String settleFlowId, Map<String, Object> extInfoMap) {
        if (ObjectUtils.isEmpty(extInfoMap)) {
            return null;
        }
        OpenBillExtInfo extInfo = openBillExtInfoDao.getById(settleFlowId);
        String srcExt = extInfo == null || ObjectUtils.isEmpty(extInfo.getExtData()) ? "" : extInfo.getExtData();
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
            Object v = ObjectUtils.isEmpty(v1) ? v2 : v1;
            resultMap.put(key, v);
        }
        return resultMap;
    }

    private Map<String, Object> getExtInfoMap(MqPersonalDetailEvent personalDetailEvent) {
        FuncBillExtInfoTransformDTO funcBillExtInfoTransformDto = new FuncBillExtInfoTransformDTO();
        funcBillExtInfoTransformDto.setType(StringUtils.obj2str(personalDetailEvent.getCategory().getKey()));
        //旅客信息
        List<MqPersonalDetailDTO.OrganizationBean> consumerBeanList = personalDetailEvent.getConsumerBeanList();
        if (!ObjectUtils.isEmpty(consumerBeanList)) {
            String userIds = consumerBeanList.stream().filter(c -> !ObjectUtils.isEmpty(c.getPersonId())).map(MqPersonalDetailEvent.OrganizationBean::getPersonId).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setUserId(userIds);
            String userDeptIds = consumerBeanList.stream().filter(c -> !ObjectUtils.isEmpty(c.getDepartmentId())).map(MqPersonalDetailEvent.OrganizationBean::getDepartmentId).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setUserDeptId(userDeptIds);
            String userPhones = consumerBeanList.stream().filter(c -> !ObjectUtils.isEmpty(c.getPhone())).map(MqPersonalDetailEvent.OrganizationBean::getPhone).collect(Collectors.joining(","));
            funcBillExtInfoTransformDto.setUserPhone(userPhones);
        }
        List<VouchersOperationFlowRespRPCDTO> voucherFlowList = Lists.newArrayList();
        VouchersOperationFlowRespRPCDTO flowRespRPCDto = new VouchersOperationFlowRespRPCDTO();
        flowRespRPCDto.setId(personalDetailEvent.getSettleFlowId());
        flowRespRPCDto.setCompanyId(personalDetailEvent.getCompanyId());
        flowRespRPCDto.setEmployeeId(personalDetailEvent.getEmployeeId());
        flowRespRPCDto.setOriginalVoucherEmployeeId(personalDetailEvent.getOriginalVoucherEmployeeId());
        List<MqPersonalDetailDTO.CostAttributionBean> voucherCostAttributions = personalDetailEvent.getVoucherCostAttributions();
        if (!ObjectUtils.isEmpty(voucherCostAttributions)) {
            flowRespRPCDto.setVoucherCostAttributions(voucherCostAttributions.stream().map(c -> {
                VoucherCostAttributionRPCDTO voucherCostAttribution = new VoucherCostAttributionRPCDTO();
                voucherCostAttribution.setCostAttributionId(c.getCostAttributionId());
                voucherCostAttribution.setCostAttributionType(c.getCostAttributionCategory());
                voucherCostAttribution.setCostAttributionName(c.getCostAttributionName());
                voucherCostAttribution.setCostAttributionPath(c.getCostAttributionPath());
                return voucherCostAttribution;
            }).collect(Collectors.toList()));
        }
        voucherFlowList.add(flowRespRPCDto);
        Map<String, Map<String, Object>> voucherFlowExtInfoMap = funcVoucherBillExtService.getVoucherFlowExtInfoByFlowList(personalDetailEvent.getCompanyId(), voucherFlowList);
        Map<String, Object> resMap = voucherFlowExtInfoMap.get(personalDetailEvent.getSettleFlowId());
        buildThirdUserInfo(resMap, funcBillExtInfoTransformDto, personalDetailEvent.getCompanyId());
        return ObjectUtils.isEmpty(voucherFlowExtInfoMap) ? null : resMap;
    }

    private void buildThirdUserInfo(Map<String, Object> resMap, FuncBillExtInfoTransformDTO transformDto, String companyId) {
        List<EmployeeContract> employeeContracts = getEmployeeList(companyId, transformDto);
        if (!ObjectUtils.isEmpty(employeeContracts)) {
            String emails = employeeContracts.stream().map(EmployeeContract::getEmail)
                .filter(email -> !ObjectUtils.isEmpty(email)).collect(Collectors.joining(","));
            String depts = employeeContracts.stream().map(EmployeeContract::getThird_org_id)
                .filter(third_org_id -> !ObjectUtils.isEmpty(third_org_id)).collect(Collectors.joining(","));
            List<String> entities = Lists.newArrayList();
            employeeContracts.forEach(e -> {
                List<Map<String, String>> listMap = JsonUtils.toObj(e.getExpand(), new TypeReference<List<Map<String, String>>>() {
                });
                if (!ObjectUtils.isEmpty(listMap) && listMap.size() > 0) {
                    entities.add(listMap.get(0).get("code1"));
                }
            });
            String entity = entities.stream().filter(c -> !ObjectUtils.isEmpty(c)).collect(Collectors.joining(","));
            resMap.put("passengerMail", emails);
            resMap.put("passengerDeptId", depts);
            resMap.put("passengerEntity", entity);
        }
    }

    private List<EmployeeContract> getEmployeeList(String companyId, FuncBillExtInfoTransformDTO transformDto) {
        Set<String> userIdList = Sets.newHashSet();
        Set<String> usPhoneList = Sets.newHashSet();
        List<EmployeeContract> employeeContracts = Lists.newArrayList();
        //使用人可能是多个,userId可能为空
        if (!ObjectUtils.isEmpty(transformDto.getUserId())) {
            userIdList.addAll(Lists.newArrayList(transformDto.getUserId().split(",")));
            return employeeExtService.batchQueryEmployeeListInfo(new ArrayList<>(userIdList), companyId);
        } else if (!ObjectUtils.isEmpty(transformDto.getUserPhone())) {
            usPhoneList.addAll(Lists.newArrayList(transformDto.getUserPhone().split(",")));
            if (!ObjectUtils.isEmpty(usPhoneList)) {
                for (String phoneNum : usPhoneList) {
                    List<EmployeeContract> employeeList = employeeExtService.queryByPhoneAndCompanyId(companyId, phoneNum);
                    EmployeeContract employee = ObjectUtils.isEmpty(employeeList) ? null : employeeList.get(0);
                    if (employee != null && ObjectUtils.isEmpty(employee.getOrg_id())) {
                        employee = employeeExtService.queryEmployeeInfo(employee.getId(), companyId);
                    }
                    if (employee != null) {
                        employeeContracts.add(employee);
                    }
                }
            }
        }
        return employeeContracts;
    }
}
