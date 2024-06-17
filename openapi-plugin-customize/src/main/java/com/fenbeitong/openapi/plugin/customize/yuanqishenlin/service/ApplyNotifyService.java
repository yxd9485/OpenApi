package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;

import java.io.IOException;

/**
 * @ClassName YqslApplyNotifyService
 * @Description 审批单同意或拒绝
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/13 上午11:57
 **/

public interface ApplyNotifyService {

    void applyNotifyAgree(ApiRequestBase request) throws IOException;

    void applyNotifyRepulse(ApiRequestBase request) throws IOException;
}
