package com.fenbeitong.openapi.plugin.customize.zhiou.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BeisenParamDTO
 * @Description 北森推送接口参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeisenParamDTO {
    /**
     * 出差列表
     */
    @JsonProperty("BusinessList")
    private List<BeisenAttendancePushDTO> businessList;

    /**
     * 主键类型
     */
    @JsonProperty("IdentityType")
    private Integer identityType;

    /**
     * 异常消息邮箱
     */
    @JsonProperty("ErrorEmail")
    private String errorEmail;
}
