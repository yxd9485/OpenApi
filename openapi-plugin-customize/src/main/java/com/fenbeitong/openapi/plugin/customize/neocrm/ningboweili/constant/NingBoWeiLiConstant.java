package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.constant;

/**
 * @Auther zhang.peng
 * @Date 2021/5/18
 */
public interface NingBoWeiLiConstant {

    public static final String NINGBOWEILI_ACCESS_TOKEN = "ningboweili_access_token:{0}";

    public static final String NINGBOWEILI_ACCOUNT_INFO = "ningboweili_account_info:{0}";

    public static final String NINGBOWEILI_ACCOUNT_COUNT = "ningboweili_account_count:{0}";

    public static final String QUERY_URL = "https://api.xiaoshouyi.com/rest/data/v2/query";

    public static final String FETCH_ACCOUNT_SQL = "select accountName,phone,id from account %s";

    public static final String FETCH_OPPORTUNITY_SQL = "select entityType,opportunityName,accountId,id from opportunity %s";

}
