package com.fenbeitong.openapi.plugin.feishu.common.constant;

/**
 * 常量
 */
public interface FeiShuConstant {

    /**
     * 表示这是一个验证请求
     */
    String CALLBACK_TYPE_VERIFYCATION = "url_verification";

    /**
     * 事件回调
     */
    String CALLBACK_TYPE_EVENT = "event_callback";

    /**
     * 首次开通应用
     */
    String APP_OPEN_EVENT = "app_open";

    /**
     * 应用启动、停用事件
     * status分别为 start_by_tenant: 租户启用; stop_by_tenant: 租户停用; stop_by_platform: 平台停用
     */
    String APP_STATUS_CHANGE_EVENT = "app_status_change";

    /**
     * 添加人员
     */
    String USER_ADD_EVENT = "user_add";

    /**
     * 更新人员
     */
    String USER_UPDATE_EVENT = "user_update";

    /**
     * 删除人员
     */
    String USER_LEAVE_EVENT = "user_leave";

    /**
     * 添加部门
     */
    String DEPT_ADD_EVENT = "dept_add";

    /**
     * 更新部门
     */
    String DEPT_UPDATE_EVENT = "dept_update";

    /**
     * 删除部门
     */
    String DEPT_DELETE_EVENT = "dept_delete";
    /**
     * 审批单事件(创建，修改，撤销)
     */
    String APPROVAL_CREATE_EVENT = "trip_approval";
    String APPROVAL_REVERT_EVENT = "trip_approval_revert";
    String APPROVAL_INSTANCE_EVENT = "approval_instance";
    String APPROVAL_INSTANCE_STATUS_APPROVED = "APPROVED";
    String APPROVAL_INSTANCE_STATUS_REVERTED = "REVERTED";
    /**
     * 审批拒绝
     */
    String APPROVAL_INSTANCE_STATUS_REJECTED = "REJECTED";

    /**
     * 用车审批事件
     */
    String APPROVAL_CAR_CREATE_EVENT = "approval_instance";

    /**
     * 审批单事件(创建)
     */
    String APPROVAL_EVENT_CREATE = "feishu_eia_approval_create";

    /**
     * 审批单事件(撤销)
     */
    String APPROVAL_EVENT_REVERTED = "feishu_eia_approval_reverted";
    /**
     * APP_TICKET
     */
    String APP_TICKET_EVENT = "app_ticket";

    /**
     * start_by_tenant: 租户启用;
     */
    String STATUS_START_BY_TENANT = "start_by_tenant";

    /**
     * stop_by_tenant: 租户停用;
     */
    String STATUS_STOP_BY_TENANT = "stop_by_tenant";

    /**
     * stop_by_platform: 平台停用
     */
    String STATUS_STOP_BY_PLATFORM = "stop_by_platform";

    /**
     * contact_scope_change: 变更权限范围,可见范围
     */
    String CONTACT_SCOPE_CHANGE = "contact_scope_change";

    /**
     * 用户和机器人的会话首次被创建
     */
    String P2P_CHAT_CREATE = "p2p_chat_create";

    /**
     * 接收消息
     */
    String MESSAGE = "message";

    /**
     * 机器人进群
     */
    String ADD_BOT = "add_bot";

    /**
     * 用户进群
     */
    String ADD_USER_TO_CHAT = "add_user_to_chat";

    /**
     * 应用商店应用购买
     */
    String ORDER_PAID = "order_paid";

    /**
     * 获取 app_access_token url（应用商店应用）
     */
    String APP_ACCESS_TOKEN_URL = "/open-apis/auth/v3/app_access_token/";

    /**
     * 获取 app_access_token url（企业自建应用）
     */
    String INTERNAL_APP_ACCESS_TOKEN_URL = "/open-apis/auth/v3/app_access_token/internal/";

    /**
     * 获取 tenant_access_token url（应用商店应用）
     */
    String TENANT_ACCESS_TOKEN_URL = "/open-apis/auth/v3/tenant_access_token/";

    /**
     * 获取 tenant_access_token url（企业自建应用）
     */
    String INTERNAL_TENANT_ACCESS_TOKEN_URL = "/open-apis/auth/v3/tenant_access_token/internal/";

    /**
     * 重新推送 app_ticket
     */
    String RESEND_APP_TICKET_URL = "/open-apis/auth/v3/app_ticket/resend/";

    /**
     * 批量获取用户信息
     */
    String USER_BATCH_GET = "/open-apis/contact/v1/user/batch_get?";

    /**
     * 获取部门用户详情
     */
    String DEPARTMENT_USER_DETAIL_LIST_URL = "/open-apis/contact/v1/department/user/detail/list";

    /**
     * 获取部门用户列表
     */
    String DEPARTMENT_USER_LIST_URL = "/open-apis/contact/v1/department/user/list";

    /**
     * 获取子部门列表
     */
    String DEPARTMENT_SIMPLE_LIST_URL = "/open-apis/contact/v3/departments";

    /**
     * 获取部门详情
     */
    String DEPARTMENT_INFO_GET_URL = "/open-apis/contact/v3/departments/%s";

    /**
     * 获取飞书的部门列表
     */
    String DEPARTMENTID_LIST_INFO_GET_URL = "/open-apis/contact/v3/departments";

    /**
     * 获取飞书的部门列表
     */
    String TENANT_QUERY = "/open-apis/tenant/v2/tenant/query";

    /**
     * 获取应用登录用户信息
     */
    String TOKEN_LOGIN_VALIDATE_URL = "/open-apis/mina/v2/tokenLoginValidate";

    /**
     * 获取后台登录用户身份
     */
    String LOGIN_VALIDATE_URL = "/open-apis/authen/v1/access_token";

    /**
     * 校验应用管理员
     */
    String IS_USER_ADMIN_URL = "/open-apis/application/v3/is_user_admin";

    /**
     * 获取通讯录授权范围
     */
    String GET_CONTACT_SCOPT_URL = "/open-apis/contact/v1/scope/get";

    /**
     * 发送消息
     */
    String SEND_MESSAGE_URL = "/open-apis/message/v4/send/";

    /**
     * 批量发送消息
     */
    String BATCH_SEND_MESSAGE_URL = "/open-apis/message/v4/batch_send/";

    /**
     * 发通知
     */
    String SEND_NOTIFY_URL = "/open-apis/notify/v4/appnotify";

    /**
     * 获取审批详情
     */
    String GET_APPROVAL_DETAIL = "/approval/openapi/v2/instance/get";

    /**
     * 查询订单详情
     */
    String GET_ORDER = "/open-apis/pay/v1/order/get";

    /**
     * 查看审批定义
     */
    String GET_APPROVAL = "/approval/openapi/v2/approval/get";

    /**
     * 创建审批
     */
    String CREATE_INSTANCE = "/approval/openapi/v2/instance/create";

    /**
     * 上传文件
     */
    String FILE_UPLOAD = "/approval/openapi/v2/file/upload";

    /**
     * 审批订阅
     */
    String SUBSCRIBE_APPROVAL = "/approval/openapi/v2/subscription/subscribe";

    /**
     * 消息推送国内机票订单详情url
     */
    String MESSAGE_URL_ORDER_AIR = "pages/orderManage/orderDetail/air/index?orderId=";

    /**
     * 消息推送酒店订单详情url
     */
    String MESSAGE_URL_ORDER_HOTEL = "pages/orderManage/orderDetail/hotel/index?orderId=";

    /**
     * 消息推送火车订单详情url
     */
    String MESSAGE_URL_ORDER_TRAIN = "pages/orderManage/orderDetail/train/index?orderId=";

    /**
     * 消息推送用车订单详情url
     */
    String MESSAGE_URL_ORDER_TAXI = "pages/orderManage/orderDetail/index?orderId=";

    /**
     * 消息推送申请单详情url
     */
    String MESSAGE_URL_APPLICATION_DETAIL_APPLY = "url=application/detail?apply_id=";

    /**
     * 消息推送退改申请单详情url
     */
    String MESSAGE_URL_APPLICATION_REFUND_CHANGE_DETAIL_APPLY = "url=application/refund/change/detail?apply_id=";

    /**
     * 消息推送差旅审批详情url
     */
    String MESSAGE_URL_APPLICATION_TRIP_APPLY = "pages/approvelList/applyDetail/index?applyId=";

    /**
     * 消息推送用车审批详情url
     */
    String MESSAGE_URL_APPLICATION_TAXI_APPLY = "pages/approvelList/applyDetail/carApply/index?applyId=";

    /**
     * 个人虚拟卡审批
     */
    String MESSAGE_URL_BANK_INDIVIDUAL_APPLY = "url=application/virtual/detail?apply_id=";

    /**
     * 虚拟卡核销审批
     */
    String MESSAGE_URL_VIRTUAL_CARDWRITE_OFF_APPLY = "url=workbench/vrtualCardWriteOffDetail?apply_id=";

    /**
     * 订单列表页url
     */
    String MESSAGE_URL_ORDER = "pages/orderManage/orderList/index";

    /**
     * 申请单列表页url
     */
    String MESSAGE_URL_APPLY = "pages/workbench/index";

    /**
     * 虚拟卡额度详情url
     */
    String MESSAGE_URL_APPLY_VIRTUAL_DETAIL = "url=virtual/virtualQuota";

    // 消息类型-审批
    String MSG_TYPE_APPLY = "apply";

    // 消息类型-订单
    String MSG_TYPE_ORDER = "order";

    /**
     * 申请人
     */
    String SAAS_VIEW_TYPE_APPLYER = "1";

    /**
     * 审批人
     */
    String SAAS_VIEW_TYPE_APPROVER = "2";


    // 根部门ID
    String ROOT_DEPARTMENT_CODE = "0";

    //差旅
    String APPROVAL_FORM_REASON = "申请事由";
    String APPROVAL_FORM_TRIP_TYPE = "交通工具";
    String APPROVAL_FORM_SINGLE = "单程往返";
    String APPROVAL_FORM_DEPATURE = "出发城市";
    String APPROVAL_FORM_DESTINATION = "目的城市";
    String APPROVAL_FORM_DATE = "DateInterval";
    String APPROVAL_FORM_COMPANION = "出行人";
    String APPROVAL_FORM_TRIP_GROUP = "出差控件组";
    String APPROVAL_FORM_USE_HOTEL = "是否使用酒店";

    //用车
    String APPROVAL_FORM_CAR_CITY = "用车城市";
    String APPROVAL_FORM_CAR_USE_COUNT = "用车次数";
    String APPROVAL_FORM_CAR_AMOUNT = "用车费用";
    String APPROVAL_FORM_CAR_USE_FLAG = "是否用车";
    String APPROVAL_FORM_CAR_OUT_GROUP = "外出控件组";
    String APPROVAL_FORM_CAR_OUT_GROUP_TIME_UNIT_DAY = "DAY";
    /**
     * 外出套件
     */
    String APPROVAL_FORM_TYPE_OUT_GROUP = "outGroup";
    /**
     * 明细
     */
    String APPROVAL_FORM_TYPE_FIELD_LIST = "fieldList";
    /**
     * 单行文本
     */
    String APPROVAL_FORM_TYPE_INPUT = "input";
    /**
     * 日期区间
     */
    String APPROVAL_FORM_TYPE_DATE_INTERVAL = "dateInterval";
    /**
     * 是否用车：是
     */
    String APPROVAL_FORM_VALUE_USE_CAR ="是";


    // 人员id类型：open_id
    String ID_TYPE_OPEN_ID = "open_ids";

    // 人员id类型：employee_id
    String ID_TYPE_EMPLOYEE_ID = "employee_ids";

    /**
     * 登录页
     */
    String FEISHU_ISV_APP_HOME = "/fsIsvLogin?appId=%s&";
    String FEISHU_ISV_APPLINK_HOME_URL = "https://applink.feishu.cn/client/mini_program/open?appId=%s&mode=window-semi&path=";

    String FEISHU_EIA_APP_HOME = "/fsSelfLogin?appId=%s&";

    /**
     * 订单当前状态，"normal" -正常；"refund"-已退款；
     */
    String ORDER_NORMAL_STATUS = "normal";

    /**
     * 订单当前状态，"normal" -正常；"refund"-已退款；
     */
    String ORDER_REFUND_STATUS = "refund";

    String EIA_INVOKE_TYPE = "user_id";

    String ISV_INVOKE_TYPE = "open_id";

    /**
     * 获取用户详情url
     */
    String GET_USER_INFO_DETAIL = "/open-apis/user/v1/batch_get_id?";

    /**
     * 获取打卡记录
     */
    String GET_ATTENDANCE_RECORD = "/open-apis/attendance/v1/user_tasks/query?employee_type=employee_id";

    /**
     * 获取考勤组详情
     */
    String GET_ATTENDANCE_GROUP_INFO = "/open-apis/attendance/v1/groups/%s?employee_type=employee_id&dept_type=open_id";

    /**
     * 创建考勤组
     */
    String CREATE_ATTENDANCE_GROUP = "/open-apis/attendance/v1/groups";

    /**
     * JSAPI 临时授权凭证
     */
    String JSAPI_TICKET = "/open-apis/jssdk/ticket/get";

    /**
     * 创建日历
     */
    String ADD_CALENDARS = "/open-apis/calendar/v4/calendars";

    /**
     * 创建日程
     */
    String ADD_EVENTS = "/open-apis/calendar/v4/calendars/%s/events";

    /**
     * 修改日程
     */
    String UPDATE_EVENTS = "/open-apis/calendar/v4/calendars/%s/events/%s";

    /**
     * 删除日程
     */
    String DELETE_EVENTS = "/open-apis/calendar/v4/calendars/%s/events/%s?need_notification=true";

    /**
     * 创建日程参与人员
     */
    String EVENTS_ATTENDEES = "/open-apis/calendar/v4/calendars/%s/events/%s/attendees?user_id_type=%s";

    /**
     * 员工详情新版接口
     */
    String SINGLE_USER_DETAIL = "/open-apis/contact/v3/users/%s?user_id_type=%s&department_id_type=department_id";

    /**
     * 通过手机号获取员工id
     */
    String BATCH_GET_ID = "/open-apis/contact/v3/users/batch_get_id?user_id_type=user_id";

    /**
     * 新版接口 EIA 用户id类型
     */
    String EIA_USER_ID_TYPE = "user_id";

    /**
     * 新版接口 ISV 用户id类型
     */
    String ISV_USER_ID_TYPE = "open_id";

    /**
     * 获取部门直属用户列表
     */
    String GET_DEPARTMENT_USERS_V3 = "/open-apis/contact/v3/users/find_by_department?department_id=%s&department_id_type=department_id";

    /**
     * 获取子部门列表
     */
    String GET_SUB_DEPARTMENT_LIST = "/open-apis/contact/v3/departments/%s/children?department_id_type=department_id";

    /**
     * 飞书人事（标准版） 批量获取员工花名册信（里面包含合同公司信息）
     */
    String GET_EHR_V1_EMPLOYEES = "/open-apis/ehr/v1/employees?view=full&page_size=%s&user_id_type=union_id&user_ids=%s";


}
