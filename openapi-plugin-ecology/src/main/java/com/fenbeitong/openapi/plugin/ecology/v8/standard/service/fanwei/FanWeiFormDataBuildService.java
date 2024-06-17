package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.weaver.v8.workflow.WorkflowRequestInfo;

import java.util.List;

/**
 * 泛微表单内容构造
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
public interface FanWeiFormDataBuildService {

    WorkflowRequestInfo buildFormDataInfo(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList);

    String getType();
}
