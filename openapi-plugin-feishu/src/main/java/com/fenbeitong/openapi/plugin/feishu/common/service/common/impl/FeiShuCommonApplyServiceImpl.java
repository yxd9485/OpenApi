package com.fenbeitong.openapi.plugin.feishu.common.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuNoticeResultDto;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.FeiShuFormDataBuildService;
import com.fenbeitong.openapi.plugin.feishu.common.service.FeiShuFormDataServiceFactory;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.FeiShuCommonApplyService;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.FeiShuNoticeFactoryService;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
@ServiceAspect
@Service
public class FeiShuCommonApplyServiceImpl implements FeiShuCommonApplyService {

    @Autowired
    private FeiShuFormDataServiceFactory feiShuFormDataServiceFactory;

    @Autowired
    private FeiShuNoticeFactoryService feiShuNoticeFactoryService;

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
    public FenbeitongApproveDto buildApproveDto(String object){
        return JsonUtils.toObj(object, FenbeitongApproveDto.class);
    }

    @Override
    public void fillFormData(List<FeiShuApprovalSimpleFormDTO> approvalDefines, FenbeitongApproveDto fenbeitongApproveDto, String serviceType){
        //查询模板详情
        FeiShuFormDataBuildService feiShuFormDataBuildService = feiShuFormDataServiceFactory.getServiceByType(serviceType);
        feiShuFormDataBuildService.buildFormDataInfo(approvalDefines,fenbeitongApproveDto);
    }

    @Override
    public FeiShuNoticeResultDto buildNoticeDto(String serviceType,Map map){
        Map<String, String> noticeResult = feiShuNoticeFactoryService.getNoticeResult(serviceType,map);
        if ( null == noticeResult ){
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(noticeResult),FeiShuNoticeResultDto.class);
    }

}
