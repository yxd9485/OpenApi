package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaApplyDetailRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;

import java.util.Map;

/**
 * <p>Title: YiDaApplyService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 3:39 下午
 */
public interface IYiDaApplyService {

    YiDaApplyDetailRespDTO getInstanceById(String processInstanceId, String corpId);

    Map<String, Object> tripApplyScript(String companyId, Map<String, Object> applyData);

    CommonApplyReqDTO parseYiDaTripApprovalForm(String companyId, YiDaApplyDetailRespDTO yiDaApplyDetailRespDTO);
}
