package com.fenbeitong.openapi.plugin.customize.zhiou.constant;

/**
 * @ClassName ZhiouConstant
 * @Description 致欧常量
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/18
 **/
public interface ZhiouConstant {
    /**
     * openapi查询审批单详情url后缀
     */
    String APPLY_DETAIL_URL_SUFFIX = "/openapi/func/apply/common/detail";
    /**
     * 蓝凌审批单通知接口url后缀
     */
    String LANDRAY_APPLY_PUSH_SUFFIX = "/api/fbt/apply/syncMultiApply";
    /**
     * 蓝凌审批单状态更新接口url后缀
     */
    String LANDRAY_APPLY_UPDATE_STATE_SUFFIX = "/api/fbt/apply/syncMultiApplyState";

    /**
     * 获取北森token接口url后缀
     */
    String BEISEN_TOKEN_URLSUFFIX = "/token";

    /**
     * 北森推送考勤接口url后缀（接收出差数据）
     */
    String BEISEN_PUSH_ATTENDANCE_V1 = "/AttendanceOpen/api/v1/Business";

    /**
     * 北森删除考勤接口url后缀（删除推送错误的出差数据）
     */
    String BEISEN_REMOVE_ATTENDANCE_V1 = "/AttendanceOpen/api/v1/Business/Remove";

    /**
     * 北森查询考勤url后缀（根据请求ID查询业务异步接口的任务处理状态）
     */
    String BEISEN_GET_STATE = "/OpenPlatform/api/AsyncApiExecInfo/GetByRequestId";

    /**
     * 重试次数
     */
    int BEISEN_TRY_COUNT = 5;

    /**
     * 北森授权类型
     */
    String BEISEN_GRANT_TYPE = "client_credentials";

    /**
     * 北森异步请求响应头中的X-PAAS-Request-ID
     */
    String X_PAAS_REQUEST_ID = "X-PAAS-Request-ID";

    /**
     * 是否推送北森
     */
    String IS_PUSH_BEISEN = "is_push_beisen";

    /**
     * 是否推送蓝凌
     */
    String IS_PUSH_LANDRAY = "is_push_landray";
}
