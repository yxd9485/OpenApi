package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.fanwei;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiCreateApprovalInstanceReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiNoticeResultDto;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.EcologyFanWeiCommonApplyService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.FanWeiFormDataBuildService;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustomReimbursementDto;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.weaver.v8.workflow.WorkflowRequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@ServiceAspect
@Service
public class FanWeiCommonApplyServiceImpl implements EcologyFanWeiCommonApplyService {

    @Autowired
    private FanWeiFormDataServiceFactory fanWeiFormDataServiceFactory;

    @Autowired
    private FanWeiNoticeFactoryService fanWeiNoticeFactoryService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public Map checkParam(String object) {
        //接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        return map;
    }

    @Override
    public FenbeitongApproveDto buildApproveDto(String object , String serviceType){
        if (CommonServiceTypeConstant.REIMBURSE.equals(serviceType)){
            return JsonUtils.toObj(object, CustomReimbursementDto.class);
        } else {
            return JsonUtils.toObj(object, FenbeitongApproveDto.class);
        }
    }

    @Override
    public WorkflowRequestInfo fillFormData(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto, String serviceType , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList){
        //查询模板详情
        FanWeiFormDataBuildService fanWeiFormDataBuildService = fanWeiFormDataServiceFactory.getServiceByType(serviceType);
        return fanWeiFormDataBuildService.buildFormDataInfo(fanWeiFormData,fenbeitongApproveDto,fbtApproveId,mappingConfigList);
    }

    @Override
    public FanWeiNoticeResultDto buildNoticeDto(String serviceType, Map map){
        Map<String, String> noticeResult = fanWeiNoticeFactoryService.getNoticeResult(serviceType,map);
        if ( null == noticeResult ){
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(noticeResult), FanWeiNoticeResultDto.class);
    }

    @Override
    public boolean saveFbtOrderApplyInfo(List<FanWeiFormData> approvalDefines, FanWeiNoticeResultDto noticeResultDto,String requestId){
        FanWeiCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FanWeiCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(noticeResultDto.getThirdProcessCode());
        feiShuCreateInstanceReqDTO.setUserId(noticeResultDto.getThirdEmployeeId());
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = requestId;
        // 存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(noticeResultDto.getCompanyId(),noticeResultDto.getThirdEmployeeId(), noticeResultDto.getApplyId(), approvalInstance, OpenType.FANWEI.getType());
    }

}
