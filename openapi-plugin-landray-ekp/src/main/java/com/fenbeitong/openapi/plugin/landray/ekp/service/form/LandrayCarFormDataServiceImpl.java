package com.fenbeitong.openapi.plugin.landray.ekp.service.form;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.LandrayFormDataBuildService;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.CarReasonEnum;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import org.apache.commons.lang.StringUtils;
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
 * 用车信息表单组装
 * @Auther zhang.peng
 * @Date 2021/8/5
 */
@ServiceAspect
@Service
public class LandrayCarFormDataServiceImpl extends LandrayFormDataBuildService {

    @Override
    public MultiValueMap<String,Object> buildFormDataInfo( FenbeitongApproveDto fenbeitongApproveDto , List<OpenEtlMappingConfig> openEtlMappingConfigList  , String fbtApplyId) {
        //用车
        String formValuesJson = "";
        MultiValueMap<String,Object> wholeForm = new LinkedMultiValueMap<>();
        if (CollectionUtils.isEmpty(openEtlMappingConfigList)){
            return wholeForm;
        }
        Map<String,Object> formParams = new HashMap<>();
        List<FenbeitongApproveDto.Trip> tripList = fenbeitongApproveDto.getTripList();
        FenbeitongApproveDto.Trip trip = tripList.get(0);
        openEtlMappingConfigList.stream().forEach(etlMappingConfig -> {
            String srcData = etlMappingConfig.getSrcCol();
            String tgtData = etlMappingConfig.getTgtCol();
            if ("cost_attribution_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getCost_attribution_id());
            }
            if ("currentTime".equals(srcData)){
                formParams.put(tgtData, DateUtils.toSimpleStr(new Date()));
            }
            if ("third_employee_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getThirdEmployeeId());
            }
            if ("apply_reason".equals(srcData)){
                formParams.put(tgtData, Double.valueOf(CarReasonEnum.getCarReasonByValue(fenbeitongApproveDto.getApplyReason()).getType()+""));
            }
            if ("start_city_name_list".equals(srcData)){
                // 检查 list
                formParams.put(tgtData, StringUtils.strip(trip.getStartCityNameList().toString(),"[]"));
            }
            if ("start_time".equals(srcData)){
                formParams.put(tgtData,trip.getStartTime());
            }
            if ("end_time".equals(srcData)){
                formParams.put(tgtData,trip.getEndTime());
            }
            if ("person_count".equals(srcData)){
                String personCount = trip.getPersonCount();
                personCount = "-1".equals(personCount) ? "9999" : personCount;
                formParams.put(tgtData,Double.valueOf(personCount));
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
            if ("apply_cost".equals(srcData)){
                String priceLimit = trip.getPriceLimit();
                priceLimit = "-1".equals(priceLimit) ? "不限制" : priceLimit;
                formParams.put(tgtData,priceLimit);
            }
        });
        formValuesJson = JSONObject.toJSONString(formParams);
        String title = fenbeitongApproveDto.getEmployeeName() + "的分贝通用车审批单";
        wholeForm.add("docSubject", title );
        wholeForm.add("formValues", formValuesJson );
        wholeForm.add("docContent", fenbeitongApproveDto.getApplyDesc() );
        return wholeForm;
    }
}
