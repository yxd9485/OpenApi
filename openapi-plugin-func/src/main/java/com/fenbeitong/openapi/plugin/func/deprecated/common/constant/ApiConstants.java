package com.fenbeitong.openapi.plugin.func.deprecated.common.constant;

/**
 * module: 迁移 open-java <br/>
 * <p>
 * description: 初始化常量<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/11 15:40
 * @since 1.0
 */
public interface ApiConstants {


    String API_TOKEN = "access_token";
    String EMPLOYEE_ID = "employee_id";
    String EMPLOYEE_TYPE = "employee_type";
    String JSON_PARAM = "data";

    String FBT_TOKEN = "token";
    String JSON_RESULT = "data";

    String RESULT_CODE = "code";
    String RESULT_MSG = "msg";
    String RESULT_DATA = "data";

    String APP_ID = "appId";
    String USER_ID = "user_id";
    String COMPANY_ID = "company_id";
    String APP_TYPE = "appType";
    String APP_TYPE_FBT="0";
    String APP_TYPE_THIRD="1";

    String UC_APP_TYPE="ucAppType";
    String UC_APP_TYPE_FBT="1";
    String UC_APP_TYPE_THIRD="2";

    String UC_OPERATOR_INFO ="thirdOperatorInfo";
    String UC_OPERATOR_ID_TYPE="operatorIdType";
    String UC_OPERATOR_ID="operatorId";
    String UC_OPERATOR_SOURCE="source";
    String UC_OPERATOR_SOURCE_OPENAPI="openapi";

    /**
     * 存储数据Data
     */
    String KEY_NAME_DATA = "open_data";
    /**
     * APPID
     */
    String KEY_NAME_APPID = "open_appId";

    /**
     * userId
     */
    String KEY_NAME_USERID = "open_userId";


}
