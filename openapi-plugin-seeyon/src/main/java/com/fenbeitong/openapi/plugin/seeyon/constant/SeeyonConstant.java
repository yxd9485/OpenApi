package com.fenbeitong.openapi.plugin.seeyon.constant;

/**
 * SeeyonConstant
 *
 * <p>Seeyon常量类
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/2/19 - 6:48 PM.
 */
public interface SeeyonConstant {
  /*
  Rest Call Header
   */
  String TOKEN_HEADER = "token";

  /*
  响应key常量
   */
  String SEEYON_TOKEN = "token";
  String SEEYON_ACCOUT_ID = "accountId";
  String SEEYON_ACCOUT_ORG = "accountOrgInfo";
  String SEEYON_ACCOUT_EMP = "accountEmpInfo";

  /*
  部门有效判断
   */
  Boolean ORG_ENABLED_TRUE = true; // 1
  Boolean ORG_ENABLED_FALSE = false;

  /*
  人员有效判断
   */
  Boolean EMP_ENABLED_TRUE = true;
  Boolean EMP_ENABLED_FALSE = false;

  /*
  数据比对时间常量
   */
  Integer DATA_TIME_GAP_DIFF = 0;

  /*
  Retry Times
   */
  Integer RETRY_COUNTER = 5;

  /*
  Retry Sleep
  */
  Long RETRY_SLEEP = 9000L;

  /*
  INIT
   */
  String INIT_CALL = "INIT-CALL";

  /*
  JOB
   */
  String JOB_CALL = "JOB-CALL";

  /*
  JOB
   */
  String COMPARE_DEL = "COMPARE-DEL";

  /*
  Call Result
   */
  String CALL_SUCCESS = "SUCCESS";
  /*
  Call Result
   */
  String CALL_FAIL = "FAIL";

  /*********** 特殊字段 **********/
  // 身份证号
  public static final String FBT_ID_CARD_ITEM = "FBT_ID_CARD";
  // 权限字段
  public static final String FBT_AUTH_ITEM = "FBT_PRIV";

  /*********** redis 相应结果 **********/
  public static final String REDIS_RETURN_SUCCESS = "successed";
  public static final String REDIS_RETURN_FAILED = "failed";

    String ENCRPT_KEY = "aWXoyC4UNb220927";

}
