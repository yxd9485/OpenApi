package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse;

import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.base.DingtalkFormComponentUtils;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyBaseDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyMultiTripDetailDTO;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.enums.company.CostAttributionTypeEnum;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *<p>Title: IntranetParseMultiTripReverseImpl<p>
 *<p>Description: <p>
 *<p>Company:www.fenbeitong.com<p>
 *
 * @author    liuhong
 * @date 2022/7/5 15:22
 */
@Slf4j
@ServiceAspect
@Service
public class IntranetParseMultiTripReverseImpl implements IIntranetParseReverseService{
    @Override
    public Integer getCallBackType() {
        return ProcessTypeConstant.MULTI_TRIP_REVERSE;
    }

    @Override
    public List<OapiProcessinstanceCreateRequest.FormComponentValueVo> buildProcessReq(IntranetApplyBaseDTO baseDTO) {
        IntranetApplyMultiTripDetailDTO apply = (IntranetApplyMultiTripDetailDTO) baseDTO;
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list = new ArrayList<>();

        //申请事由
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.APPLY_REASON,buildReason(apply),list);
        //开始时间
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.APPLY_START_TIME,apply.getStartTime(),list);
        //结束时间
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.APPLY_END_TIME,apply.getEndTime(),list);
        //使用场景
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.APPLY_MULTI_TRIP_SCENE,buildTripScene(apply),list);
        //出差城市
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.APPLY_CITY,buildApplyCity(apply),list);
        //总预估费用
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.KEY_TRIP_FEE, buildEstimatedAmount(apply), list);
        //出行人
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.APPLY_GUEST_NAME,buildApplyGuest(apply),list);
        //费用归属部门
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.COST_ATTRIBUTION_DEPARTMENT,
            buildCostAttribution(apply,CostAttributionTypeEnum.ORG_UNIT.getKey()),
            list);
        //费用归属项目
        DingtalkFormComponentUtils.addComponent(OpenTripApplyConstant.multiTrip.COST_ATTRIBUTION_PROJECT,
            buildCostAttribution(apply,CostAttributionTypeEnum.PROJECT.getKey()),
            list);
        return list;
    }

    private String buildApplyGuest(IntranetApplyMultiTripDetailDTO apply) {
        if (CollectionUtils.isBlank(apply.getGuestList())){
            return null;
        }
        return apply.getGuestList().stream()
            .map(IntranetApplyMultiTripDetailDTO.GuestList::getName)
            .collect(Collectors.joining(","));
    }

    private String buildReason(IntranetApplyMultiTripDetailDTO apply){
        String applyReason = apply.getApplyReason();
        if(!StringUtils.isBlank(apply.getApplyReasonDesc())){
            applyReason = applyReason + ";" + apply.getApplyReasonDesc();
        }
        return applyReason;
    }

    private String buildTripScene(IntranetApplyMultiTripDetailDTO apply){
        if (CollectionUtils.isBlank(apply.getMultiTripScene())){
            return null;
        }
        return String.join("," , apply.getMultiTripScene());
    }

    private String buildApplyCity(IntranetApplyMultiTripDetailDTO apply) {
        if (CollectionUtils.isBlank(apply.getMultiTripCity())) {
            return null;
        }
        return apply.getMultiTripCity().stream()
            .map(IntranetApplyMultiTripDetailDTO.MultiTripCity::getValue)
            .collect(Collectors.joining("；"));
    }

    private String buildEstimatedAmount(IntranetApplyMultiTripDetailDTO apply){
        BigDecimal estimatedAmountBig = BigDecimalUtils.obj2big(apply.getEstimatedAmount(), null);
        return ObjectUtils.isEmpty(estimatedAmountBig) ? null : "¥" + estimatedAmountBig.setScale(2, RoundingMode.HALF_UP);
    }

    private String buildCostAttribution(IntranetApplyMultiTripDetailDTO apply, int costAttributionCategory) {
        if (CollectionUtils.isBlank(apply.getCostAttributionList())) {
            return null;
        }
        return apply.getCostAttributionList().stream()
            .filter(costAttribution -> Integer.valueOf(costAttributionCategory).equals(costAttribution.getCostAttributionCategory()))
            .findAny().map(IntranetApplyMultiTripDetailDTO.CostAttributionList::getCostAttributionName).orElse(null);
    }

}
