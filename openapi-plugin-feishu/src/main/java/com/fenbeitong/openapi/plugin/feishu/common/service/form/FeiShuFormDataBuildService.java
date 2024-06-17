package com.fenbeitong.openapi.plugin.feishu.common.service.form;

import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;

import java.util.List;

/**
 * 飞书表单内容构造服务
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
public interface FeiShuFormDataBuildService {

    void buildFormDataInfo(List<FeiShuApprovalSimpleFormDTO> approvalDefines, FenbeitongApproveDto fenbeitongApproveDto);
}
