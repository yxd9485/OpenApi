package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.ApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 差旅信息表单解析
 * @author xiaohai
 * @Date 2022/06/30
 */
@ServiceAspect
@Service
@Slf4j
public class  ParseTripInfoFormServiceImpl implements ParseApplyFormService<String> {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    /**
     *
     * @param approvalDefines ：飞书表单控件数据
     * @param applyDetail ： 审批单详情数据，json字符串
     */
    @Override
    public void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines ,String applyDetail) {
        ApplyDTO applyDTO = JsonUtils.toObj(applyDetail, ApplyDTO.class);
        commonApplyService.parseFbtTripApplyDetail(applyDTO);
        // 解析组件详情
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenTripApplyConstant.APPLY_PERSON)) {
                value = applyDTO.getApplyReasonStr() == "null" ? "" : applyDTO.getApplyReasonStr();
            }
            if (name.equals(OpenTripApplyConstant.TRIP_LIST)) {
                value = applyDTO.getTripListStr();
            }
            if (name.equals(OpenTripApplyConstant.GUEST_LIST)) {
                value = applyDTO.getGuestListStr();
            }
            if (name.equals(OpenTripApplyConstant.TRAVEL_TIME)) {
                value = applyDTO.getTravelTimeStr();
            }
            if (name.equals(OpenTripApplyConstant.WHERE_IS)) {
                value = applyDTO.getCost_attribution_name();
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
    }

    @Override
    public void afterPropertiesSet()  {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.TRIP , this);
    }

}
