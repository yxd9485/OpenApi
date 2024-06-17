package com.fenbeitong.openapi.plugin.seeyon.dto;

import lombok.Data;

/**
 * TokenResponse
 *
 * <p>seeyon Token请求响应
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/1/19 - 6:02 PM.
 */
@Data
public class SeeyonAccessTokenResp {
  String id;
  String bindingUser;
}
