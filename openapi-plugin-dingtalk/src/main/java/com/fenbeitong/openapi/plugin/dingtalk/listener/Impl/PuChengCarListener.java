package com.fenbeitong.openapi.plugin.dingtalk.listener.Impl;


import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.finhub.common.utils.NumericUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkCarApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkCarListener;
import com.fenbeitong.openapi.plugin.dingtalk.listener.DingTalkCommon;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class PuChengCarListener extends DingTalkCommon implements DingTalkCarListener {


    @Autowired
    OpenMsgSetupDao openMsgSetupDao;


    @Override
    public void filterEiaDingTalk(DingtalkTripApplyProcessInfo.ApplyBean apply,
                                  DingtalkTripApplyProcessInfo.TripListBean tripListBean,
                                  List<OapiProcessinstanceGetResponse.FormComponentValueVo> formComponentList,
                                  List<DingtalkCarApplyProcessInfo.UseCarApplyRule> useCarApplyRules,
                                  String companyId,
                                  String dingtalkUserId) {

        List<OpenMsgSetup> companyDingtalkCarApplyChangeList = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_dingtalk_car_apply_change"));

        String applyReason = "";
        OpenMsgSetup openMsgSetup = companyDingtalkCarApplyChangeList.get(0);
        String strVal1 = openMsgSetup.getStrVal1();
        String strVal2 = openMsgSetup.getStrVal2();
        for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponent : formComponentList) {
            if (StringUtils.isEmpty(formComponent.getName())){
                continue;
            }
            switch (formComponent.getName()) {
                case DingTalkConstant.car.KEY_APPLY_REASON:
                    //查询申请理由
                    applyReason = ObjectUtils.isEmpty(formComponent.getValue()) || "null".equals(formComponent.getValue()) ? null : formComponent.getValue();
                    apply.setApplyReason(applyReason);
                    break;
                case DingTalkConstant.car.KEY_START_CITY:
                    tripListBean.setStartCityId(getCityId(formComponent.getValue(), companyId, dingtalkUserId, apply));
                    break;
                case DingTalkConstant.car.KEY_START_END_TIME:
                    setUseTime(tripListBean, formComponent.getValue());
                    break;
                case DingTalkConstant.car.KEY_TRIP_FEE:
                    tripListBean.setEstimatedAmount(NumericUtils.obj2int(formComponent.getValue()));
                    break;
                default:
                    break;
            }
        }
        //用车次数根据用车的使用场景进行设置，根据不同公司的配置进行数据查询填充
        int limit = 0;
        if (strVal1.contains(applyReason)) {//目前只有两种类型，后续可以扩展成三种，如果类型超过三种则无法匹配
            limit = openMsgSetup.getIntVal1();
        } else if (strVal2.contains(applyReason)) {
            limit = openMsgSetup.getIntVal2();
        } else {
            limit = ObjectUtils.isEmpty(openMsgSetup.getIntVal3()) ? limit : openMsgSetup.getIntVal3();
        }
        DingtalkCarApplyProcessInfo.UseCarApplyRule timesLimit = DingtalkCarApplyProcessInfo.UseCarApplyRule.builder().type("times_limit").value(limit).build();
        useCarApplyRules.add(timesLimit);
    }

}
