package com.fenbeitong.openapi.plugin.dingtalk.listener;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingTalkCarCityDTO;
import com.fenbeitong.openapi.plugin.dingtalk.eia.constant.ApplyTripType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingTalkNoticeServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.impl.CityCodeServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: DingTalkCommon</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-11-10 13:28
 */
@Slf4j
@Primary
public abstract class DingTalkCommon {

    @Autowired
    CityCodeServiceImpl cityCodeService;

    @Autowired
    IDingtalkCorpService dingtalkCorpService;

    @Autowired
    DingTalkNoticeServiceImpl dingTalkNoticeService;

    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;

    @Autowired
    OpenSysConfigDao openSysConfigDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;


    /**
     * 获取城市ID
     */
    public String getCityId(String cityName, String companyId, String thirdUserId, DingtalkTripApplyProcessInfo.ApplyBean apply) {
        StringBuilder cityIds = new StringBuilder();
        //去除空格
        cityName = StringUtils.deleteWhitespace(cityName);
        //多个城市时用逗号分隔
        cityName = cityName.replace("，", ",").replace("、", ",");
        String[] cityNameArr = cityName.split(",");
        for (String cityNameSingle : cityNameArr) {
            Map<String, CityBaseInfo> carCodeMap = cityCodeService.getCarCode(Lists.newArrayList(cityNameSingle));
            CityBaseInfo cityBaseInfo = carCodeMap.get(cityNameSingle);
            String carCityCode = cityBaseInfo == null ? null : cityBaseInfo.getId();
            if (carCityCode == null) {
                String corpId = dingtalkCorpService.getByCompanyId(companyId).getThirdCorpId();
                log.info("发送用车城市错误信息 人员参数 :{},公司 :{}", thirdUserId, corpId);
                dingTalkNoticeService.sendOneMsg(corpId, thirdUserId, "您的分贝通用车申请中的【" + cityNameSingle + "】未获取到对应的用车城市，请检查城市名称后重新提交", apply.getThirdId());
                throw new RuntimeException(cityNameSingle + "未获取到用车城市");
            }
            if (cityIds.length() > 0) {
                cityIds.append(",");
            }
            cityIds.append(carCityCode);
        }
        return cityIds.toString();
    }


    public void setUseTime(DingtalkTripApplyProcessInfo.TripListBean tripListBean, String value) {
        List values = JsonUtils.toObj(value, List.class);
        if(CollectionUtils.isNotBlank(values) &&values.size()>=2){
            String startTime = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(values.get(0));
            String endTime = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(values.get(1));
            tripListBean.setStartTime( startTime.substring(0 , 10));
            tripListBean.setEndTime( endTime.substring(0, 10) );
        }
    }


    /**
     * 多个城市拼接
     */
    public String addCity(String oldValue, String newValue) {
        log.info("用车审批，用车城市：{}", newValue);
        if (!"".equals(oldValue) && oldValue != null) {
            return oldValue.concat(",").concat(newValue);
        } else {
            return newValue;
        }
    }


    /**
     * 截取钉钉传过来的城市字段
     */
    public StringBuilder getSubString(String cityName) {
        JSONArray jsonArray = JSONObject.parseArray(cityName);
        List<DingTalkCarCityDTO> dingTalkCarCityDTOS = jsonArray.toJavaList(DingTalkCarCityDTO.class);
        StringBuilder cityIds = new StringBuilder();
        for (int i = 0; i < 10 && i < dingTalkCarCityDTOS.size(); i++) {

            String name = dingTalkCarCityDTOS.get(i).getRowValue().get(1).getValue();
            String[] cityNameArr = name.split(",");
            cityIds.append(cityNameArr[1]).append(",");
        }
        log.info("钉钉用车审批,传入的城市：{}", cityIds.toString());
        return cityIds;
    }


    /**
     * 获取城市code
     */
    public String parseCityCode(String companyId, Map field, int type) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList("open-plus",
                Lists.newArrayList(ItemCodeEnum.DINGTALK_CITY_VERSION.getCode()));
        if (ObjectUtils.isEmpty(openMsgSetups)) {
            return parseCityCodeNew(companyId, field, type);
        }
        return parseCityCodeOld(companyId, field, type);
    }

    public String parseCityCodeNew(String companyId, Map field, int type) {
        String cityCode = null;
        Map<String, Object> extendValue = (Map<String, Object>) field.get("extendValue");
        // 编码，飞机使用
        String code = (String) extendValue.get("c");
        // 名称，火车站使用
        String name = (String) extendValue.get("n");
        int areaType = NumericUtils.obj2int(extendValue.get("areaType"));
        if (type == ApplyTripType.TRAIN.getCode()) {
            //火车站三字码
            if (areaType != 0) {
                //火车站名称搜索
                cityCode = this.getCityCodeByStationName(companyId, name);
                //火车站三字码搜索
                if (StringUtils.isEmpty(cityCode)) {
                    cityCode = this.getCityCodeByTrainStationCode(code);
                }
            } else  {
                cityCode = this.getCityCodeByStationName(companyId, name);
                //如果名称解析不到，码为大写时，尝试机场三字码解析
                if (StringUtils.isEmpty(cityCode) && code.equals(code.toUpperCase())) {
                    //机场三字码（列表选取）
                    cityCode = this.getCityCodeByAir(companyId, code);
                }
            }
        } else if (type == ApplyTripType.AIR.getCode()) {
            //国内机票
            cityCode = this.getCityCodeByAir(companyId, code);
            if (StringUtils.isEmpty(cityCode)) {
                //名称
                cityCode = this.getCityCodeByAir(companyId, name);
            }
        } else if (type == ApplyTripType.INTEL_AIR.getCode()) {
            //国际机票
            cityCode = this.getCityCodeByIntlAir(companyId, code);
        }
        if (StringUtils.isEmpty(cityCode)) {
            log.info("创建审批单失败，没有找到对应的分贝通城市，name: {}, code:{}, type: {}", name, code, type);
//            throw new FinhubException(DingtalkMessageCode.APPLY_CITY_NOT_EXIST);
        }
        return cityCode;
    }

    /**
     * 获取城市code
     */
    public String parseCityCodeOld(String companyId, Map field, int type) {
        String cityCode = null;
        Map<String, Object> extendValue = (Map<String, Object>) field.get("extendValue");
        // 编码，飞机使用
        String code = (String) extendValue.get("c");
        // 名称，火车站使用
        String name = (String) extendValue.get("n");

        if (type == ApplyTripType.TRAIN.getCode()) {
            // 火车
            cityCode = this.getCityCodeByStationName(companyId, name);
        } else if (type == ApplyTripType.AIR.getCode()) {
            //国内飞机
            cityCode = this.getCityCodeByAir(companyId, code);
        } else if (type == ApplyTripType.INTEL_AIR.getCode()) {
            //国际机票
            cityCode = this.getCityCodeByIntlAir(companyId, code);

        }

        if (StringUtils.isEmpty(cityCode)) {
            log.info("创建审批单失败，没有找到对应的分贝通城市，name: {}, code:{}, type: {}", name, code, type);
//            throw new FinhubException(DingtalkMessageCode.APPLY_CITY_NOT_EXIST);
        }
        return cityCode;
    }

    /**
     * 从钉钉机场编码中获取分贝通城市编码
     *
     * @param companyId companyId
     * @param airCode   airCode
     * @return
     */
    public String getCityCodeByAir(String companyId, String airCode) {
        Map<String, CityBaseInfo> airCodeMap = cityCodeService.getAirCode(Lists.newArrayList(airCode));
        CityBaseInfo cityBaseInfo = airCodeMap == null ? null : airCodeMap.get(airCode);
        return cityBaseInfo == null ? null : cityBaseInfo.getId();
    }

    public String getCityCodeByTrainStationCode(String trainStationCode) {
        Map<String, CityBaseInfo> airCodeMap = cityCodeService.getIdByTrainStationCode(Lists.newArrayList(trainStationCode));
        CityBaseInfo cityBaseInfo = airCodeMap.get(trainStationCode);
        return cityBaseInfo == null ? null : cityBaseInfo.getId();
    }


    /**
     * 从钉钉国际机场编码中获取分贝通城市编码
     *
     * @param companyId companyId
     * @param airCode   airCode
     * @return
     */
    public String getCityCodeByIntlAir(String companyId, String airCode) {
        Map<String, CityBaseInfo> airCodeMap = cityCodeService.getIntlAirCode(Lists.newArrayList(airCode));
        CityBaseInfo cityBaseInfo = airCodeMap.get(airCode);
        return cityBaseInfo == null ? null : cityBaseInfo.getId();
    }

    /**
     * 根据
     *
     * @param companyId   companyId
     * @param stationName stationName
     * @return
     */
    public String getCityCodeByStationName(String companyId, String stationName) {
        Map<String, CityBaseInfo> trainCodeMap = cityCodeService.getTrainCode(Lists.newArrayList(stationName));
        CityBaseInfo cityBaseInfo = trainCodeMap.get(stationName);
        return cityBaseInfo == null ? null : cityBaseInfo.getId();
    }


    /**
     * 若无法查到审批城市，则进行钉钉消息通知
     *
     * @param companyId
     * @param dingtalkUserId
     * @param msg
     */
    public void sendMsg(String companyId, String dingtalkUserId, String msg, String dataId) {
        //如果没有找到相应的城市code，需要进行相应的消息通知提示
        log.info("进行审批消息推送");
        PluginCorpDefinition byCompanyId = dingtalkCorpService.getByCompanyId(companyId);
        String corpId = byCompanyId.getThirdCorpId();
        dingTalkNoticeService.sendOneMsg(corpId, dingtalkUserId, msg, dataId);
        throw new FinhubException(300002, "行程审批单:城市不存在 " + msg);
    }

    /**
     * 获取配置
     */
    public OpenSysConfig getConfig(String companyId) {
        if (ObjectUtils.isEmpty(companyId)) {
            return null;
        }
        // 获取配置信息
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("state", 1);
        configMap.put("type", OpenSysConfigType.OPEN_CAR_CONFIG.getType());
        return openSysConfigDao.getOpenSysConfig(configMap);
    }
}
