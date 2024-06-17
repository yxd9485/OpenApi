package com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket;

import lombok.Data;

/**
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
@Data
public class YunzhijiaTicketReqDto {

    /**
     * 轻应用id
     */
    private String appid;

    /**
     * 云之家APP会传递ticket参数给轻应用（把ticket参数追加到轻应用对应的url中）
     * 时效为1小时
     */
    private String ticket;

}
