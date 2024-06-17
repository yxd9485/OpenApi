package com.fenbeitong.openapi.plugin.qiqi.constant;

/**
 * @ClassName OperationType
 * @Description 操作类型枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/23
 **/
public enum OperationTypeEnum {
    CREATE("create", "新增"),
    UPDATE("update", "修改"),
    DELETE("delete", "删除");

    private String code;
    private String desc;

    OperationTypeEnum(String code, String desc) {
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
