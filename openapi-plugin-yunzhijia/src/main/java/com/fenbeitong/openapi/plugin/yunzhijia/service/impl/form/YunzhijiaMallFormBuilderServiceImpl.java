package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.form;

import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.service.mall.MallInfoUtil;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaFormDataBuilderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * 云之家采购表单构建
 * @Auther zhang.peng
 * @Date 2021/7/12
 */
@ServiceAspect
@Service
@Slf4j
public class YunzhijiaMallFormBuilderServiceImpl implements IYunzhijiaFormDataBuilderService {

    @Override
    public Map<String,Object> buildForm(FenbeitongApproveDto fenbeitongApproveDto){
        Map<String,Object> yunzhijiaApplyReqMap = new HashMap<>();
        String applyName = fenbeitongApproveDto.getApplyName();
        yunzhijiaApplyReqMap.put("_S_TITLE", applyName + "的分贝通采购审批单");
        yunzhijiaApplyReqMap.put("Ta_0",fenbeitongApproveDto.getApplyDesc());//采购描述
        yunzhijiaApplyReqMap.put("Ta_1",MallInfoUtil.buildMallInfo(fenbeitongApproveDto));//采购信息
        yunzhijiaApplyReqMap.put("Mo_0", MallInfoUtil.getMallAmount(fenbeitongApproveDto.getApplyTotalPrice()));//采购金额
        return yunzhijiaApplyReqMap;
    }
}
