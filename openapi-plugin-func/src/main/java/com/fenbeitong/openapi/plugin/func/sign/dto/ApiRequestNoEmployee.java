package com.fenbeitong.openapi.plugin.func.sign.dto;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 不含员工
 */
@Data
@Builder
@AllArgsConstructor
@ApiModel("ApiRequestNoEmployee")
public class ApiRequestNoEmployee extends ApiRequestBase {

}
