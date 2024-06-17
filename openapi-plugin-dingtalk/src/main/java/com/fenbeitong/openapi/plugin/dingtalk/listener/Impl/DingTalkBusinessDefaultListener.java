package com.fenbeitong.openapi.plugin.dingtalk.listener.Impl;


import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.constant.ApplyTripType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkBusinessListener;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkCommon;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.impl.CityCodeServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class DingTalkBusinessDefaultListener extends DingTalkCommon implements DingTalkBusinessListener {

    @Autowired
    private CityCodeServiceImpl cityCodeService;

    @Override
    public void fillTripBeanFields(String companyId, List rowValues, boolean returnTrip, DingtalkTripApplyProcessInfo.TripListBean tripListBean, boolean isIntlAir, String dingtalkUserId, String instanceId, Integer applyDepartureDate, int type) {
        tripListBean.setEstimatedAmount(0);
        String startCityName = "";
        String endCityName = "";
        String startCityCode = "";
        String endCityCode = "";
        String startTime = "";
        String endTime = "";
        int fieldLength = 6;
        for (int i = 0; i < fieldLength; i++) {
            Map field = (Map) rowValues.get(i);
            String label = (String) field.get("label");
            String value = (String) field.get("value");
            switch (label) {
                case DingTalkConstant.business.KEY_START_CITY:
                    if (!returnTrip) {
                        startCityCode = this.parseCityCode(companyId, field, type);
                        Map<String, Object> extendValue = (Map<String, Object>) field.get("extendValue");
                        //发送错误信息使用城市名称
                        startCityName = (String) extendValue.get("n");
                        log.info("获取的出发城市名称 {}", startCityName);
                        tripListBean.setStartCityId(startCityCode);
                    } else {
                        startCityCode = this.parseCityCode(companyId, field, type);
                        tripListBean.setArrivalCityId(startCityCode);
                    }
                    break;
                case DingTalkConstant.business.KEY_ARRIVAL_CITY:
                    // 是否是返程
                    if (!returnTrip) {
                        Map<String, Object> extendValue = (Map<String, Object>) field.get("extendValue");
                        //发送错误信息使用城市名称
                        endCityName = (String) extendValue.get("n");
                        log.info("获取的目的城市名称 {}", endCityName);
                        endCityCode = this.parseCityCode(companyId, field, type);
                        tripListBean.setArrivalCityId(endCityCode);
                    } else {
                        endCityCode = this.parseCityCode(companyId, field, type);
                        tripListBean.setStartCityId(endCityCode);
                    }
                    break;
                case DingTalkConstant.business.KEY_START_TIME:
                    startTime = value.substring(0, 10);
                    // 截取日期前10位 yyyy-MM-dd
                    tripListBean.setStartTime(startTime);
                    break;
                case DingTalkConstant.business.KEY_END_TIME:
                    endTime = value.substring(0, 10);
                    tripListBean.setEndTime(endTime);
                    if (0 == applyDepartureDate) {
                        tripListBean.setStartTime(endTime);
                    }
                    break;
                case DingTalkConstant.business.KEY_APPLY_TYPE:
                    log.info("解析交通工具");
                    break;
                case DingTalkConstant.business.KEY_TRIP_TYPE:
                    log.info("解析单程往返");
                    break;
                case DingTalkConstant.business.KEY_TRIP_DURATION:
                    log.info("解析时长");
                    break;
                default:
                    break;
            }
        }
        //时长字段数据用于发送城市code无法查询进行推送消息使用
        if (StringUtils.isBlank(startCityCode) || StringUtils.isBlank(endCityCode)) {
            String msg = "通知：您创建的" + startTime + "到" + endTime + "的" + startCityName + "到" + endCityName + "的分贝通差旅审批单，" +
                    "因审批单中的城市不符合分贝通标准创建失败，请联系管理员";
            sendMsg(companyId, dingtalkUserId, msg, instanceId);
        }
    }


    @Override
    public void fillIntlAirTripBeanFields(String companyId, List rowValues, boolean returnTrip, DingtalkTripApplyProcessInfo.TripListBean tripListBean, boolean isIntlAir, String dingtalkUserId, String instanceId, Integer applyDepartureDate) {

        tripListBean.setEstimatedAmount(0);
        String startCityName = "";
        String endCityName = "";
        String startCityCode = "";
        String endCityCode = "";
        String startTime = "";
        String endTime = "";
        int fieldLength = 6;
        for (int i = 0; i < fieldLength; i++) {
            Map field = (Map) rowValues.get(i);
            log.info("解析field表单数据 {}", JsonUtils.toJson(field));
            String label = (String) field.get("label");
            String value = (String) field.get("value");
            Map<String, Object> extendValue = (Map<String, Object>) field.get("extendValue");
            log.info("获取extendValue内容 {}", JsonUtils.toJson(extendValue));
            switch (label) {
                case DingTalkConstant.business.KEY_START_CITY:
                    log.info("解析出发城市 ，消息推送");
                    startCityCode = this.parseCityCode(companyId, field, ApplyTripType.INTEL_AIR.getCode());
                    tripListBean.setStartCityId(startCityCode);
                    break;
                case DingTalkConstant.business.KEY_ARRIVAL_CITY:
                    log.info("解析目的城市 ，消息推送");
                    endCityCode = this.parseCityCode(companyId, field, ApplyTripType.INTEL_AIR.getCode());
                    tripListBean.setArrivalCityId(endCityCode);
                    break;
                case DingTalkConstant.business.KEY_START_TIME:
                    log.info("解析开始时间 ，消息推送");
                    // 截取日期前10位 yyyy-MM-dd
                    if (!returnTrip) {//是否为往返
                        startTime = value.substring(0, 10);
                        tripListBean.setStartTime(startTime);
                        //单程
                        tripListBean.setTripType(1);
                    } else {
                        startTime = value.substring(0, 10);
                        tripListBean.setStartTime(startTime);
                        tripListBean.setBackStartTime(startTime);
                        tripListBean.setTripType(2);
                    }
                    break;
                case DingTalkConstant.business.KEY_END_TIME:
                    log.info("解析结束时间 ，消息推送");
                    endTime = value.substring(0, 10);
                    if (!returnTrip) {
                        tripListBean.setEndTime(endTime);
                    } else {
                        tripListBean.setEndTime(endTime);
                        tripListBean.setBackEndTime(endTime);
                        if (0 == applyDepartureDate) {
                            tripListBean.setBackStartTime(endTime);
                        }
                    }
                    break;
                case DingTalkConstant.business.KEY_APPLY_TYPE:
                    log.info("解析交通工具");
                    break;
                case DingTalkConstant.business.KEY_TRIP_TYPE:
                    log.info("解析单程往返");
                    break;
                case DingTalkConstant.business.KEY_TRIP_DURATION:
                    log.info("解析时长");
                    break;
                default:
                    break;
            }
        }
        //时长字段数据用于发送城市code无法查询进行推送消息使用
        if (StringUtils.isBlank(startCityCode) || StringUtils.isBlank(endCityCode)) {
            log.info("default send msg");
            String msg = "通知：您创建的" + startTime + "到" + endTime + "的" + startCityName + "到" + endCityName + "的分贝通差旅审批单，" +
                    "因审批单中的城市不符合分贝通标准创建失败，请联系管理员";
            this.sendMsg(companyId, dingtalkUserId, msg, instanceId);
        }
    }

    @Override
    public DingtalkTripApplyProcessInfo.TripListBean createHotelTripBean(String companyId, String dingtalkUserId, List rowValues, String instanceId) {
        DingtalkTripApplyProcessInfo.TripListBean tripListBean = new DingtalkTripApplyProcessInfo.TripListBean();
        tripListBean.setType(ApplyTripType.HOTEL.getCode());
        tripListBean.setEstimatedAmount(0);
        int fieldLength = 6;
        for (int i = 0; i < fieldLength; i++) {
            Map field = (Map) rowValues.get(i);
            String label = (String) field.get("label");
            String value = (String) field.get("value");
            switch (label) {
                case DingTalkConstant.business.KEY_ARRIVAL_CITY:
                    if (StringUtils.isNotEmpty(value)) {
                        value = value.trim();
                    }
                    Map<String, CityBaseInfo> hotelCodeMap = cityCodeService.getHotelCode(Lists.newArrayList(value));
                    CityBaseInfo cityBaseInfo = hotelCodeMap.get(value);
                    String hotelCityCode = cityBaseInfo == null ? null : cityBaseInfo.getId();
                    if (!StringUtils.isEmpty(hotelCityCode)) {
                        tripListBean.setArrivalCityId(hotelCityCode);
                        tripListBean.setStartCityId(hotelCityCode);
                    } else {
                        // 通知信息
                        StringBuffer sb = new StringBuffer();
                        sb.append("您好，您提交的酒店审批单没有创建成功, 原因:\n")
                                .append("未能查询到名为“").append(value).append("”")
                                .append("的城市, 请核实城市名称后提交。");
                        this.sendMsg(companyId, dingtalkUserId, sb.toString(), instanceId);
                        return null;
                    }

                    break;
                case DingTalkConstant.business.KEY_START_TIME:
                    // 截取日期前10位 yyyy-MM-dd
                    tripListBean.setStartTime(value.substring(0, 10));
                    break;
                case DingTalkConstant.business.KEY_END_TIME:
                    tripListBean.setEndTime(value.substring(0, 10));
                    break;
                default:
                    break;
            }
        }
        return tripListBean;
    }
}
