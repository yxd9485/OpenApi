package com.fenbeitong.openapi.plugin.wechat.eia.process;

import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;

/**
 * Created by dave.hansins on 19/12/16.
 */
public interface IWeChatEiaProcessFormParser {

    ApprovalInfo parse(String companyId, int applyType, String instanceId,
                       WeChatApprovalDetail.WeChatApprovalInfo approvalInfo);
}
