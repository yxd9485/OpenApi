package com.fenbeitong.openapi.plugin.customize.wantai.constant;

/**
 * 数据同步类型枚举
 * @author lizhen
 */
public enum DepartmentSyncType {
    /**
     * 新增
     */
    CREATE(1),

    /**
     * 更新
     */
    UPDATE(2),

    /**
     * 删除
     */
    DELETE(3);




    private Integer key;

    private DepartmentSyncType(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }
}
