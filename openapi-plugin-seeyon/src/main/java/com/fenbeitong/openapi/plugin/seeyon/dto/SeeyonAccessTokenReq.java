package com.fenbeitong.openapi.plugin.seeyon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TokenParam
 *
 * <p>seeyon Token请求参数
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/1/19 - 5:59 PM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeeyonAccessTokenReq {
  String userName;
  String password;
}
