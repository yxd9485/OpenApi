package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei;

import lombok.Data;

/**
 * 获取表单信息请求DTO
 * @Auther zhang.peng
 * @Date 2021/5/28
 */
@Data
public class FanWeiCreateWorkflowReqDTO {

    private int pageNo;
    private int pageSize;
    private int recordCount;
    private int userId;
    private int workflowType;
    private String[] conditions;

}
