package com.fenbeitong.openapi.plugin.landray.ekp.service.form;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.landray.ekp.service.LandrayFormDataBuildService;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.constant.OrderTypeEnum;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 超规信息表单组装
 * @Auther zhang.peng
 * @Date 2021/8/5
 */
@ServiceAspect
@Service
public class LandrayOrderFormDataServiceImpl extends LandrayFormDataBuildService {

    @Override
    public MultiValueMap<String,Object> buildFormDataInfo( FenbeitongApproveDto fenbeitongApproveDto , List<OpenEtlMappingConfig> openEtlMappingConfigList , String fbtApplyId) {
        String formValuesJson = "";
        MultiValueMap<String,Object> wholeForm = new LinkedMultiValueMap<>();
        if (CollectionUtils.isEmpty(openEtlMappingConfigList)){
            return wholeForm;
        }
        Map<String,String> formParams = new HashMap<>();
        //超规
        openEtlMappingConfigList.stream().forEach(etlMappingConfig -> {
            String srcData = etlMappingConfig.getSrcCol();
            String tgtData = etlMappingConfig.getTgtCol();
            if ("third_dept_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getThirdDepartmentId());
            }
            if ("apply_time".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getApplyTime());
            }
            if ("third_employee_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getThirdEmployeeId());
            }
            if ("apply_type".equals(srcData)){
                String type = "";
                FenbeitongApproveDto.Train train = fenbeitongApproveDto.getTrain();
                FenbeitongApproveDto.Air air = fenbeitongApproveDto.getAir();
                FenbeitongApproveDto.Hotel hotel = fenbeitongApproveDto.getHotel();
                List<FenbeitongApproveDto.IntelAir> intelAirs = fenbeitongApproveDto.getIntlAir();
                if ( null != train ){
                    type = OrderTypeEnum.TRAIN_ORDER.getType() + "";
                }
                if ( null != air || !CollectionUtils.isEmpty(intelAirs)){
                    type = OrderTypeEnum.AIR_ORDER.getType() + "";
                }
                if ( null != hotel ){
                    type = OrderTypeEnum.HOTEL_ORDER.getType() + "";
                }
                formParams.put(tgtData,type);
            }
            if ("apply_price".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getOrderPrice());
            }
            if ("exceed_buy_desc_content".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getApplyReasonDesc());
            }
            if ("order_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getOrderId());
            }
            if ("apply_id".equals(srcData)){
                formParams.put(tgtData,fbtApplyId);
            }
            if ("employee_id".equals(srcData)){
                formParams.put(tgtData,fenbeitongApproveDto.getEmployeeId());
            }
        });
        formValuesJson = JSONObject.toJSONString(formParams);
        String title = fenbeitongApproveDto.getOrderPerson() + "的分贝通超规审批单";
        wholeForm.add("docSubject", title );
        wholeForm.add("formValues", formValuesJson );
        wholeForm.add("docContent", fenbeitongApproveDto.getApplyDesc() );
        return wholeForm;
    }
}
