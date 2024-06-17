package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

/**
 * <p>Title: FxkGetApprovalInstanceReqDTO</p>
 * <p>Description: 纷享销客根据审批实例ID获取审批详情请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:04 PM
 */
@Data
public class FxkGetApprovalInstanceReqDTO {

    /**
     * 企业应用访问公司合法性凭证
     */
    private String corpAccessToken;

    /**
     * 开放平台公司账号
     */
    private String corpId;

    /**
     * 当前操作人的openUserId
     */
    private String currentOpenUserId;

    /**
     * 流程实例Id
     */
    private String instanceId;
}
