package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTaxiApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.ApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用车信息表单解析
 * @author xiaohai
 * @Date 2022/06/30
 */
@ServiceAspect
@Service
@Slf4j
public class ParseTaxiInfoFormServiceImpl implements ParseApplyFormService<String> {

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
        commonApplyService.parseFbtTaxiApplyDetail(applyDTO);
        // 解析组件详情
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenTaxiApplyConstant.APPLY_PERSON)) {
                value = applyDTO.getApplyReasonStr() == "null" ? "" : applyDTO.getApplyReasonStr() ;
            }
            if (name.equals(OpenTaxiApplyConstant.APPLY_TAXI_INFO)) {
                value = applyDTO.getTaxiInfoStr();
            }
            if (name.equals(OpenTaxiApplyConstant.RULE_INFO)) {
                value = applyDTO.getRuleInfoStr();
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
    }

    @Override
    public void afterPropertiesSet() {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.TAXI , this);
    }

}
