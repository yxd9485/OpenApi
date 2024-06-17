package com.fenbeitong.openapi.plugin.beisen.common.constant;

/**
 * <p>Title="Constant</p>
 * <p>Description="</p>
 * <p>Company="www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020_12_02 19:18
 */
public interface BeiSenConstant {
    String token_url_new = "https://openapi.italent.cn/token";
    String org_list_url_new = "https://openapi.italent.cn/TenantBasePublicApiV2/v2/organization/timewindow/search";
    String employee_list_url_new = "https://openapi.italent.cn/TenantBasePublicApiV2/v2/employee/timewindow/search";
    String business_outward_apply_list_url_new = "https://openapi.italent.cn/AttendanceOpen/api/v1/Outward/GetApprovalCompletedOutwardList";
    String business_apply_list_url_new = "https://openapi.italent.cn/AttendanceOpen/api/v1/Business/GetApprovalCompletedBusinessList";
    String business_object_data_url_new = "https://openapi.italent.cn/TenantBasePublicApiV2/v2/setting/page";
    String employee_job_list_url = "https://openapi.italent.cn/TenantBasePublicApiV2/v2/employee/serviceinfo/ids/search";

    /**
     * 每批次数据量
     */
    Integer CAPACITY = 300;

    /**
     * 直接操作类型：1-新增或更新，2-绑定
     */
    Integer RANK_TYPE = 1;

    /**
     * 开始时间
     */
    String START_DATE = "1990-01-01T00:00:00";

    /**
     * 根据时间窗滚动查询变动的组织单元信息
     */
    String BEISEN_RANK_URL_V5 = "https://openapi.italent.cn/TenantBaseExternal/api/v5/Organization/GetByTimeWindow";
    /**
     * 根据时间窗滚动查询变动的员工与单条任职信息
     */
    String BEISEN_EMPLOYEE_ORGANIZATION_V5 = "https://openapi.italent.cn/TenantBaseExternal/api/v5/Employee/GetByTimeWindow";

    /**
     * 根据时间窗滚动查询变动的职级信息
     */
    String BEISEN_JOB_LEVEL_URL_V5 = "https://openapi.italent.cn/TenantBaseExternal/api/v5/JobLevel/GetByTimeWindow";
    /**
     * 根据时间窗滚动查询变动的组织单元信息
     */
    String BEISEN_DEPT_ORGANIZATION_V5 = "https://openapi.italent.cn/TenantBaseExternal/api/v5/Organization/GetByTimeWindow";
    /**
     * 北森响应成功常量
     */
    String BEISEN_RESULT_SUCCESS = "200";

}
