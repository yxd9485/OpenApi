package com.fenbeitong.openapi.plugin.func.project.dto;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@ApiModel("ApiRequestProject")
public class ApiRequestProject extends ApiRequestBase {

}
