package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.FeiShuFormDataBuildService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenMallApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.service.mall.MallInfoUtil;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

/**
 * 采购信息表单组装
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
@ServiceAspect
@Service
public class MallFormDataServiceImpl implements FeiShuFormDataBuildService {

    @Override
    public void buildFormDataInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines, FenbeitongApproveDto fenbeitongApproveDto) {
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {//解析组件详情
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
}
