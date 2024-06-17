package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.form;

import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.service.utils.ApplyUtil;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaFormDataBuilderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * 云之家用餐表单构建
 * @Auther zhang.peng
 * @Date 2021/7/12
 */
@ServiceAspect
@Service
@Slf4j
public class YunzhijiaDinnerFormBuilderServiceImpl implements IYunzhijiaFormDataBuilderService {

    @Override
    public Map<String,Object> buildForm(FenbeitongApproveDto fenbeitongApproveDto){
        Map<String,Object> yunzhijiaApplyReqMap = new HashMap<>();
        String employeeName = fenbeitongApproveDto.getEmployeeName();
        yunzhijiaApplyReqMap.put("_S_TITLE", employeeName + "的分贝通用餐审批单");
        yunzhijiaApplyReqMap.put("Te_0", fenbeitongApproveDto.getApplyReason() + ApplyUtil.getReasonDesc(fenbeitongApproveDto.getApplyReasonDesc()));//申请事由
        yunzhijiaApplyReqMap.put("Te_1",employeeName);//申请人
        yunzhijiaApplyReqMap.put("Te_2", getTripInfo(fenbeitongApproveDto).getEstimatedAmount());//申请用餐金额
        yunzhijiaApplyReqMap.put("Te_3",null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getPersonCount());//用餐人数
        yunzhijiaApplyReqMap.put("Te_4",null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getStartCityName());//用餐城市
        yunzhijiaApplyReqMap.put("Te_5",getTime(getTripInfo(fenbeitongApproveDto).getStartTime()));//用餐开始时间
        yunzhijiaApplyReqMap.put("Te_6",getTime(getTripInfo(fenbeitongApproveDto).getEndTime()));//用餐结束时间
        return yunzhijiaApplyReqMap;
    }



    public String getTime(String applyTime){
        String time = applyTime;
        if ( !StringUtils.isBlank(time) ){
            time = time + ":00";
        }
        return time;
    }

    private FenbeitongApproveDto.Trip getTripInfo(FenbeitongApproveDto fenbeitongApproveDto){
        if ( null == fenbeitongApproveDto || CollectionUtils.isBlank(fenbeitongApproveDto.getTripList())){
            return null;
        }
        return fenbeitongApproveDto.getTripList().get(0);
    }

}
