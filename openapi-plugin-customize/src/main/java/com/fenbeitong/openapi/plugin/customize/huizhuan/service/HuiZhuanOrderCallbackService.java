package com.fenbeitong.openapi.plugin.customize.huizhuan.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.customize.huizhuan.dto.HuiZhuanCallbackRuleDTO;
import com.fenbeitong.openapi.plugin.event.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.apply.dto.CityInfoDTO;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.po.orgunit.OrgUnit;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 汇专订单回传定制
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class HuiZhuanOrderCallbackService {

    @Autowired
    private CityCodeService cityCodeService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    @Autowired
    private CommonService commonService;

    public static final String ORDER_INFO = "order_info";
    public static final String ORDER_CREATE_TIME = "order_create_time";
    public static final String CREATE_TIME = "create_time";
    public static final Integer ORDER_CATEGORY_TYPE_VALUE = 50;
    public static final String ORDER_CATEGORY_TYPE = "order_category_type";

    /**
     * 回传定制
     * 增加出发地、目的地地级市code和名称
     *
     * @param callbackData
     */
    public String callback(String callbackData, String companyId) {
        Map<String, Object> callbackInfo = JsonUtils.toObj(callbackData, Map.class);
        if (ObjectUtils.isEmpty(callbackInfo)) {
            throw new FinhubException(-9999, "订单数据为空");
        }
        OpenMsgSetup openMsgSetup =
            openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, ItemCodeEnum.ORDER_CALLBACK_COSTOM_URL.getCode());
        if (openMsgSetup == null || StringUtils.isBlank(openMsgSetup.getStrVal1())) {
            throw new FinhubException(-9999, "订单回传配置order_callback_costom_url不存在");
        }
        if (openMsgSetup == null || StringUtils.isBlank(openMsgSetup.getStrVal2())) {
            throw new FinhubException(-9999, "订单回传配置callback_config不存在");
        }
        // 判断是否是汇专企业
        OpenMsgSetup openMsgSetupHz = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId,ItemCodeEnum.HZ_ORDER_CALLBACK_COSTOM_URL.getCode());
        if (!ObjectUtils.isEmpty(openMsgSetupHz)) {
            callbackInfo = this.callbackInfoChange(callbackInfo);
        }
        HuiZhuanCallbackRuleDTO huiZhuanCallbackRuleDTO =
            JsonUtils.toObj(openMsgSetup.getStrVal2(), HuiZhuanCallbackRuleDTO.class);
        setPrefecturalLevelCity(callbackInfo, huiZhuanCallbackRuleDTO);
        boolean filter = filterPassengerInDepartment(callbackInfo, huiZhuanCallbackRuleDTO, companyId);
        // 如果被过滤而不需要发送，则从配置中读取返回报文，方便统一判断推送结果
        String result = openMsgSetup.getStrVal3();
        if (filter) {
            result = RestHttpUtils.postJson(openMsgSetup.getStrVal1(), JsonUtils.toJson(callbackInfo));
        }
        return result;
    }

    /**
     * 汇专定制 订单回传 只给汇专传 ORDER_CREATE_TIME 字段
     *
     * @param callbackInfo
     * @return
     */
    private Map<String, Object> callbackInfoChange(Map<String, Object> callbackInfo) {
        if (callbackInfo.containsKey(ORDER_INFO)) {
             Object o = callbackInfo.get(ORDER_INFO);
             Map<String, Object> map = JsonUtils.toObj(JsonUtils.toJson(o), new TypeReference<Map<String, Object>>() {
             });
             if (map.containsKey(ORDER_CATEGORY_TYPE) && !ObjectUtils.isEmpty(map.get(ORDER_CATEGORY_TYPE))) {
                  Integer type =(Integer) map.get(ORDER_CATEGORY_TYPE);
                  if (ORDER_CATEGORY_TYPE_VALUE.equals(type)) {
                      if (map.containsKey(CREATE_TIME) && !ObjectUtils.isEmpty(map.get(CREATE_TIME))) {
                          map.put(ORDER_CREATE_TIME,map.get(CREATE_TIME));
                      }
                      callbackInfo.put(ORDER_INFO, JSON.parse(JSON.toJSONString(map)));
                  }
             }
        }
        return callbackInfo;
    }

    /**
     * 处理火车地级市，将火车场景的起始目的地转换出地级市
     *
     * @param callbackInfo
     * @param huiZhuanCallbackRuleDTO
     */
    private void setPrefecturalLevelCity(Map<String, Object> callbackInfo,
        HuiZhuanCallbackRuleDTO huiZhuanCallbackRuleDTO) {
        Boolean prefecturalLevelCity = huiZhuanCallbackRuleDTO.getPrefecturalLevelCity();
        if (ObjectUtils.isEmpty(huiZhuanCallbackRuleDTO) || ObjectUtils.isEmpty(prefecturalLevelCity)
            || !prefecturalLevelCity) {
            return;
        }
        //处理火车出发地目的地的地级市
        Map<String, Object> trainInfo = (Map) callbackInfo.get("train_info");
        if (!ObjectUtils.isEmpty(trainInfo)) {
            String fromCityCode = StringUtils.obj2str(trainInfo.get("from_city_code"));
            String toCityCode = StringUtils.obj2str(trainInfo.get("to_city_code"));
            if (!StringUtils.isBlank(fromCityCode)) {
                CityInfoDTO cityInfoDTO = cityCodeService.cityCodeLevelUpToCityInfo(fromCityCode);
                trainInfo.put("from_prefectural_level_city_name", cityInfoDTO.getName());
                trainInfo.put("from_prefectural_level_city_code", cityInfoDTO.getId());
            }
            if (!StringUtils.isBlank(toCityCode)) {
                CityInfoDTO cityInfoDTO = cityCodeService.cityCodeLevelUpToCityInfo(toCityCode);
                trainInfo.put("to_prefectural_level_city_name", cityInfoDTO.getName());
                trainInfo.put("to_prefectural_level_city_code", cityInfoDTO.getId());
            }
        }
    }

    /**
     * 过滤乘客所在部门，仅支持机酒车火
     * 计算配置部门与人员full_org_id是否有交集
     * 有交集返回true,未配置返回true
     *
     * @param callbackInfo
     * @param huiZhuanCallbackRuleDTO
     */
    private boolean filterPassengerInDepartment(Map<String, Object> callbackInfo,
        HuiZhuanCallbackRuleDTO huiZhuanCallbackRuleDTO, String companyId) {
        if (ObjectUtils.isEmpty(huiZhuanCallbackRuleDTO) || ObjectUtils.isEmpty(
            huiZhuanCallbackRuleDTO.getPassengerInDepartment())) {
            return true;
        }
        int orderCategory = NumericUtils.obj2int(MapUtils.getValueByExpress(callbackInfo, "order_info"
            + ":order_category_type"));
        if (orderCategory == OrderCategoryEnum.Air.getKey() || orderCategory == OrderCategoryEnum.Train.getKey()) {
            return filterPassengerInfoTrain(callbackInfo, huiZhuanCallbackRuleDTO, companyId);
        } else if (orderCategory == OrderCategoryEnum.Taxi.getKey()) {
            return filterPassengerInfoTaxi(callbackInfo, huiZhuanCallbackRuleDTO, companyId);
        } else if (orderCategory == OrderCategoryEnum.Hotel.getKey()) {
            return filterPassengerInfoHotel(callbackInfo, huiZhuanCallbackRuleDTO, companyId);
        }
        return false;
    }

    /**
     * 火车、机票、乘车人过滤,取passenger_info.org_unit_id
     *
     * @param callbackInfo
     * @param huiZhuanCallbackRuleDTO
     * @param companyId
     * @return
     */
    private boolean filterPassengerInfoTrain(Map<String, Object> callbackInfo,
        HuiZhuanCallbackRuleDTO huiZhuanCallbackRuleDTO, String companyId) {
        String orgUnitId = StringUtils.obj2str(MapUtils.getValueByExpress(callbackInfo, "passenger_info"
            + ":org_unit_id"));
        if (ObjectUtils.isEmpty(orgUnitId)) {
            return false;
        }
        return filterFullOrgUnitId(Lists.newArrayList(orgUnitId), huiZhuanCallbackRuleDTO.getPassengerInDepartment(),
            companyId);
    }

    /**
     * 打车乘车人过滤,取passenger_info.unit_id
     *
     * @param callbackInfo
     * @param huiZhuanCallbackRuleDTO
     * @param companyId
     * @return
     */
    private boolean filterPassengerInfoTaxi(Map<String, Object> callbackInfo,
        HuiZhuanCallbackRuleDTO huiZhuanCallbackRuleDTO, String companyId) {
        String orgUnitId = StringUtils.obj2str(MapUtils.getValueByExpress(callbackInfo, "passenger_info"
            + ":unit_id"));
        if (ObjectUtils.isEmpty(orgUnitId)) {
            return false;
        }
        return filterFullOrgUnitId(Lists.newArrayList(orgUnitId), huiZhuanCallbackRuleDTO.getPassengerInDepartment(),
            companyId);
    }

    /**
     * 酒店入住人、同住人部门过滤,guest_info.org_unit_id&guest_info.live_with.org_unit_id
     *
     * @param callbackInfo
     * @param huiZhuanCallbackRuleDTO
     * @param companyId
     * @return
     */
    private boolean filterPassengerInfoHotel(Map<String, Object> callbackInfo,
        HuiZhuanCallbackRuleDTO huiZhuanCallbackRuleDTO, String companyId) {
        //酒店入住人三方部门id。 可能是多个（同住）
        if (ObjectUtils.isEmpty(callbackInfo.get("guest_info"))) {
            return false;
        }
        List<String> orgUnitId = Lists.newArrayList();
        List<Map> guestInfoList = (List<Map>) callbackInfo.get("guest_info");
        for (Map<String, Object> guestInfo : guestInfoList) {
            String guestOrgUnitId = StringUtils.obj2str(guestInfo.get("org_unit_id"));
            if (!StringUtils.isBlank(guestOrgUnitId)) {
                orgUnitId.add(guestOrgUnitId);
            }
            String liveWithOrgUnitId = StringUtils.obj2str(MapUtils.getValueByExpress(guestInfo, "live_with"
                + ":org_unit_id"));
            if (!StringUtils.isBlank(liveWithOrgUnitId)) {
                orgUnitId.add(liveWithOrgUnitId);
            }
        }
        return filterFullOrgUnitId(orgUnitId, huiZhuanCallbackRuleDTO.getPassengerInDepartment(),
            companyId);
    }


    /**
     * 对比部门列表是否有交集
     *
     * @param orgUnitIdList        订单的下单人或使用人部门id列表
     * @param configDepartmentList 配置的下单人或或使用人部门id列表
     * @param companyId
     * @return
     */
    private boolean filterFullOrgUnitId(List<String> orgUnitIdList, List<String> configDepartmentList,
        String companyId) {
        if (ObjectUtils.isEmpty(orgUnitIdList)) {
            log.info("未获取到部门id");
            return false;
        }
        for (String orgUnitId : orgUnitIdList) {
            if (ObjectUtils.isEmpty(orgUnitId)) {
                continue;
            }
            //全路径部门id与配置有交集
            List<OrgUnit> orgUnits = orgUnitService.listAllParentOrgUnits(companyId, orgUnitId);
            List<String> parentIdList = orgUnits.stream().map(OrgUnit::getId).collect(Collectors.toList());
            parentIdList.retainAll(configDepartmentList);
            if (!ObjectUtils.isEmpty(parentIdList)) {
                return true;
            }
        }
        return false;
    }
}
