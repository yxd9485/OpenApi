package com.fenbeitong.openapi.plugin.daoyiyun.constant;

/**
 * @author lizhen
 */
public interface DaoYiYunConstant {

    /**
     * token的redis key
     */
    String REDIS_KEY_TOKEN = "daoyiyun_token:{0}";

    /**
     * 审批单锁
     */
    String REDIS_KEY_APPLY = "daoyiyun_apply_{0}_{1}_{2}";

    /**
     * URL有效性验证
     */
    String EVNET_TYPE_URL_VERIFY = "URL_VERIFY";

    /**
     * 表单数据更新
     */
    String EVENT_TYPE_FORM_DATA_MODIFY = "FORM_DATA_MODIFY";

    /**
     * 审批完成
     * 1未完成，2已完成
     */
    String APPROVE_FINISH = "2";

    /**
     * 新增审批单
     */
    String APPROVE_CREATE = "create";

    /**
     * 变更审批单
     */
    String APPROVE_UPDATE = "update";

    /**
     * 作废审批单
     */
    String APPROVE_CANCEL = "cancel";

    /**
     * 审批单配置信息
     */
    String APPROVE_ITEM_CODE = "daoyiyun_apply_code";

    /**
     * 道一云HOST
     */
    String DAO_YI_YUN_HOST = "https://qiqiao.do1.com.cn/";

    /**
     * 获取access_key
     */
    String URL_ACCESS_KEY = "plus/cgi-bin/securities/access_key";

    /**
     * 获取token
     */
    String URL_ACCESS_TOKEN = "plus/cgi-bin/securities/qiqiao_token";

    /**
     * 查询主表数据
     * 0 applicationId 应用id
     * 1 formModelId 表单模型id
     * 2 id 表单实例Id
     *
     */
    String URL_APPLY_MAIN_FORM = "plus/cgi-bin/open/applications/{0}/forms/{1}/{2}";

    /**
     * 查询子表数据
     * 0 applicationId 应用id
     * 1 parentId 主表单实例id
     * 2 parentFieldName 关联主表的外键字段名
     * 3 subFormModelId 子表单模型id
     */
    String URL_APPLY_SUB_FORM = "plus/cgi-bin/open/applications/{0}/forms/{1}/{2}/{3}";

    /**
     * 查询用户信息
     * 0 用户id
     */
    String URL_USER_INFO = "plus/cgi-bin/open/users/{0}";

    /**
     * 使用account查询用户信息
     */
    String URL_SUER_INFO_ACCOUNT = "plus/cgi-bin/open/users/account";


    /**
     * 修改表单
     * 0 applicationId 应用id
     * 1 formModelId 表单模型id
     *
     */
    String URL_APPLY_MODIFY = "/plus/cgi-bin/open/applications/{0}/forms/{1}";
}

