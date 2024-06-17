package com.fenbeitong.openapi.plugin.customize.wantai.constant;

/**
 * 档案任务状态枚举
 * @author lizhen
 */
public enum ArchiveTaskStatusType {
    /**
     * 初始化
     */
    INIT(0),

    /**
     * 归档中
     */
    FILING(10),

    /**
     * 归档完成
     */
    FILING_COMPLETE(20),

    /**
     * 处理数据中
     */
    PROCESS(30),

    /**
     * 处理完成
     */
    PROCESS_COMPLETE(40),

    /**
     * 失败
     */
    ERROR(99);




    private Integer key;

    private ArchiveTaskStatusType(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }
}
