package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenOrderApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTaxiApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.ApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单修改信息表单解析
 * @author xiaohai
 * @Date 2022/06/30
 */
@ServiceAspect
@Service
@Slf4j
public class ParseOrderChangeFormServiceImpl implements ParseApplyFormService<String> {

    /**
     *
     * @param approvalDefines ：飞书表单控件数据
     * @param applyDetail ： 审批单详情数据，json字符串
     */
    @Override
    public void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines ,String applyDetail) {
        Map map = JsonUtils.toObj(applyDetail, Map.class);
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            //具体字段名称 例：出发地
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenOrderApplyConstant.CHANGE_TYPE)) {
                value = StringUtils.obj2str(map.get("changeType"));
            }
            if (name.equals(OpenOrderApplyConstant.CHANGE_REASON)) {
                value = StringUtils.obj2str(map.get("changeReason"));
                // 申请详情
                if (map.get("changeDetail") != null) {
                    value = value + map.get("changeDetail").toString();
                }
            }
            if (name.equals(OpenOrderApplyConstant.OLD_ORDER_INFO)) {
                value = StringUtils.obj2str(map.get("oldOrderInfo"));
            }
            if (name.equals(OpenOrderApplyConstant.ORDER_INFO)) {
                value = StringUtils.obj2str(map.get("orderInfo"));
            }
            if (name.equals(OpenOrderApplyConstant.ORDER_CHANGE_FEE)) {
                value = StringUtils.obj2str(map.get("orderChangeFee"));
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
    }

    @Override
    public void afterPropertiesSet() {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.ORDER_CHANGE , this);
    }

}
