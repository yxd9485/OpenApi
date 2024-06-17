package com.fenbeitong.openapi.plugin.feishu.common.service.common;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuNoticeResultDto;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;

import java.util.List;
import java.util.Map;

/**
 * 飞书反向推送通用方法
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
public interface FeiShuCommonApplyService {

    Map checkParam(String object);

    FenbeitongApproveDto buildApproveDto(String object);

    FeiShuNoticeResultDto buildNoticeDto(String serviceType,Map map);

    void fillFormData(List<FeiShuApprovalSimpleFormDTO> approvalDefines, FenbeitongApproveDto fenbeitongApproveDto, String serviceType);

}
