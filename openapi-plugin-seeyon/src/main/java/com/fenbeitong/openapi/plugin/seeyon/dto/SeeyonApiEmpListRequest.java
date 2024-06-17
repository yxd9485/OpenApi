package com.fenbeitong.openapi.plugin.seeyon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ApiEmpListRequest
 *
 * <p>OpenApi人员接口请求
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/11/19 - 5:58 PM.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonApiEmpListRequest {
  List<SeeyonApiEmpRequest> employeeList;
}
