package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.form;

import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.service.utils.ApplyUtil;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaFormDataBuilderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * 云之家外卖表单构建
 * @Auther zhang.peng
 * @Date 2021/7/12
 */
@ServiceAspect
@Service
@Slf4j
public class YunzhijiaTakeOutFormBuilderServiceImpl implements IYunzhijiaFormDataBuilderService {

    @Override
    public Map<String,Object> buildForm(FenbeitongApproveDto fenbeitongApproveDto){
        Map<String,Object> yunzhijiaApplyReqMap = new HashMap<>();
        String employeeName = fenbeitongApproveDto.getEmployeeName();
        yunzhijiaApplyReqMap.put("_S_TITLE", employeeName + "的分贝通外卖审批单");
        yunzhijiaApplyReqMap.put("Te_0", ApplyUtil.getReasonDesc(fenbeitongApproveDto.getApplyDesc()));//申请事由
//        yunzhijiaApplyReqMap.put("Te_4", );//申请外卖费用
//        yunzhijiaApplyReqMap.put("Te_3", );//送餐时间
//        yunzhijiaApplyReqMap.put("Te_2", );//送餐时段
//        yunzhijiaApplyReqMap.put("Te_1", );//送餐地址
        return yunzhijiaApplyReqMap;
    }
}
