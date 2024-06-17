package com.fenbeitong.openapi.plugin.kingdee.common.constant;

/**
 * <p>Title: Constant</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-02 19:18
 */
public interface KingdeeConstant {

    /**
     * 配置类型定义
     */
    interface Bills {
        // 保存
        String SAVE = "save";
        // 提交
        String COMMIT = "commit";
        // 审核
        String AUDIT = "audit";

    }

    interface DataType {
        // map
        int MAP = 1;
        // 集合
        int LIST = 2;
        // 数组
        int ARRAY = 3;
    }

    interface TtlRelationType {
        int SAVE_PARSE = 1;
        int COMMIT_PARSE = 2;
        int AUDIT_PARSE = 3;
        int SET_LIST = 4;
        int SET_MAP = 5;
        int BUSINESS = 6;
    }

    interface Status {
        Integer INIT = 0;
        Integer SUCCESS = 1;
        Integer FAIL = 2;

    }

    interface OpenKingdeeDataRecordStatus {
        Integer INIT = 0;
        Integer SAVE = 1;
        Integer COMMIT = 2;
        Integer AUDIT = 3;

    }

}
