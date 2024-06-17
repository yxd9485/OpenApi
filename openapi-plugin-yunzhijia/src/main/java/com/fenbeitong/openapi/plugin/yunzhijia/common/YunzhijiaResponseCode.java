package com.fenbeitong.openapi.plugin.yunzhijia.common;

public interface YunzhijiaResponseCode {
    /**
     * yunzhijia.msg.100=调用云之家成功
     */
    String YUNZHIJIA_SUCCESS = "100";
    /**
     * yunzhijia.msg.150001=企业不存在
     */
    String YUNZHIJIA_TOKEN_INFO_IS_NULL = "150001";
    /**
     * yunzhijia.msg.150002=调用云之家获取access_token失败
     */
    String YUNZHIJIA_TOKEN_ERROR = "150002";

    /**
     * yunzhijia.msg.150003=云之家人员为空
     */
    String YUNZHIJIA_EMPLOYEE_NULL = "150003";

    /**
     * yunzhijia.msg.150004=添加云之家人员至分贝通失败
     */
    String YUNZHIJIA_EMPLOYEE_ADD_ERROR = "150004";

    /**
     * yunzhijia.msg.150005=企业未注册
     */
    String YUNZHIJIA_CORP_UN_REGIST = "150005";

    /**
     * yunzhijia.msg.150006=云之家部门为空
     */
    String YUNZHIJIA_ORG_NULL = "150006";

    /**
     * yunzhijia.msg.150007=云之家部门负责人为空
     */
    String YUNZHIJIA_ORG_LEADER_NULL = "150007";

    /**
     * yunzhijia.msg.150008=更新云之家人员至分贝通失败
     */
    String YUNZHIJIA_EMPLOYEE_UPDATE_ERROR = "150008";

    /**
     * yunzhijia.msg.150009=删除云之家人员至分贝通失败
     */
    String YUNZHIJIA_EMPLOYEE_DELETE_ERROR = "150009";

    /**
     * yunzhijia.msg.150010=新增云之家部门至分贝通失败
     */
    String YUNZHIJIA_ORG_ADD_ERROR = "150010";
    /**
     * yunzhijia.msg.150011=更新云之家部门至分贝通失败
     */
    String YUNZHIJIA_ORG_UPDATE_ERROR = "150011";
    /**
     * yunzhijia.msg.150012=删除云之家部门至分贝通失败
     */
    String YUNZHIJIA_ORG_DELETE_ERROR = "150012";
    /**
     * yunzhijia.msg.150013=更新云之家部门负责人至分贝通失败
     */
    String YUNZHIJIA_ORG_LEADER_MODIFY_ERROR = "150013";

    /**
     * yunzhijia.msg.150014=云之家审批基础数据为空
     */
    String YUNZHIJIA_APPLY_BASIC_IS_NULL = "150014";

    /**
     * yunzhijia.msg.150015=云之家审批单详情数据为空
     */
    String YUNZHIJIA_APPLY_DETAIL_IS_NULL = "150015";

    /**
     * yunzhijia.msg.150016=分贝人员不存在
     */
    String YUNZHIJIA_FB_EMPLOYEE_IS_NOT_EXIST = "150016";

    /**
     * 免登失败
     */
    int YUNZHIJIA_LOGIN_FAILED = 150018;

    /**
     * 参数为空
     */
    String PARAM_ERROR = "-9999";
}
