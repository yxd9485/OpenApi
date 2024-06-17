package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

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
public class IFormFieldPropsDTO {

    private String label;

    private String placeholder;

    private Boolean required;

    private Boolean disabled;

    private Boolean invisible;

    private Boolean hidden;

    private List<IOptionsDTO> options;


}


