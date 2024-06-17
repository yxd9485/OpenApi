package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceListReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceListRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceRespDTO;

/**
 * <p>Title: IFxkApprovalInstanceService</p>
 * <p>Description: 纷享销客审批实例服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 8:14 PM
 */
public interface IFxkApprovalInstanceService {

    /**
     * 获取纷享销客crm审批实例
     *
     * @param req 请求参数
     * @return 纷享销客审批实例
     */
    FxkGetApprovalInstanceRespDTO getInstance(FxkGetApprovalInstanceReqDTO req);

    /**
     * 获取纷享销客审批实例列表
     *
     * @param req 请求参数
     * @return 纷享销客审批实例列表
     */
    FxkGetApprovalInstanceListRespDTO getInstanceList(FxkGetApprovalInstanceListReqDTO req);



}
