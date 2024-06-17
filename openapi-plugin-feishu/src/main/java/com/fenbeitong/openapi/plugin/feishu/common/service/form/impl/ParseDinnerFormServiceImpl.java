package com.fenbeitong.openapi.plugin.feishu.common.service.form.impl;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenDinnerApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
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
public class ParseDinnerFormServiceImpl implements ParseApplyFormService<String> {

    @Override
    public void parseFormInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines , String reqData) {
        FenbeitongApproveDto fenbeitongApproveDto = JsonUtils.toObj(reqData, FenbeitongApproveDto.class);
        //解析组件详情
        for (FeiShuApprovalSimpleFormDTO approvalDefine : approvalDefines) {
            String name = approvalDefine.getName();
            String value = "";
            if (OpenDinnerApplyConstant.APPLY_PERSON.equals(name)){
                value = fenbeitongApproveDto.getApplyReason() + (StringUtils.isBlank(fenbeitongApproveDto.getApplyDesc()) ? "" : fenbeitongApproveDto.getApplyDesc());
            }
            if (OpenDinnerApplyConstant.ORDER_PERSON.equals(name)){
                value = fenbeitongApproveDto.getApplyName();
            }
            if (OpenDinnerApplyConstant.DINNER_PRICE.equals(name)){
                value = null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getEstimatedAmount();
            }
            if (OpenDinnerApplyConstant.person_number.equals(name)){
                value = null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getPersonCount();
            }
            if (OpenDinnerApplyConstant.CITY.equals(name)){
                value = null == getTripInfo(fenbeitongApproveDto) ? "" : getTripInfo(fenbeitongApproveDto).getStartCityName();
            }
            if (OpenDinnerApplyConstant.START_IME.equals(name)){
                String startTime = getTripInfo(fenbeitongApproveDto).getStartTime();
                if ( !StringUtils.isBlank(startTime) ){
                    startTime = startTime + ":00";
                }
                value = StringUtils.isBlank(startTime) ? "" : DateUtils.toStr(DateUtils.toDate(startTime),"yyyy-MM-dd HH:mm:ss");
            }
            if (OpenDinnerApplyConstant.EEND_TIME.equals(name)){
                String endTime = getTripInfo(fenbeitongApproveDto).getEndTime();
                if ( !StringUtils.isBlank(endTime) && endTime.contains(" ") ){
                    String[] times = endTime.split(" ");
                    endTime = "24:00".equals(times[1]) ? (times[0] + " 23:59:59") : (endTime + ":00");
                }
                value = StringUtils.isBlank(endTime) ? "" : DateUtils.toStr(DateUtils.toDate(endTime),"yyyy-MM-dd HH:mm:ss");
            }
            approvalDefine.setValue(value);
        }
    }

    private FenbeitongApproveDto.Trip getTripInfo(FenbeitongApproveDto fenbeitongApproveDto){
        if ( null == fenbeitongApproveDto || CollectionUtils.isBlank(fenbeitongApproveDto.getTripList())){
            return null;
        }
        return fenbeitongApproveDto.getTripList().get(0);
    }

    @Override
    public void afterPropertiesSet() {
        ApplyFormFactory.registerHandler(FeiShuServiceTypeConstant.DINNER , this);
    }
}
