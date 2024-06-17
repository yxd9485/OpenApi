package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaEmployeeReqDTO {
    //注册号
    private String eid;
    //0：手机号码，1：openId，默认0
    private int type;
    //手机号码或者openId数组
    private List<String> array;
}
