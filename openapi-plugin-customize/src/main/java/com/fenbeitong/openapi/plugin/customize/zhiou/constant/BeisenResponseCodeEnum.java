package com.fenbeitong.openapi.plugin.customize.zhiou.constant;
/**
 * @ClassName BeisenResponseCode
 * @Description 北森返回码枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/31
 **/
public enum BeisenResponseCodeEnum {
    BEISEN_STATE_SUCCESS("AllSuccess", "成功"),
    BEISEN_STATE_RUNNING("Running", "执行中"),
    BEISEN_STATE_FAIL("Fail", "失败");

    private String code;
    private String desc;

    BeisenResponseCodeEnum(String code, String desc) {
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
