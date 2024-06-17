package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiNoticeResultDto;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.weaver.v8.workflow.WorkflowRequestInfo;

import java.util.List;
import java.util.Map;

/**
 * 泛微反向推送通用方法
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
public interface EcologyFanWeiCommonApplyService {

    Map checkParam(String object);

    FenbeitongApproveDto buildApproveDto(String object , String serviceType);

    FanWeiNoticeResultDto buildNoticeDto(String serviceType, Map map);

    WorkflowRequestInfo fillFormData(FanWeiFormData approvalDefines, FenbeitongApproveDto fenbeitongApproveDto, String serviceType , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList);

    boolean saveFbtOrderApplyInfo(List<FanWeiFormData> approvalDefines, FanWeiNoticeResultDto noticeResultDto , String requestId);

}
