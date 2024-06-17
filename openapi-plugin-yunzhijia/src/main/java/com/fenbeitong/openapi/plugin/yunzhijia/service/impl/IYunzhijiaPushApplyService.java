package com.fenbeitong.openapi.plugin.yunzhijia.service.impl;

/**
 * @Auther zhang.peng
 * @Date 2021/4/28
 */
public interface IYunzhijiaPushApplyService {

    boolean pushTripApply(String object);

    boolean pushCarApply(String object);

    boolean pushCommonApply(String object , String serviceType);
}
