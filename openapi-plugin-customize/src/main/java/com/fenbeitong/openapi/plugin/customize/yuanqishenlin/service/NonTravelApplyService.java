package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service;

import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;

/**
 * @ClassName NonTravelApplyService
 * @Description 元气森林差旅审批
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/1 上午11:58
 **/
public interface NonTravelApplyService {
    /*
    * 创建非行程差旅审批单
     **/

    public FuncResultEntity createTripApply(ApiRequestBase request) throws Exception;
}
