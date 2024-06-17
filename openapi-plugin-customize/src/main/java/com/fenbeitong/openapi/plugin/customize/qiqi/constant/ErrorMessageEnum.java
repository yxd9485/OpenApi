package com.fenbeitong.openapi.plugin.customize.qiqi.constant;
/**
 * @ClassName OpenBudgetCostComparison
 * @Description 错误信息枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/6
 **/
public enum ErrorMessageEnum {
    CREATE_ERR("create_err", "新增失败"),
    UPDATE_ERR("update_err", "修改失败"),
    DELETE_ERR("delete_err", "删除失败");

    private String code;
    private String desc;

    ErrorMessageEnum(String code, String desc) {
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
