package com.fenbeitong.openapi.plugin.func.common;

/**
 * <p>Title: FuncResponseCode</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/21 5:49 PM
 */
public interface FuncResponseCode {

    /**
     * msg.403=Token失效或者企业ID不存在，请登录
     */
    String TOKEN_INFO_IS_ERROR = "403";

    /**
     * msg.11001=sign签名不正确
     */
    String SIGN_ERROR = "11001";

    /**
     * msg.11002=sign签名不正确 dev及test使用
     */
    String SIGN_ERROR_TIP = "11002";

    /**
     * msg.11009=timestamp失效
     */
    String TIMESTAMP_NO_EFFECT = "11009";

    /**
     * msg.110010=查询第三方人员失败
     */
    String QUERY_THIRD_USER_ERROR = "110010";

    /**
     * msg.110011=查询第三方公司万能配置失败
     */
    String QUERY_THIRD_ALTMAN_CONFIG_ERROR = "110011";

    /**
     * msg.110800=登录失败，请检查配置或者使用管理员账号登录
     */
    String FBT_WEB_LOGIN_ERROR = "110800";

    /**
     * msg.110800=登录失败，请联系管理员处理
     */
    String FBT_WEB_APP_LOGIN_ERROR = "110801";

    /**
     * msg.110810=请求内部http接口异常
     */
    String OPANAPI_HTTP_ERROR = "110810";

    /**
     * msg.110810=请求内部http接口返回数据异常
     */
    String OPANAPI_HTTP_DATA_ERROR = "110820";

    /**
     * msg.110830=请求接口内部异常
     */
    String OPANAPI_HTTP_INTERAL_ERROR = "110830";

    /**
     * msg.110831=同步组织机构人员失败
     */
    int OPANAPI_SYNC_ORG_EMPLOYEE_ERROR = 110831;

    /**
     * msg.110832=查询酒店数据失败，请检查公司查询数据
     */
    int OPANAPI_HOTEL_COMPANY_ERROR = 110832;

    /**
     * msg.110833=同步组织机构人员长度失败
     */
    int OPANAPI_SYNC_ORG_EMPLOYEE_FIELD_LENGTH_ERROR = 110833;
}
