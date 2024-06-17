package com.fenbeitong.openapi.plugin.wechat.eia.listener.Impl;


import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.CarApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import com.fenbeitong.openapi.plugin.wechat.common.notice.sender.WeChatNoticeSender;
import com.fenbeitong.openapi.plugin.wechat.eia.enums.ApplyType;
import com.fenbeitong.openapi.plugin.wechat.eia.enums.FbTripType;
import com.fenbeitong.openapi.plugin.wechat.eia.enums.WeChatApplyContentControl;
import com.fenbeitong.openapi.plugin.wechat.eia.listener.AbstractWeChatCommon;
import com.fenbeitong.openapi.plugin.wechat.eia.listener.WeChatCarListener;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ServiceAspect
@Service
@Slf4j
public class WeChatCarUnlimitedCityListener extends AbstractWeChatCommon implements WeChatCarListener {

    @Autowired
    WeChatNoticeSender weChatNoticeSender;


    @Override
    public String filterEiaWeChat(WeChatApprovalDetail.WeChatApprovalInfo approvalInfo, List<ApprovalInfo.TripListBean> tripBeans, List<WeChatApprovalDetail.Content> contens, CarApprovalInfo processInfo, int type, String companyId, String userId) {

        //进行具体行程数据解析，包括机票，酒店，火车的行程出发目的城市，出发目的时间，金额相关信息
        List<String> sceneList = Lists.newArrayList();
        sceneList.add(car.APPLY_USER_CAR);

        String departureCityCode = "";
        String arrivedCityCode = "";
        String departureDate = "";
        String arrivedDate = "";
        String departurePlace = "";
        String arrivedPlace = "";
        String thirdRemark = "";
        String contentValueNumber = "";
        String newMoney = "";
        for (WeChatApprovalDetail.Content content : contens) {
            String control = content.getControl();
            if (WeChatApplyContentControl.SELECTOR.getValue().equals(control)) {//选择器，包括行程的列表，具体审批包含哪些审批
                log.info("解析审批单具体场景 {}", JsonUtils.toJson(sceneList));
            } else if (WeChatApplyContentControl.TEXT.getValue().equals(control)) {//出发和目的城市
                List<WeChatApprovalDetail.Title> titles = content.getTitles();
                String text = titles.get(0).getText();
                WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                if (car.KEY_START_CITY.equals(text)) {
                    //获取出发城市名称
                    departurePlace = contentValue.getText().trim();
                    log.info("解析审批单具体出发城市 {}", departurePlace);
                    //根据城市名称获取城市code,获取哪些城市code，需要根据场景来进行区分
                    departureCityCode = getCityCodeByStationName(departurePlace);
                } else if (car.KEY_ARRIVAL_CITY.equals(text)) {
                    //到达城市名称
                    arrivedPlace = contentValue.getText().trim();
                    arrivedCityCode = getCityCodeByStationName(departurePlace);
                    log.info("解析审批单具体目的城市 {}", departurePlace);
                } else if (car.USE_CAR_CITY.equals(text)) {
                    arrivedPlace = contentValue.getText().trim();
                    String carCityId = getCityCodeByStationName(arrivedPlace);
                    departureCityCode = carCityId;
                    arrivedCityCode = carCityId;
                    log.info("解析审批单具体用车城市 {}", departurePlace);
                }
            } else if (WeChatApplyContentControl.DATE.getValue().equals(control)) {//出发和到达时间
                List<WeChatApprovalDetail.Title> titles = content.getTitles();
                String text = titles.get(0).getText();
                WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                if (car.BEGIN_DATE.equals(text)) {
                    long sDepatureTimestamp = contentValue.getDate().getSTimestamp();
                    // TODO 如果控件时间类型为年月日时分秒，则会解析错误,需要适配
                    departureDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(Long.parseLong(sDepatureTimestamp + "") * 1000));
                    log.info("解析审批单具体出发日期 {}", departureDate);
                } else if (car.END_DATE.equals(text)) {
                    long sArrivedTimestamp = contentValue.getDate().getSTimestamp();
                    arrivedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(Long.parseLong(sArrivedTimestamp + "") * 1000));
                    log.info("解析审批单具体到达日期 {}", arrivedDate);
                }
            } else if (WeChatApplyContentControl.MONEY.getValue().equals(control)) {//金额控件,用车费用，根据不同企业的配置，可能为空
                List<WeChatApprovalDetail.Title> titles = content.getTitles();
                String text = titles.get(0).getText();
                WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                if (car.CAR_MONEY.equals(text)) {//具体的金额数据
                    newMoney = contentValue.getNewMoney();
                }

            } else if (WeChatApplyContentControl.NUMBER.getValue().equals(control)) {
                List<WeChatApprovalDetail.Title> titles = content.getTitles();
                String text = titles.get(0).getText();
                WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                if (car.CAR_COUNT.equals(text)) {
                    contentValueNumber = contentValue.getNumber();
                }
            } else if (WeChatApplyContentControl.TEXTAREA.getValue().equals(control)) {
                List<WeChatApprovalDetail.Title> titles = content.getTitles();
                String text = titles.get(0).getText();
                WeChatApprovalDetail.ContentValue contentValue = content.getContentValue();
                if (car.APPLY_REASON.equals(text)) {
                    thirdRemark = contentValue.getText();
                }
            }
        }
        //根据解析的场景创建具体的场景实体,后期优化,简化代码
        for (String scene : sceneList) {
            ApprovalInfo.TripListBean tripListBean = null;
            if (car.APPLY_USER_CAR.equals(scene)) {//审批用车
                tripListBean = ApprovalInfo.TripListBean.builder()
                        .type(FbTripType.CAR.getCode())
                        .startCityId(departureCityCode)
                        .arrivalCityId(arrivedCityCode)
                        .startTime(departureDate)
                        .endTime(arrivedDate)
                        .estimatedAmount(0)
                        .build();
            }
            tripBeans.add(tripListBean);
        }
        // 设置审批信息
        ApprovalInfo.ApplyBean apply = new ApprovalInfo.ApplyBean();
        apply.setType(type);
        //必须为4
        apply.setFlowType(4);
        apply.setThirdId(approvalInfo.getSpNo());
        if (StringUtils.isBlank(thirdRemark)) {
            apply.setThirdRemark(car.APPLY_USER_CAR);
            apply.setApplyReasonDesc(car.APPLY_USER_CAR);
        } else {
            apply.setThirdRemark(thirdRemark);
            apply.setApplyReasonDesc(thirdRemark);
        }
        apply.setBudget(0);

        processInfo.setApply(apply);

        log.info("创建行程审批单具体行程信息：{}", JsonUtils.toJson(tripBeans));
        if (ApplyType.APPLY_CAR.getCode() == type) {
            List<CarApprovalInfo.CarApplyRule> carApplyRules = buildUseCarRuleList();
            if (!ObjectUtils.isEmpty(contentValueNumber)) {//没有传递，不限制次数
                carApplyRules.add(CarApprovalInfo.CarApplyRule.builder().type("times_limit").value(contentValueNumber).build());
            }
            boolean limitPrice = !(StringUtils.isBlank(newMoney));
            carApplyRules.add(CarApprovalInfo.CarApplyRule.builder().type("price_limit_flag").value(limitPrice ? 2 : 0).build());
            if (limitPrice) {
                carApplyRules.add(CarApprovalInfo.CarApplyRule.builder().type("total_price").value(newMoney).build());
            }
            log.info("审批用车规则信息 {}", JsonUtils.toJson(carApplyRules));
            processInfo.setApplyTaxiRuleInfo(carApplyRules);
        }
        return "success";
    }
}
