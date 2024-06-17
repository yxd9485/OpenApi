package com.fenbeitong.openapi.plugin.seeyon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ApiEmpDelListRequest
 *
 * <p>OpenApi 人员删除请求
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/13/19 - 11:31 AM.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeeyonApiEmpDelListRequest {
  private List<String> thirdEmployeeIds;
}
