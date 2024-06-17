package com.fenbeitong.openapi.plugin.feishu.common.listener;

import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;

public interface FeiShuApprovalListener {

    /**
     * 用车审批
     */
    CommonApplyReqDTO parseFeiShuCarForm(String companyId,String corpId, String approvalId, String form, String thirdEmployeeId);

    /**
     * 差旅审批
     */
    CommonApplyReqDTO parseFeiShuBusinessForm(String companyId,String corpId, String approvalId, String form);
}
