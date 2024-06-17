package com.fenbeitong.openapi.plugin.wechat.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="wechat_apply")
public class WeChatApply {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CORP_ID")
    private String corpId;

    @Column(name = "AGENT_ID")
    private Integer angentId ;

    @Column(name = "AGENT_SECRET")
    private String agentSecret;

    @Column(name = "CALLBACK_URL")
    private String callbackUrl;

    @Column(name = "STATE")
    private Integer state;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;
}
