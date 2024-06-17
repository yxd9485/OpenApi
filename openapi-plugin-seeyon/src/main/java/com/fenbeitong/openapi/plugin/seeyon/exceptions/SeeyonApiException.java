package com.fenbeitong.openapi.plugin.seeyon.exceptions;

import com.fenbeitong.openapi.plugin.seeyon.common.SeeyonResultEntity;
import com.fenbeitong.openapi.plugin.seeyon.enums.HttpStatusCodeEnum;

/**
 * ApiException
 *
 * <p>Exception throws from api
 *
 * @author ivan
 * @version 1.0 Created by ivan on 12/15/18 - 2:55 PM.
 */
public class SeeyonApiException extends RuntimeException {

  private static final long serialVersionUID = -2344691769119882244L;
  private final SeeyonResultEntity responseStatusInfo;

  public SeeyonApiException(HttpStatusCodeEnum errorCodeEnum) {
    super(errorCodeEnum.msg());
    this.responseStatusInfo = errorCodeEnum.transform();
  }

  public SeeyonApiException(SeeyonResultEntity apiStatusInfo) {
    super(apiStatusInfo.getMsg());
    this.responseStatusInfo = apiStatusInfo;
  }

  public SeeyonResultEntity getResponseStatusInfo() {
    return responseStatusInfo;
  }
}
