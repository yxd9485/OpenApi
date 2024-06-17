package com.fenbeitong.openapi.plugin.yunzhijia.enums;

public enum YunzhijiaApplyActionType {

    YUNZHIJIA_APPLY_ACTION_TYPE_REACH("reach", "节点到达"),
    YUNZHIJIA_APPLY_ACTION_TYPE_AGREE("agree", "节点同意"),
    YUNZHIJIA_APPLY_ACTION_TYPE_SUBMIT("submit", "节点提交"),
    YUNZHIJIA_APPLY_ACTION_TYPE_DELETE("delete", "单据删除"),
    YUNZHIJIA_APPLY_ACTION_TYPE_WITHDRAW("withdraw", "节点撤回");

    private final String key;
    private final String value;

    YunzhijiaApplyActionType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static YunzhijiaApplyActionType parse(String key) {
        if (key == null) {
            return null;
        }
        YunzhijiaApplyActionType[] itemAry = values();
        for (YunzhijiaApplyActionType item : itemAry) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
