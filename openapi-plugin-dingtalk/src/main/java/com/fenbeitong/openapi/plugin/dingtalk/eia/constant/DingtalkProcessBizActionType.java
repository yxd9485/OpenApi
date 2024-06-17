package com.fenbeitong.openapi.plugin.dingtalk.eia.constant;

/**
 * <p>Title: DingtalkProcessBizActionType</p>
 * <p>Description: 钉钉审批单业务操作类型</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 10:36 AM
 */
public enum DingtalkProcessBizActionType {

    /**
     * 正常审批单
     */
    NONE("NONE"),
    /**
     * 撤销审批单
     */
    REVOKE("REVOKE"),
    /**
     * 修改审批单
     */
    MODIFY("MODIFY"),
    ;
    String value;

    DingtalkProcessBizActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
