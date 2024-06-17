package com.fenbeitong.openapi.plugin.customize.wantai.constant;

/**
 * @author lizhen
 */
public interface WanTaiArchiveConstant {
    String SYS_CODE_DEFAULT = "DEFAULT";
    String SYS_CODE_NCC = "NCC";
    String SYS_CODE_ERP = "ERP";

    String URL_NCC_FILING_UP = "/uapws/rest/exarc/makedoc";
    String URL_NCC_GET_DATA = "/uapws/rest/exarc/getdata";

    String URL_ERP_TOKEN = "/wterpApi/open/token";
    String URL_ERP_FILING_UP = "/wterpApi/restApi/fbt/recTask";
    String URL_ERP_GET_DATA = "/wterpApi/restApi/fbt/getTaskData";
    String URL_ERP_GET_SYNC = "/wterpApi/restApi/fbt/dappay03/sync";

    String URL_ERP_SUPPLIER = "/wterpApi/restApi/fbt/getCorrsAccounts";

    /**
     * 归档redis key
     */
    String ARCHIVE_FILING_DATA_REDIS_KEY = "archive_filing_up:{0}";

    /**
     * 获取档案数据redis key
     */
    String ARCHIVE_GET_DATA_REDIS_KEY = "archive_get_data:{0}";

    String ACCESS_KEY = "accessKey";

    String SECRET_KEY = "secretKey";

    String  NCC_SUPPLIER_SYNC_END_TIME  ="ncc_supplier_sync_end_time";

    String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    String DEFAULT_START_TIME = "1970-01-01 00:00:00";

    String TOKEN = "token";



}
