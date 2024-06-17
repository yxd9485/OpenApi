package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant;

/**
 * 泛微 restful api
 * @auther zhang.peng
 * @Date 2022/03/04
 */
public interface EcologyRestApi {

    /**
     * 获取分部
     */
    String GET_SUB_COMPANY = "/api/hrm/resful/getHrmsubcompanyWithPage";

    /**
     * 获取部门
     */
    String GET_DEPARTMENT = "/api/hrm/resful/getHrmdepartmentWithPage";

    /**
     * 获取人员
     */
    String GET_USERS = "/api/hrm/resful/getHrmUserInfoWithPage";
}
