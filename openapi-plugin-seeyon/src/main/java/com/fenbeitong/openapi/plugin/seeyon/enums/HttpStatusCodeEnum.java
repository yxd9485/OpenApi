package com.fenbeitong.openapi.plugin.seeyon.enums;


import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResultEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * HttpStatusCodeEnum
 *
 * <p>Http Status Codes
 *
 * @author ivan
 * @version 1.0 Created by ivan on 12/13/18 - 5:58 PM.
 * @see org.springframework.http.HttpStatus
 * @see HttpServletResponse
 * @see <a href="http://www.iana.org/assignments/http-status-codes">HTTP Status Code Registry</a>
 * @see <a href="http://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List of HTTP status codes -
 *     Wikipedia</a>
 */
public enum HttpStatusCodeEnum {
  TEST(1000, "EXCEPTION"),
  /** -1 */
  UNKNOWN(999, "未知错误"),
  // 2xx Success
  /** 200 */
  OK(200, "请求已成功"),
  /** 201 */
  CREATED(201, "请求资源已创建"),
  /** 202 */
  ACCEPTED(202, "服务器已接受请求"),
  /** 203 */
  NON_AUTHORITATIVE_INFORMATION(203, "请求已处理，非授权信息"),
  /** 204 */
  NO_CONTENT(204, "请求已处理，无返回"),
  /** 205 */
  RESET_CONTENT(205, "请求已处理，连接重置"),
  // 4xx Request Error Response
  /** 400 */
  BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, "请求参数错误或不完整"),
  /** 400 - JSON格式错误 */
  JSON_FORMAT_ERROR(HttpServletResponse.SC_BAD_REQUEST, "JSON格式错误"),
  /** 401 */
  TOKEN_INVALID(HttpServletResponse.SC_UNAUTHORIZED, "认证Token无效"),
  UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "请先进行授权认证"),
  /** 403 */
  FORBIDDEN(HttpServletResponse.SC_FORBIDDEN, "无权查看"),
  /** 404 */
  NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "未找到该资源"),
  /** 405 */
  METHOD_NOT_ALLOWED(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "请求方式不支持"),
  /** 406 */
  NOT_ACCEPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE, "请求被拒绝"),
  /** 411 */
  LENGTH_REQUIRED(HttpServletResponse.SC_LENGTH_REQUIRED, "长度受限"),
  /** 415 */
  UNSUPPORTED_MEDIA_TYPE(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "不支持的媒体类型"),
  /** 416 */
  REQUESTED_RANGE_NOT_SATISFIABLE(
      HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "不能满足请求的范围"),
  // 5xx Service Error Response
  /** 500 */
  INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "接口服务错误"),
  /** 503 */
  SERVICE_UNAVAILABLE(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "请求超时"),
  ;

  /** Code */
  private int code;
  /** Message */
  private String msg;

  HttpStatusCodeEnum(int code, String msg) {
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
