package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common;

public interface FxkResponseCode {
    /**
     * 纷销客参数错误
     */
    Integer FXK_PARAM_ERROR = 190001;
    /**
     * 纷销客企业ID错误
     */
    Integer FXK_CORP_ID_PARAM_ERROR = 190002;
    /**
     * 纷销客apiname错误
     */
    Integer FXK_API_NAME_ERROR = 190003;
    /**
     * 获取纷销客客户预置对象数据异常
     */
    Integer FXK_PREINSTALL_DATA_ERROR = 190004;
    /**
     * 保存纷销客客户预置对象数据异常
     */
    Integer FXK_SAVE_PRE_INSTALL_DATA_ERROR = 190005;
    /**
     * 企业未注册
     */
    Integer FXK_CORP_UN_REGIST = 190006;

    Integer FXK_EMPLOYEE_NOT_EXISTS = 190007;

    Integer FXK_GET_CORP_ACCESS_TOKEN_FAILED = 190008;
    /**
     * 纷销客参数错误
     */
    Integer FXK_JOB_CONFIG_ERROR = 190009;

}
