package com.fenbeitong.openapi.plugin.func.apply.constant;

/**
 * <p>Title: CustformApplyType<p>
 * <p>Description: 自定义申请单请求类型枚举<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/2/14 2:54 PM
 */
public enum CustformApplyType {
    /**
     * 自定义申请单详情查询默认类型，返回信息全面
     */
    App(1,"app"),

    Web(2,"web");

    private final int key;
    private final String value;
    CustformApplyType(int key, String value) {
        this.key = key;
        this.value = value;
    }
    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
