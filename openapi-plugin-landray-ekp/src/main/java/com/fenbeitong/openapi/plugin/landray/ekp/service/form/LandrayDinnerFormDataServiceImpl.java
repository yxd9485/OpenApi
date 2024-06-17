package com.fenbeitong.openapi.plugin.landray.ekp.service.form;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.LandrayFormDataBuildService;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.DinnerReasonEnum;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.DinnerRuleEnum;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用餐信息表单组装
 * @Auther zhang.peng
 * @Date 2021/8/5
 */
@ServiceAspect
@Service
public class LandrayDinnerFormDataServiceImpl extends LandrayFormDataBuildService {

    public static final String END_TIME_OF_DAY = "24:00";

    @Override
    public MultiValueMap<String,Object> buildFormDataInfo( FenbeitongApproveDto fenbeitongApproveDto , List<OpenEtlMappingConfig> openEtlMappingConfigList , String fbtApplyId) {
        String formValuesJson = "";
        MultiValueMap<String,Object> wholeForm = new LinkedMultiValueMap<>();
        if (CollectionUtils.isEmpty(openEtlMappingConfigList)){
            return wholeForm;
        }
        //用餐
        Map<String,Object> formParams = new HashMap<>();
        List<FenbeitongApproveDto.Trip> tripList = fenbeitongApproveDto.getTripList();
        if (CollectionUtils.isEmpty(tripList)){
            return wholeForm;
        }
        FenbeitongApproveDto.Trip trip = tripList.get(0);
        openEtlMappingConfigList.stream().forEach(etlMappingConfig -> {
            String srcData = etlMappingConfig.getSrcCol();
            String tgtData = etlMappingConfig.getTgtCol();
            if ("third_dept_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getThirdDepartmentId());
            }
            if ("currentTime".equals(srcData)){
                formParams.put(tgtData, DateUtils.toSimpleStr(new Date()));
            }
            if ("apply_name".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getThirdEmployeeId());
            }
            if ("apply_reason".equals(srcData)){
                formParams.put(tgtData, DinnerReasonEnum.getDinnerReasonByValue(fenbeitongApproveDto.getApplyReason()).getType());
            }
            if ("dinner_rule".equals(srcData)){
                formParams.put(tgtData, DinnerRuleEnum.getDinnerRuleSource(Integer.parseInt(trip.getCostType())).getType());
            }
            if ("estimated_amount".equals(srcData)){
                formParams.put(tgtData,trip.getEstimatedAmount());
            }
            if ("use_dinner_time".equals(srcData)){
                formParams.put(tgtData, StringUtils.isBlank(trip.getStartTime()) ? "" : trip.getStartTime().split(" ")[0]);
            }
            if ("start_time".equals(srcData)){
                String startTime = "";
                if ( null != trip && trip.getStartTime().contains(" ") ){
                    startTime = trip.getStartTime().split(" ")[1];
                }
                formParams.put(tgtData,startTime);
            }
            if ("end_time".equals(srcData)){
                String endTime = "";
                if ( null != trip && trip.getEndTime().contains(" ") ){
                    endTime = trip.getEndTime().split(" ")[1];
                    endTime = END_TIME_OF_DAY.equals(endTime) ? "23:59" : endTime;
                }
                formParams.put(tgtData,endTime);
            }
            if ("start_city_name".equals(srcData)){
                formParams.put(tgtData,trip.getStartCityName());
            }
            if ("person_count".equals(srcData)){
                formParams.put(tgtData,trip.getPersonCount());
            }
            if ("apply_reason_desc".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getApplyReasonDesc());
            }
            if ("apply_id".equals(srcData)){
                formParams.put(tgtData,fbtApplyId);
            }
            if ("employee_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getEmployeeId());
            }
        });
        formValuesJson = JSONObject.toJSONString(formParams);
        String title = fenbeitongApproveDto.getApplyName() + "的分贝用餐审批单";
        wholeForm.add("docSubject", title );
        wholeForm.add("formValues", formValuesJson );
        wholeForm.add("docContent", fenbeitongApproveDto.getApplyDesc() );
        return wholeForm;
    }
}
