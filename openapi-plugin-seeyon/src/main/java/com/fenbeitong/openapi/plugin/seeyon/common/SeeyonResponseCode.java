package com.fenbeitong.openapi.plugin.seeyon.common;

public interface SeeyonResponseCode {
    /**
     * seeyon.msg.100=调用云之家成功
     */
    String SEEYON_SUCCESS = "100";
    /**
     * seeyon.msg.170001=企业不存在
     */
    String SEEYON_TOKEN_INFO_IS_NULL = "170001";
    /**
     * seeyon.msg.170002=调用云之家获取access_token失败
     */
    String SEEYON_TOKEN_ERROR = "170002";

    /**
     * seeyon.msg.170003=云之家人员为空
     */
    String SeeyonEMPLOYEE_NULL = "170003";

    /**
     * seeyon.msg.170004=添加云之家人员至分贝通失败
     */
    String SEEYON_EMPLOYEE_ADD_ERROR = "170004";

    /**
     * seeyon.msg.170005=企业未注册
     */
    String SEEYON_CORP_UN_REGIST = "170005";

    /**
     * seeyon.msg.170006=云之家部门为空
     */
    String SEEYON_ORG_NULL = "170006";

    /**
     * seeyon.msg.170007=云之家部门负责人为空
     */
    String SEEYON_ORG_LEADER_NULL = "170007";

    /**
     * seeyon.msg.170008=更新云之家人员至分贝通失败
     */
    String SEEYON_EMPLOYEE_UPDATE_ERROR = "170008";

    /**
     * seeyon.msg.170009=删除云之家人员至分贝通失败
     */
    String SEEYON_EMPLOYEE_DELETE_ERROR = "170009";

    /**
     * seeyon.msg.170010=新增云之家部门至分贝通失败
     */
    String SEEYON_ORG_ADD_ERROR = "170010";
    /**
     * seeyon.msg.170011=更新云之家部门至分贝通失败
     */
    String SEEYON_ORG_UPDATE_ERROR = "170011";
    /**
     * seeyon.msg.170012=删除云之家部门至分贝通失败
     */
    String SEEYON_ORG_DELETE_ERROR = "170012";
    /**
     * seeyon.msg.170013=更新云之家部门负责人至分贝通失败
     */
    String SEEYON_ORG_LEADER_MODIFY_ERROR = "170013";

    /**
     * seeyon.msg.170014=云之家审批基础数据为空
     */
    String SEEYON_APPLY_BASIC_IS_NULL = "170014";

    /**
     * seeyon.msg.170017=云之家审批单详情数据为空
     */
    String SEEYON_APPLY_DETAIL_IS_NULL = "170017";

    /**
     * seeyon.msg.170016=分贝人员不存在
     */
    String SEEYON_FB_EMPLOYEE_IS_NOT_EXIST = "170016";


}
