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
public class YunzhijiaOrgReqDTO {
    //注册号 不必须，如果没有，则以外面的eid参数为准
    private String eid;
    //查询类型，0：根据orgId查询
    private int type;
    //orgId数组
    private List<String> array;
}
