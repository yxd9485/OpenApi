package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import lombok.Data;

/**
 * 钉钉正向审批同行人表单数据
 * @Auther zhang.peng
 * @Date 2021/8/14
 */
@Data
public class DingtalkPartnerDto {

    // 员工Id
    private String empId;

    // 员工姓名
    private String name;

    private String avatar;
}
