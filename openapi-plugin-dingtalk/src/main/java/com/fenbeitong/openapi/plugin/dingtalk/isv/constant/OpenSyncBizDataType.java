package com.fenbeitong.openapi.plugin.dingtalk.isv.constant;

/**
 * 任务类型
 * @author lizhen
 */
public enum OpenSyncBizDataType {

    /**
     * 钉钉isv open_sync_biz_data
     */
    DINGTALK_ISV_SUITE_TICKET(2, "套件票据"),
    DINGTALK_ISV_CHANGE_AUTH(4, "企业授权变更，包含授权、解除授权、授权变更"),
    DINGTALK_ISV_CHANGE_STATUS(7, "企业微应用变更，包含停用、启用、删除(删除保留授权)"),
    DINGTALK_ISV_ORDER(17, "订单信息"),
    DINGTALK_ISV_TRY_OUT(63, "试用");

    private final Integer key;
    private final String value;

    OpenSyncBizDataType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static OpenSyncBizDataType parse(String key) {
        if (key == null) {
            return null;
        }
        OpenSyncBizDataType[] itemAry = values();
        for (OpenSyncBizDataType item : itemAry) {
            if (item.getKey().equals(key)) {

                return item;
            }
        }
        return null;
    }
}
