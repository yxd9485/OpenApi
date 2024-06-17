package com.fenbeitong.openapi.plugin.dingtalk.isv.constant;

/**
 * 任务类型
 * @author lizhen
 */
public enum OpenSyncBizDataMediumType {

    /**
     * 钉钉isv open_sync_biz_data_medium
     */
    DINGTALK_ISV_USER(13, "企业用户变更，包含用户添加、修改、删除"),
    DINGTALK_ISV_DEPARTMENT(14, "企业部门变更，包含部门添加、修改、删除"),
    DINGTALK_ISV_COMPANY_STATUS_CHANGE(16, "企业的最新状态"),
    DINGTALK_ISV_PROCESS_BIZ(22, "创建审批单");



    private final Integer key;
    private final String value;

    OpenSyncBizDataMediumType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static OpenSyncBizDataMediumType parse(String key) {
        if (key == null) {
            return null;
        }
        OpenSyncBizDataMediumType[] itemAry = values();
        for (OpenSyncBizDataMediumType item : itemAry) {
            if (item.getKey().equals(key)) {

                return item;
            }
        }
        return null;
    }
}
