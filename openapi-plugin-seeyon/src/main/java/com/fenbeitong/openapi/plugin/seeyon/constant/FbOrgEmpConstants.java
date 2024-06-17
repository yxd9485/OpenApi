package com.fenbeitong.openapi.plugin.seeyon.constant;

/**
 * FbOrgEmpConstants
 *
 * <p>分贝推送数据常量类
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/6/19 - 5:38 PM.
 */
public interface FbOrgEmpConstants {
  /*
  请求数据类型
   */
  Integer CALL_TYPE_ORG = 0;
  Integer CALL_TYPE_EMP = 1;

  /*
  请求方法
   */
  String CALL_METHOD_CREATE = "CREATE";
  String CALL_METHOD_UPDATE = "UPDATE";
  String CALL_METHOD_DELETE = "DELETE";

  /*
  请求执行排序号
   */
  String CALL_ORDER_ONE = "a";
  String CALL_ORDER_TWO = "b";
  String CALL_ORDER_THREE = "c";
  String CALL_ORDER_FOUR = "d";
  String CALL_ORDER_FIVE = "e";
  String CALL_ORDER_SIX = "f";

  /*
  请求执行状态
   */
  String CALL_EXECUTE_WAIT = "0";
  String CALL_EXECUTE_DONE = "1";

  /*
  错误数据执行状态
   */
  Integer ERROR_DATA_EXE_RESULT_FINISH = 0;
  Integer ERROR_DATA_EXE_RESULT_WAIT = 1;
  Integer ERROR_DATA_EXE_RESULT_IGNOR = 2;

  /*
  默认员工角色编码
   */
  Integer EMP_ROLE_DEFAULT = 3;

  /*
  初始化Magic No
   */
  Long INIT_DAY_TRIGGER = -1452777L;
}
