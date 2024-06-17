package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fenbeitong.openapi.plugin.seeyon.entity.SuperModel;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import lombok.*;

/**
 * SeeyonRestRequest
 *
 * @author Ivan
 * @version 1.0 Create by Ivan on 2019/4/1 - 19:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class SeeyonRestReq extends SuperModel {

  private static final long serialVersionUID = -7775217544105326853L;
  private String orgName;
  private Long compareDaysGap;

  public static SeeyonRestReq fromJson(String requestJson) {
    return JacksonHelper.readValue(requestJson, SeeyonRestReq.class);
  }
}
