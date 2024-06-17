package com.fenbeitong.openapi.plugin.yunzhijia.exception;

/**
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
public interface YunzhijiaErrorCode {

    /**
     * 系统异常
     */
    int ERROR = 500;

    /**
     * 三方系统异常
     */
    int THIRD_SERVICE_ERROR = 1001;

    /**
     * 参数异常
     */
    int PARAM_ERROR = 1002;

    /**
     * 云之家返回结果为空
     */
    int EMPTY_RESULT = 1003;

    /**
     * UC返回结果为空
     */
    int UC_EMPTY_RESULT = 1004;
}
