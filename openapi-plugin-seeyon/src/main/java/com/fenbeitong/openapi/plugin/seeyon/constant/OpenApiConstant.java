package com.fenbeitong.openapi.plugin.seeyon.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenApiConstant
 *
 * <p>OpenApi 常量类
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/11/19 - 7:07 PM.
 */
public interface OpenApiConstant {
  /*
  响应成功状态码
   */
  Integer RESULT_CODE_SUCCESS = 0;

  /*
  响应错误关键字
   */
  List<String> ERROR_KEY_WORDS =
      new ArrayList<String>() {
        private static final long serialVersionUID = 1532957464922294439L;

        {
          add("errorMsg");
        }
      };

  /*
  Api 请求参数
   */
  String EMPLOYEE_ID_TYPE_THIRD = "1";
  String EMPLOYEE_ID_TYPE_FBT = "0";
  // Third Employee Info
  Integer COMPANY_TYPE_FBT = 1;
  Integer COMPANY_TYPE_THIRD = 2;
  /*
  邮件发送账户
   */
  String HARMONY_MAIL_CUSTOMER_ID = "spacex";
  String HARMONY_MAIL_SERVER_ID = "spacex.billing";
  String HARMONY_MAIL_SEND_LIST_ERROR1 =
      "[\"shuqi.han@fenbeitong.com\"]";
  List<String> HARMONY_MAIL_SEND_LIST_ERROR =
      new ArrayList<String>() {
        private static final long serialVersionUID = -5080059593177434144L;

        {
          add("shuqi.han@fenbeitong.com");
        }
      };
  String HARMONU_MAIL_SUBJECT_ERROR = "推送错误信息邮件提醒";
}
