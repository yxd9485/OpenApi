package com.fenbeitong.openapi.plugin.func.deprecated.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * module: 迁移openapi-java项目<br/>
 * <p>
 * description: 获取token<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/5 17:07
 * @since 2.0
 */
@Data
@ApiModel("获取token实例")
@NoArgsConstructor
@AllArgsConstructor
public class OpenTokenRes {

    @JsonProperty("access_token")
    private String accessToken;
}
