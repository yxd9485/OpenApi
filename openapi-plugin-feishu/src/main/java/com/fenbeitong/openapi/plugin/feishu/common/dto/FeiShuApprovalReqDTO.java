package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.Data;

@Data
public class FeiShuApprovalReqDTO {
    //审批实例单号
    private String instanceCode;
    //语言类型，非必填
    private String locale;
}
