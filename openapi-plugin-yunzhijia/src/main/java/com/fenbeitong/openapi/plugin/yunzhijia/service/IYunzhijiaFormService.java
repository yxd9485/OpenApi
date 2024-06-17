package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.dto.CommonNoticeResultDto;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyRespDTO;

import java.util.Map;

/**
 * 云之家表单填充和创建
 * @Auther zhang.peng
 * @Date 2021/7/12
 */
public interface IYunzhijiaFormService {

    Map<String,Object> fillFormData(String type, FenbeitongApproveDto fenbeitongApproveDto);

    YunzhijiaApplyRespDTO createApply(YunzhijiaAccessTokenReqDTO build, Map<String, Object> yunzhijiaApplyReqMap, CommonNoticeResultDto noticeResultDto);

}
