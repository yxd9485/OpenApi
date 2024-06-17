package com.fenbeitong.openapi.plugin.dingtalk.listener;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkCarApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;

import java.util.List;

public interface DingTalkCarListener {

    /**
     * 订单用车审批模板设置
     */
    void filterEiaDingTalk(DingtalkTripApplyProcessInfo.ApplyBean apply,
                           DingtalkTripApplyProcessInfo.TripListBean tripListBean,
                           List<OapiProcessinstanceGetResponse.FormComponentValueVo> formComponentList,
                           List<DingtalkCarApplyProcessInfo.UseCarApplyRule> useCarApplyRules,
                           String companyId,
                           String dingtalkUserId);


}