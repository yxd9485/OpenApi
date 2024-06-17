package com.fenbeitong.openapi.plugin.func.order.service;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderCostDetailDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.SaasApplyCustomFieldRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.dto.FbCostAttributionDTO;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenCostAttributionDTO;
import com.fenbeitong.openapi.plugin.support.common.service.OpenCostAttrTranService;
import com.fenbeitong.openapi.plugin.support.util.EmployeeUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO.ThirdCommonExpress;
import static com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO.ThirdCostExpress;
import static com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO.ThirdUserPhoneExpress;

/**
 * <p>Title: FuncThirdInfoServiceImpl</p>
 * <p>Description: 三方信息服务类 </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/6 5:44 PM
 */
@SuppressWarnings("all")
@ServiceAspect
@Service
public class FuncThirdInfoServiceImpl {

    @DubboReference(check = false)
    private ICommonService commonService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private EmployeeUtils employeeUtils;

    @Autowired
    private OpenCostAttrTranService openCostAttrTranService;

    public void setThirdInfoMap(String companyId, Map srcMap, FuncThirdInfoExpressDTO expressDto) {
        List<ThirdCommonExpress> userExpressList = expressDto.getUserExpressList();
        Map<String, Object> thirdInfoMap = Maps.newLinkedHashMap();
        //设置人员三方id
        setThirdId(companyId, srcMap, userExpressList, thirdInfoMap, 3);
        List<ThirdCommonExpress> deptExpressList = expressDto.getDeptExpressList();
        //设置部门三方id
        setThirdId(companyId, srcMap, deptExpressList, thirdInfoMap, 1);
        List<ThirdCostExpress> costExpressList = expressDto.getCostExpressList();
        //设置费用归属三方id
        setCostThirdId(companyId, thirdInfoMap, costExpressList);
        List<ThirdCommonExpress> applyExpressList = expressDto.getApplyExpressList();
        //设置审批单三方id
        setApplyThird(companyId, srcMap, thirdInfoMap, applyExpressList);
        //设置手机号用户信息
        List<ThirdUserPhoneExpress> userPhoneExpressList = expressDto.getUserPhoneExpressList();
        setPhoneUser(thirdInfoMap, userPhoneExpressList, companyId);
        if (!ObjectUtils.isEmpty(thirdInfoMap)) {
            srcMap.put("third_info", thirdInfoMap);
        }
    }

    public Map<String, Object> setThirdInfoEntity(String companyId, Map srcMap, FuncThirdInfoExpressDTO expressDto) {
        List<ThirdCommonExpress> userExpressList = expressDto.getUserExpressList();
        Map<String, Object> thirdInfoMap = Maps.newLinkedHashMap();
        //设置人员三方id
        setThirdId(companyId, srcMap, userExpressList, thirdInfoMap, 3);
        List<ThirdCommonExpress> deptExpressList = expressDto.getDeptExpressList();
        //设置部门三方id
        setThirdId(companyId, srcMap, deptExpressList, thirdInfoMap, 1);
        List<ThirdCostExpress> costExpressList = expressDto.getCostExpressList();
        //设置费用归属三方id
        setCostThirdId(companyId, thirdInfoMap, costExpressList);
        List<ThirdCommonExpress> applyExpressList = expressDto.getApplyExpressList();
        //设置审批单三方id
        setApplyThird(companyId, srcMap, thirdInfoMap, applyExpressList);
        //设置手机号用户信息
        List<ThirdUserPhoneExpress> userPhoneExpressList = expressDto.getUserPhoneExpressList();
        setPhoneUser(thirdInfoMap, userPhoneExpressList, companyId);
        return thirdInfoMap;
    }

    private void setPhoneUser(Map<String, Object> thirdInfoMap, List<ThirdUserPhoneExpress> userPhoneExpressList,
        String companyId) {
        if (ObjectUtils.isEmpty(userPhoneExpressList)) {
            return;
        }
        for (ThirdUserPhoneExpress express : userPhoneExpressList) {
            if (ObjectUtils.isEmpty(express.getPhone())) {
                continue;
            }
            EmployeeContract employee = employeeExtService.queryEmployeeInfoByPhone(companyId, express.getPhone());
            if (ObjectUtils.isEmpty(employee)) {
                continue;
            }
            String thirdOrgId = employee.getThird_org_id();
            if (!ObjectUtils.isEmpty(thirdOrgId)) {
                Object thirdIdStr = thirdInfoMap.get(express.getTgtDept());
                thirdInfoMap.put(express.getTgtDept(),
                    ObjectUtils.isEmpty(thirdIdStr) ? thirdOrgId : (thirdIdStr + "," + thirdOrgId));
            }
            String thirdUserId = employee.getThird_employee_id();
            if (!ObjectUtils.isEmpty(thirdUserId)) {
                Object thirdIdStr = thirdInfoMap.get(express.getTgtUser());
                thirdInfoMap.put(express.getTgtUser(),
                    ObjectUtils.isEmpty(thirdIdStr) ? thirdUserId : (thirdIdStr + "," + thirdUserId));
            }
        }
    }

    private void setApplyThird(String companyId, Map srcMap, Map<String, Object> thirdInfoMap,
        List<ThirdCommonExpress> applyExpressList) {
        Map<String, ThirdCommonExpress> expressMap =
            applyExpressList.stream().collect(Collectors.toMap(ThirdCommonExpress::getExpress, Function.identity()));
        Map<String, List<String>> idMapList = getDataFromSrcMap(srcMap,
            applyExpressList.stream().map(ThirdCommonExpress::getExpress).collect(Collectors.toList()));
        List<String> idList = idMapList.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
        idMapList.keySet().forEach(express -> {
            ThirdCommonExpress thirdCommonExpress = expressMap.get(express);
            String tgtField = thirdCommonExpress.getTgtField();
            String group = thirdCommonExpress.getGroup();
            idMapList.get(express).forEach(id -> {
                SaasApplyCustomFieldRespDTO applyCustomFields = commonApplyService.getApplyCustomFields(companyId, id);
                String thirdId = applyCustomFields == null ? null : applyCustomFields.getThirdId();
                String customFields = applyCustomFields == null ? null : applyCustomFields.getCustomFields();
                if (!ObjectUtils.isEmpty(thirdId)) {
                    Object thirdIdStr = thirdInfoMap.get(tgtField);
                    thirdInfoMap.put(tgtField,
                        ObjectUtils.isEmpty(thirdIdStr) ? thirdId : (thirdIdStr + "," + thirdId));
                }
                if (!ObjectUtils.isEmpty(customFields)) {
                    Object thirdCustomFieldStr = thirdInfoMap.get(group + "_custom_fields");
                    thirdInfoMap.put(group + "_custom_fields", ObjectUtils.isEmpty(thirdCustomFieldStr) ?
                        customFields :
                        (thirdCustomFieldStr + "," + customFields));
                }
            });
        });
    }

    private void setCostThirdId(String companyId, Map<String, Object> thirdInfoMap,
        List<ThirdCostExpress> costExpressList) {
        if (ObjectUtils.isEmpty(costExpressList)) {
            return;
        }
        Map<Integer, List<ThirdCostExpress>> thirdCostExpressMap =
            costExpressList.stream().collect(Collectors.groupingBy(ThirdCostExpress::getCostCategory));
        thirdCostExpressMap.forEach((k, v) -> {
            List<String> idList = v.stream().map(ThirdCostExpress::getId).collect(Collectors.toList());
            List<CommonIdDTO> idDtoList =
                ObjectUtils.isEmpty(idList) ? null : commonService.queryIdDTO(companyId, idList, 1, k);
            Map<String, CommonIdDTO> commonIdDtoMap = ObjectUtils.isEmpty(idDtoList) ?
                null :
                idDtoList.stream().collect(Collectors.toMap(CommonIdDTO::getId, Function.identity()));
            if (ObjectUtils.isEmpty(commonIdDtoMap)) {
                return;
            }
            v.forEach(thirdCommonExpress -> {
                String tgtField = thirdCommonExpress.getTgtField();
                idList.forEach(id -> {
                    if (commonIdDtoMap != null) {
                        CommonIdDTO commonIdDto = commonIdDtoMap.get(id);
                        String thirdId = commonIdDto == null ? null : commonIdDto.getThirdId();
                        if (!ObjectUtils.isEmpty(thirdId) && id.equals(thirdCommonExpress.getId())) {
                            Object thirdIdStr = thirdInfoMap.get(tgtField);
                            thirdInfoMap.put(tgtField,
                                ObjectUtils.isEmpty(thirdIdStr) ? thirdId : (thirdIdStr + "," + thirdId));
                        }
                    }
                });
            });
        });
    }

    private void setThirdId(String companyId, Map srcMap, List<ThirdCommonExpress> expressList,
        Map<String, Object> thirdInfoMap, int bussinessType) {
        if (ObjectUtils.isEmpty(expressList)) {
            return;
        }
        Map<String, ThirdCommonExpress> expressMap =
            expressList.stream().collect(Collectors.toMap(ThirdCommonExpress::getExpress, Function.identity()));
        Map<String, List<String>> idMapList = getDataFromSrcMap(srcMap,
            expressList.stream().map(ThirdCommonExpress::getExpress).collect(Collectors.toList()));
        List<String> idList = idMapList.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
        List<CommonIdDTO> userIdDtoList =
            ObjectUtils.isEmpty(idList) ? null : commonService.queryIdDTO(companyId, idList, 1, bussinessType);
        Map<String, CommonIdDTO> commonIdDtoMap = ObjectUtils.isEmpty(userIdDtoList) ?
            null :
            userIdDtoList.stream().collect(Collectors.toMap(CommonIdDTO::getId, Function.identity()));
        if (ObjectUtils.isEmpty(commonIdDtoMap)) {
            return;
        }
        idMapList.keySet().forEach(express -> {
            ThirdCommonExpress thirdCommonExpress = expressMap.get(express);
            String tgtField = thirdCommonExpress.getTgtField();
            idMapList.get(express).forEach(id -> {
                CommonIdDTO commonIdDto = commonIdDtoMap.get(id);
                String thirdId = commonIdDto.getThirdId();
                if (!ObjectUtils.isEmpty(thirdId)) {
                    Object thirdIdStr = thirdInfoMap.get(tgtField);
                    thirdInfoMap.put(tgtField,
                        ObjectUtils.isEmpty(thirdIdStr) ? thirdId : (thirdIdStr + "," + thirdId));
                }
            });
        });
    }

    private Map<String, List<String>> getDataFromSrcMap(Map srcMap, List<String> expressList) {
        Map<String, List<String>> vlaueMap = Maps.newHashMap();
        for (String express : expressList) {
            Object value = MapUtils.getValueByExpress(srcMap, express);
            List<String> valueList = Lists.newArrayList();
            if (value instanceof String) {
                valueList.add((String) value);
            } else if (value instanceof List) {
                ((List) value).forEach(rowValue -> {
                    if (rowValue instanceof String) {
                        valueList.add((String) value);
                    } else if (rowValue instanceof List) {
                        valueList.addAll((List<String>) rowValue);
                    }
                });
            }
            if (!ObjectUtils.isEmpty(valueList)) {
                vlaueMap.put(express, valueList);
            }
        }
        return vlaueMap;
    }

    /**
     * 分摊三方信息处理
     * 将场景分摊信息转换为openApi分摊DTO
     * 如果是使用了ETL，出参是大Map的，会自动放入saas_info中
     * 如果出参已封装DTO，transformMap给null，需要自已将本方法返回的分摊信息set入自己的DTO
     * @param companyId
     * @param transformMap 订单详情出参大Map
     * @param costDetail 场景的分摊信息Map
     * @return
     */
    public List<OpenCostAttributionDTO> setCostAttribution(String companyId, Map transformMap, Map costDetail) {
        if (ObjectUtils.isEmpty(costDetail)) {
            return null;
        }
        OrderCostDetailDTO orderCostDetailDTO = JsonUtils.toObj(JsonUtils.toJson(costDetail), OrderCostDetailDTO.class);
        List<OrderCostDetailDTO.Costattributiongrouplist> costAttributionGroupList =
            orderCostDetailDTO.getCostAttributionGroupList();
        List<FbCostAttributionDTO> fbCostAttributionDTOList = new ArrayList<>();
        costAttributionGroupList.forEach(costAttributionGroup -> {
            FbCostAttributionDTO fbCostAttributionDTO = new FbCostAttributionDTO();
            fbCostAttributionDTO.setCategory(costAttributionGroup.getCategory());
            fbCostAttributionDTO.setCategoryName(costAttributionGroup.getCategoryName());
            fbCostAttributionDTO.setRecordId(costAttributionGroup.getRecordId());
            List<FbCostAttributionDTO.CostAttributionListDTO> costAttributionList = new ArrayList<>();
            fbCostAttributionDTO.setCostAttributionList(costAttributionList);
            List<OrderCostDetailDTO.Costattributionlist> costAttributionListSrc =
                costAttributionGroup.getCostAttributionList();
            costAttributionListSrc.forEach(costAttributionSrc -> {
                FbCostAttributionDTO.CostAttributionListDTO costAttributionListDTO =
                    new FbCostAttributionDTO.CostAttributionListDTO();
                costAttributionListDTO.setPrice(costAttributionSrc.getPrice());
                costAttributionListDTO.setName(costAttributionSrc.getName());
                costAttributionListDTO.setId(costAttributionSrc.getId());
                costAttributionListDTO.setWeight(costAttributionSrc.getWeight());
                costAttributionList.add(costAttributionListDTO);
            });
            fbCostAttributionDTOList.add(fbCostAttributionDTO);
        });
        List<OpenCostAttributionDTO> openCostAttribution =
            openCostAttrTranService.fbCostListToOpenCostList(companyId, fbCostAttributionDTOList, true);
        if (!ObjectUtils.isEmpty(transformMap)) {
            Map saasInfo = (Map) MapUtils.getValueByExpress(transformMap, "saas_info");
            if (!ObjectUtils.isEmpty(saasInfo)) {
                saasInfo.put("cost_attribution", openCostAttribution);
            }
        }
        return openCostAttribution;
    }

}
