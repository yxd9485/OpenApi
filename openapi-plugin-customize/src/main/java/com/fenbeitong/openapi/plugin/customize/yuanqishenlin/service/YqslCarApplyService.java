package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;

/**
 * @ClassName YqslCarApplyService
 * @Description 元气森林用车外出申请审批
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/3 上午10:22
 **/
public interface YqslCarApplyService {

    public Object createCarApply(ApiRequestBase request) throws Exception;
}
