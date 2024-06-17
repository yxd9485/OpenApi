package com.fenbeitong.openapi.plugin.seeyon.dto;

import lombok.*;

/**
 * OpenApiRestRequest
 *
 * @author Ivan
 * @version 1.0 Create by Ivan on 2019/4/1 - 19:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class OpenApiRestRequest {
  private String orgName;
}
