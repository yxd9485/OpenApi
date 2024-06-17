package com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther zhang.peng
 * @Date 2021/4/27
 */
@Data
@Builder
public class YunzhijiaUserInfoResponse {

    /**
     * 分贝通员工id
     */
    private String userId;
}
