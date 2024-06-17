package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenOrderApplyConstant;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单退订信息表单解析
 * @author xiaohai
 * @Date 2022/06/30
 */
@ServiceAspect
@Service
@Slf4j
public class ParseOrderRefundFormServiceImpl implements ParseApplyFormService<String> {

    /**
     *
     * @param approvalDefines ：飞书表单控件数据
     * @param applyDetail ： 审批单详情数据，json字符串
     */
    @Override
    public void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines ,String applyDetail) {
        Map map = JsonUtils.toObj(applyDetail, Map.class);
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            // 具体字段名称 例：出发地
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenOrderApplyConstant.CHANGE_TYPE)) {
                value = map.get("changeType").toString();
            }
            if (name.equals(OpenOrderApplyConstant.REFUND_REASON)) {
                value = map.get("refundReason").toString();
                // 申请详情
                if (map.get("refundDetail") != null) {
                    value = value + map.get("refundDetail").toString();
                }
            }
            if (name.equals(OpenOrderApplyConstant.OLD_ORDER_INFO)) {
                value = map.get("oldOrderInfo").toString();
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
    }

    @Override
    public void afterPropertiesSet() {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.ORDER_REFUND , this);
    }

}
