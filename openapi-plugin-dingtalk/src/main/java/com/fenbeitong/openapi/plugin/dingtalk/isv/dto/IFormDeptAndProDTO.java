package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/12/09 下午4:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IFormDeptAndProDTO {

    private String tab;

    private String department;

    private String project;


}


