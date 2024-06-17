package com.fenbeitong.openapi.plugin.seeyon.enums;


import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResultEntity;

/**
 * ApiStatusCodeEnum
 *
 * <p>Self Defined Api Status Code
 *
 * @author ivan
 * @version 1.0 Created by ivan on 12/15/18 - 3:12 PM.
 */
public enum ApiStatusCodeEnum {
  /** 996 */
  UNKNOWN(996, "未知错误"),
  /** 501 */
  UNKOWN_API_TYPE(501, "未知的接口类型"),
  /* 6XX Signature */
  /** 11000 */
  UN_SIGN(11000, "sign签名未传递"),
  /** 601 - 11001 */
  SIGN_ERROR(11001, "sign签名不正确"),
  /** 11003 */
  UN_TIMESTAMP(11003, "timestamp时间戳未传递"),
  /** 11004 */
  UN_DATA(11004, "data数据未传递"),
  /** 11009 */
  UN_VALID_TIMESTAMP(11009, "timestamp失效"),

  /** 10002 */
  UN_VALID_COMPANY_ID(10002, "Company ID参数错误"),
  /** 11006 */
  UN_VALID_EMPLOYEE_ID(11006, "Employee ID参数错误"),

  /** 12000 */
  OPENAPI_TOKEN_FAILED(12000, "获取Open Api Token错误"),
  /** 12001 */
  SEEYON_TOKEN_FAILED(12001, "获取Seeyon Token错误"),
  /** 12001 */
  SEEYON_ACCOUNT_ID_FAILED(12001, "获取Seeyon Account Id错误"),
  /** 12002 */
  PHONE_NUM_UNFIND(12002, "没有获取到第三方用户ID，手机号不存在"),
  /** 12003 */
  ID_BIND_FAILED(12003, "绑定第三方用户ID失败"),
  /** 12004 */
  SEEYON_ORG_NAME_NOT_FOUND(12004, "获取Seeyon公司账户错误"),
  /** 12005 */
  SEEYON_ORG_ACCOUNTS_FAILED(12005, "获取 Seeyon 单位列表错误"),;

  /** Code */
  private int code;
  /** Message */
  private String msg;

  ApiStatusCodeEnum(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int code() {
    return this.code;
  }

  public String msg() {
    return this.msg;
  }

  public SeeyonResultEntity transform() {
    return SeeyonResultEntity.builder().code(code()).msg(msg()).build();
  }
}
