package com.fenbeitong.openapi.plugin.ecology.v8.common;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author lizhen
 * @date 2020/12/8
 */
public interface EcologyConstant {

    /**
     * 泛微根部门
     */
    public static final String ECOLOGY_ROOT_DEPARTMENT_ID = "0";

    /**
     * 用户有效状态。0试用；1正式；2临时；3试用延期；4解聘；5离职；6退休；7无效
     */
    public static List<String> USER_VALID_STATUS = Lists.newArrayList("0", "1", "2", "3");

    /**
     * 泛微部门、分部封存标识
     */
    public static String ORGANIZATION_CANCELED = "1";

    /**
     * 0 主账号  1 子账号
     */
    public Integer ACCOUNTTYPE = 1;

    /**
     * 泛微 rest token
     */
    String SERVER_TOKEN = "ecology_rest_token";

    /**
     * 泛微 rest secret
     */
    String SECRET = "ecology_rest_secret";

    /**
     * 泛微 rest public_key
     */
    String SECRET_PUBLIC_KEY = "ecology_rest_public_key";

    /**
     * 本地客户端RSA私钥
     */
    String LOCAL_PRIVATE_KEY = "ecology_local_private_key";

    /**
     * 本地客户端RSA公钥
     */
    String LOCAL_PUBLIC_KEY = "ecology_local_public_key";

    /**
     * 查询审批人URL
     */
    String GET_SUPER_APPROVE_USER_ID_URL = "/api/workflow/paService/getRequestOperatorInfo";

    /**
     * 退回流程URL
     */
    String REJECT_APPROVE_URL = "/api/workflow/paService/rejectRequest";

    /**
     * 删除流程URL
     */
    String DELETE_APPROVE_URL = "/api/workflow/paService/deleteRequest";
}
