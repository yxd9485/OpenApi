package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.FanWeiFormDataBuildService;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.weaver.v8.workflow.WorkflowRequestInfo;
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
public class MallFanWeiFormDataServiceImpl implements FanWeiFormDataBuildService {

    @Override
    public WorkflowRequestInfo buildFormDataInfo(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList ) {
        WorkflowRequestInfo workflowRequestInfo = new WorkflowRequestInfo();
        return workflowRequestInfo;
    }

    @Override
    public String getType(){
        return CommonServiceTypeConstant.MALL;
    }
}
