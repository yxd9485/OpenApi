package com.fenbeitong.openapi.plugin.qiqi.constant;

/**
 * @ClassName QiqiObjectTypeEnum
 * @Description 目标对象枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/19
 **/
public enum ObjectTypeEnum {
    DEPARTMENT("Department", "部门"),
    USER("User", "人员"),
    RANK("Rank", "值集"),
    PROJECT_CATEGORY("ProjectCategory", "项目分组"),
    PROJECT("Project", "项目"),
    BUDGET_ACCOUNT("BudgetAccount", "自定义档案"),
    COST("Cost", "费用类别"),
    OTHER_AP_PAYMENT("OtherApPayment", "其他应付单");

    private String code;
    private String desc;

    ObjectTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
