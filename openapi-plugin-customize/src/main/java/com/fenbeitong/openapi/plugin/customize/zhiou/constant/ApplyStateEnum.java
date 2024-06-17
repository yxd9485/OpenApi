package com.fenbeitong.openapi.plugin.customize.zhiou.constant;
/**
 * @ClassName ApplyStateEnum
 * @Description 申请单状态枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/31
 **/
public enum ApplyStateEnum {
    APPLY_PASS(4, "通过"),
    APPLY_CHANGE(7, "变更"),
    APPLY_INVALID(8,"作废");

    private Integer code;
    private String desc;

    ApplyStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
