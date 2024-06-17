package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ControllerDTO
 * @Description 控件结构
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/4/26 下午5:48
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
}
