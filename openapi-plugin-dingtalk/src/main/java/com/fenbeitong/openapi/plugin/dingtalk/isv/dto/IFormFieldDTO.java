package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/8/23 下午4:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IFormFieldDTO {

    private String id;

    @JsonProperty("bizAlias")
    private String bizAlias;

    @JsonProperty("componentName")
    private String componentName;

    //设置项属性值
    private IFormFieldPropsDTO props;

    private String value;

    @JsonProperty("extendValue")
    private String extendValue;

    private List<IFormFieldDTO> children;


}


