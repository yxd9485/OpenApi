package com.fenbeitong.openapi.plugin.yunzhijia.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;

import java.util.Map;

/**
 * 云之家表单构建填充
 * @Auther zhang.peng
 * @Date 2021/7/12
 */
public interface IYunzhijiaFormDataBuilderService {

    Map<String,Object> buildForm(FenbeitongApproveDto fenbeitongTripApproveDto);
}
