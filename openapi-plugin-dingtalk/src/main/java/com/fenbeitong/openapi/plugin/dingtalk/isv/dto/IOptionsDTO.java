package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/8/23 下午4:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IOptionsDTO {

    private String key;

    private String value;

}


