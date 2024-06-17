package com.fenbeitong.openapi.plugin.func.deprecated.common.constant;

import lombok.Getter;

/**
 * module: 迁移open-java<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/6 18:17
 * @since 2.0
 */
@Deprecated
@Getter
public enum GlobalResponseCodeOld {
    /**
     * msg.10009=手机格式错误
     */
    MOBILE_IS_ERROR(10009,"手机格式错误"),
    /**
     * msg.10003=app_key不正确
     */
    AppKeyError(10003,"app_key不正确"),
    /**
     * msg.11005=employee_id/fb_employee_id未传递
     */
    UnEmployeeId(11005,"employee_id/fb_employee_id未传递"),
    /**
     * msg.90003=预算ID不存在
     */
    BUDGET_ID_IS_NOT_EXIST(90003,"预算ID不存在"),
    /**
     * msg.30016=获取城市列表业务类型为空
     */
    CITY_LIST_TYPE_DATA_IS_NULL(30016,"获取城市列表业务类型为空"),
    /**
     * msg.11006=用户不存在
     */
    EmployeeIdNotExist(11006,"用户不存在"),
    /**
     * msg.403=Token失效或者企业ID不存在，请登录
     */
    TOKEN_INFO_IS_ERROR(403,"Token失效或者企业ID不存在，请登录"),
    /**
     * msg.10000=app_id已存在,请勿重复添加
     */
    AppIdAlreadyExist(10000,"app_id已存在,请勿重复添加"),
    /**
     * msg.40006=第三方部门的ID类型为空
     */
    ORG_UNIT_ID_TYPE_DATA_IS_NULL(40006,"第三方部门的ID类型为空"),
    /**
     * msg.40001=部门ID为空
     */
    ORG_UNIT_ID_DATA_IS_NULL(40001,"部门ID为空"),
    /**
     * msg.11008=服务器异常，请稍后重试
     */
    HyperloopServiceError (11008,"服务器异常，请稍后重试"),

    /**
     * msg.30041=请求数据格式错误
     */
    COMMON_JSON_DATA_IS_ERROR(30041,"请求数据格式错误"),
    /**
     * msg.30024=数据格式或参数错误
     */
    COMMON_DATA_TYPE_DATA_IS_ERROR(30024,"数据格式或参数错误"),
    /**
     * 参数未传递
     */
    APP_DATA_IS_NULL(10010, "参数未传递"),

    /**
     * app_key不正确
     */
    APP_KEY_ERROR(10003, "app_key不正确"),


    /**
     * msg.13015=第三方公司员工ID为空
     */
    THIRD_COMPANY_EMPLOYEE_ID_DATA_IS_NULL(13015,"第三方公司员工ID为空"),
    /**
     * 获取token出现异常
     */
    TOKEN_ERROR(100011, "获取token出现异常");

    private final Integer code;

    private final String message;

    GlobalResponseCodeOld(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
