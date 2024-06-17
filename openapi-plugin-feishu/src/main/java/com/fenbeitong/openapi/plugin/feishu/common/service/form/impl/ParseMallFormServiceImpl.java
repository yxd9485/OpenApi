package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenMallApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.service.mall.MallInfoUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 采购信息表单组装
 * @author xiaohai
 * @Date 2022/06/30
 */
@ServiceAspect
@Service
@Slf4j
public class ParseMallFormServiceImpl implements ParseApplyFormService<String> {

    @Override
    public void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines , String reqData) {
        FenbeitongApproveDto fenbeitongApproveDto = JsonUtils.toObj(reqData, FenbeitongApproveDto.class);
        //解析组件详情
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            String name = approvalDefine.getName();
            String value = "";
            if (name.equals(OpenMallApplyConstant.APPLY_DESC)) {
                value = fenbeitongApproveDto.getApplyDesc();
            }
            if (name.equals(OpenMallApplyConstant.APPLY_TOTAL_PRICE)) {
                // 申请详情
                value = MallInfoUtil.getMallAmount(fenbeitongApproveDto.getApplyTotalPrice());
            }
            if (name.equals(OpenMallApplyConstant.MALL_INFO)) {
                value = MallInfoUtil.buildMallInfo(fenbeitongApproveDto);
            }
            approvalDefine.setName(null);
            approvalDefine.setValue(value);
        }
    }

    @Override
    public void afterPropertiesSet()  {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.MALL , this);
    }
}
