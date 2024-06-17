package com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Auther zhang.peng
 * @Date 2021/4/26
 */
@Data
@ToString
public class YunzhijiaTicketDto {

    /**
     * 轻应用id
     */
    @JsonProperty(value = "appId")
    private String appId;

    /**
     *  团队id
     */
    private String eid;

    /**
     *  用户姓名
     */
    private String username;

    /**
     *  云之家用户id
     */
    private String userid;

    /**
     *  工号
     */
    private String jobNo;

    /**
     *  工作圈id
     */
    private String networkid;

    /**
     *  设备id
     */
    private String deviceId;

    /**
     *  团队用户id
     */
    private String openid;

    /**
     *  云之家APP会传递ticket参数给轻应用（把ticket参数追加到轻应用对应的url中）
     */
    private String ticket;

}
