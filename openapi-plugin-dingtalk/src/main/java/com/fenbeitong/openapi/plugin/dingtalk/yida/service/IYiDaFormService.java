package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaFormDetailRespDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: YiDaApplyService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 3:39 下午
 */
public interface IYiDaFormService {

    YiDaFormDetailRespDTO getFormDataById(String processInstanceId, String corpId);

    List<String> getAllFormIds(String formUuid, String corpId);

    List<Map<String, Object>> listFormDataByFormId(String formUuid, String corpId);
}
