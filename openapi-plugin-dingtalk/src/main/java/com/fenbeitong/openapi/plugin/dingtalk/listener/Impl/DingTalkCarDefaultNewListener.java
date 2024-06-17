package com.fenbeitong.openapi.plugin.dingtalk.listener.Impl;


import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.finhub.common.utils.NumericUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkCarApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkCarListener;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkCommon;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenOrderApplyConstant;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class DingTalkCarDefaultNewListener extends DingTalkCommon implements DingTalkCarListener {


    @Override
    public void filterEiaDingTalk(DingtalkTripApplyProcessInfo.ApplyBean apply,
                                  DingtalkTripApplyProcessInfo.TripListBean tripListBean,
                                  List<OapiProcessinstanceGetResponse.FormComponentValueVo> formComponentList,
                                  List<DingtalkCarApplyProcessInfo.UseCarApplyRule> useCarApplyRules,
                                  String companyId,
                                  String dingtalkUserId) {
        for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponent : formComponentList) {
            if (StringUtils.isEmpty(formComponent.getName())){
                continue;
            }
            switch (formComponent.getName()) {
                case DingTalkConstant.car.KEY_APPLY_REASON:
                    apply.setApplyReason(ObjectUtils.isEmpty(formComponent.getValue()) || "null".equals(formComponent.getValue()) ? null : formComponent.getValue());
                    break;
                case DingTalkConstant.car.KEY_START_CITY:
                    if (ObjectUtils.isEmpty(getConfig(companyId))) {
                        tripListBean.setStartCityId(addCity(tripListBean.getStartCityId(), getCityId(getSubString(formComponent.getValue()).toString(), companyId, dingtalkUserId, apply)));
                    } else {
                        tripListBean.setStartCityId(OpenOrderApplyConstant.cityCode.beiJing);
                    }
                    break;
                case DingTalkConstant.car.KEY_START_END_TIME:
                    setUseTime(tripListBean, formComponent.getValue());
                    break;
                case DingTalkConstant.car.KEY_TRIP_COUNT:
                    if(!ObjectUtils.isEmpty(formComponent.getValue()) && !"null".equals(formComponent.getValue())){
                        if(!ObjectUtils.isEmpty(useCarApplyRules)){
                            useCarApplyRules.stream().forEach(r->{
                                if("times_limit_flag".equals(r.getType()) && Integer.valueOf(0).equals(r.getValue())){
                                    r.setValue(2);
                                }
                            });
                            useCarApplyRules.add(DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("times_limit").value(formComponent.getValue()).build());
                        }
                    }
                    break;
                case DingTalkConstant.car.KEY_TRIP_FEE:
                    tripListBean.setEstimatedAmount(NumericUtils.obj2int(formComponent.getValue()));
                    break;
                default:
                    break;
            }
        }
    }
}
