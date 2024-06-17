package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant;

import lombok.Getter;

/**
 * @author ctl
 * @date 2021/11/16
 */
@Getter
@SuppressWarnings("all")
public enum QueryParamEnum {

    USER_PARAM("userParam", "人员查询条件"),
    DEPARTMENT_PARAM("departmentParam", "部门查询条件"),
    SUBCOMPANY_PARAM("subCompanyParam", "分部查询条件"),
    ;

    private String type;
    private String desc;

    QueryParamEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
