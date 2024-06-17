package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.feishu.common.util.FeiShuParseFormUtils;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenMallApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyMultiTripDetailDTO;
import com.fenbeitong.openapi.plugin.support.service.mall.MallInfoUtil;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 非行程信息表单组装
 * @author xiaohai
 * @Date 2022/06/30
 */
@ServiceAspect
@Service
@Slf4j
public class ParseMulitiTripFormServiceImpl implements ParseApplyFormService<String> {

    @Override
    public void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines , String applyDetail) {
        IntranetApplyMultiTripDetailDTO apply = JsonUtils.toObj(applyDetail, IntranetApplyMultiTripDetailDTO.class);
        //申请事由
        String applyReason = apply.getApplyReason();
        if(!StringUtils.isBlank(apply.getApplyReasonDesc())){
            applyReason = applyReason + ";" + apply.getApplyReasonDesc();
        }
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenTripApplyConstant.multiTrip.APPLY_REASON)) {
                value = applyReason;
            }else if (name.equals(OpenTripApplyConstant.multiTrip.APPLY_START_TIME)) {
                value = apply.getStartTime();
            }else if (name.equals(OpenTripApplyConstant.multiTrip.APPLY_END_TIME)) {
                value = apply.getEndTime();
            }else if (name.equals(OpenTripApplyConstant.multiTrip.APPLY_CITY)) {
                List<IntranetApplyMultiTripDetailDTO.MultiTripCity> multiTripCity = apply.getMultiTripCity();
                if(CollectionUtils.isNotBlank(multiTripCity)){
                    value = apply.getMultiTripCity().stream().map(IntranetApplyMultiTripDetailDTO.MultiTripCity::getValue).collect(Collectors.joining("；"));
                }
            }else if (name.equals(OpenTripApplyConstant.multiTrip.APPLY_MULTI_TRIP_SCENE)) {
                value = String.join("," , apply.getMultiTripScene());
            }else if (name.equals(OpenTripApplyConstant.multiTrip.KEY_TRIP_FEE)) {
                BigDecimal estimatedAmountBig = BigDecimalUtils.obj2big(apply.getEstimatedAmount(), null);
                value =  ObjectUtils.isEmpty(estimatedAmountBig) ? null : "¥" + estimatedAmountBig.setScale(2, RoundingMode.HALF_UP).toString();
            }else if (name.equals(OpenTripApplyConstant.multiTrip.APPLY_GUEST_NAME)) {
                value = apply.getGuestList().stream().map(IntranetApplyMultiTripDetailDTO.GuestList::getName).collect(Collectors.joining(","));
            }else if (name.equals(OpenTripApplyConstant.multiTrip.COST_ATTRIBUTION_DEPARTMENT)) {
                List<IntranetApplyMultiTripDetailDTO.CostAttributionList> costAttributionList = apply.getCostAttributionList();
                if(CollectionUtils.isNotBlank(costAttributionList)){
                    for(IntranetApplyMultiTripDetailDTO.CostAttributionList costAttribution : costAttributionList){
                        if( costAttribution.getCostAttributionCategory() == 1){
                            //部门
                            value = costAttribution.getCostAttributionName();
                        }
                    }
                }
            }else if (name.equals(OpenTripApplyConstant.multiTrip.COST_ATTRIBUTION_PROJECT)) {
                List<IntranetApplyMultiTripDetailDTO.CostAttributionList> costAttributionList = apply.getCostAttributionList();
                if(CollectionUtils.isNotBlank(costAttributionList)){
                    for(IntranetApplyMultiTripDetailDTO.CostAttributionList costAttribution : costAttributionList){
                        if( costAttribution.getCostAttributionCategory() == 2){
                            //部门
                            value = costAttribution.getCostAttributionName();
                        }
                    }
                }
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
    }




    @Override
    public void afterPropertiesSet() {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.MULTI_TRIP , this);
    }
}
