package com.fenbeitong.openapi.plugin.func.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author zhangjindong
 */
public class OpenApiConstant {



    /**
     * 审批状态
     */
    public static class ApplyStatus {
        public final static String APPLY_APPROVED = "APPROVED";
        public final static String APPLY_REJECTED = "REJECTED";
        public final static String APPLY_CANCELED = "CANCELED";
        public final static String APPLY_DELETED = "DELETED";
    }

    /**
     *  网关层企业id key
     */
    public static String APPID = "appId";

    public static String COMPANY_ID = "companyId";

    public static String COMPANY_ID_LOWER = "company_id";

    public static String THIRD_EMP_TYPE = "1";

    public static String FBT_EMP_TYPE = "0";

    public static String ACCESS_TOKEN = "access-token";

    public static String TOKEN = "token";

    public static String APP_TYPE = "appType";

    public static String USER_ID = "user_id";

    public static String EMPTY_APPLY = "审批单申请内容[apply]不可为空";

    public static String COST_INFO ="cost_info";

    public static String ORDER_INFO ="order_info";

    public static String COST_ATTRIBUTION_ID = "cost_attribution_id";


    public static String BILL_ALL = "bill_all";

    public static String SETTLE_ORDER_BASE_BEAN_TYPE_KEY = "settleOrder_baseBean_category_key";

    public static String SETTLE_ORDER_THIRD_EXT_FIELDS = "settleOrder_thirdExtFieldsJson";

    public static String THIRD_FIELDS = "third_fields_json";

    public static String COMPANY_CUSTOM_FILED = "company_custom_filed";

    public static String COMMON_DATA = "commonData";

    public static String TYPE = "type";

    public static String SCR_DATA = "scrData";

    public static String ORDER_STATE = "order_state";
    public static String ORDER_STATUS =  "order_status";
    public static String START_ADRESS_NAME = "start_adress_name";
    public static String END_ADRESS_NAME = "end_adress_name";
    public static String START_ADDRESS_NAME = "start_address_name";
    public static String END_ADDRESS_NAME = "end_address_name";
    public static String ORDER_CATEGORY =  "order_category";

    public static String YYYY_MM_DD_HH_MM_SS ="yyyy-MM-dd HH:mm:ss";

    /**
     *  为日期格式字符串拼接 ，起始时间
     */
    public static final String START_TIME_STR = " 00:00:00";

    /**
     *  为日期格式字符串拼接 ，结束时间
     */
    public static final String END_TIME_STR = " 23:59:59";

    /**
     *  默认页码
     */
    public static final Integer DEFAULT_PAGE_INDEX = 1;

    /**
     *  默认条数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 50;



    public static String OPENAPI = "openapi";

    public static  String  STEREO = "stereo";

    public static String SUBJECT_NAME = "bill_split_result_subjectName";

    public static String SUBJECT_NO = "bill_split_result_subjectNo";

    public static String SUBJECT_BANK_NAME = "bill_split_result_subjectBankName";

    public static String SUBJECT_BANK_NO = "bill_split_result_subjectBankNo";

    public static String SUBJECT_ADDRESS = "bill_split_result_subjectAddr";

    public static String SUBJECT_PHONE = "bill_split_result_subjectPhone";
    public static String FB_SUBJECT_NAME = "subject_name";
    public static String FB_SUBJECT_CODE = "subject_code";
    public static String FB_SUBJECT_BANK = "subject_bank";
    public static String FB_SUBJECT_ACCOUNT = "subject_account";
    public static String FB_SUBJECT_ADDRESS = "subject_address";
    public static String FB_SUBJECT_PHONE = "subject_phone";

    public static String FB_PAYER_CODE ="payer_code";

    public static String PAYER_CODE ="settleOrder_personBean_userBean_employeeNo";

    public static String FB_USER_CODE ="user_code";

    public static String USER_CODE ="settleOrder_consumerBeanList_userBean_employeeNo";

    public static String FB_MANUAL_PRICE ="manual_price";

    public static String MANUAL_PRICE ="settleOrder_categoryExtBean_trainItineraryFee";

    public static String  VALUE_ADDED ="settleOrder_categoryExtBean_shadowClassify_value";

    public static String  ROOT_ORDER_ID = "billConsumeDetail_rootOrderId";

    public static String FB_ROOT_ORDER_ID="root_order_id";

    public static Integer ADD_SERVICE_CATEGORY = 913;



    public static  final BigDecimal DEFAULT_ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);




}
