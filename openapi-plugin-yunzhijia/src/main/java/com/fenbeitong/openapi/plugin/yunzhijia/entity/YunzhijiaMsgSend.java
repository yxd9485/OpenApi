package com.fenbeitong.openapi.plugin.yunzhijia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/04/07.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yunzhijia_msg_send")
public class YunzhijiaMsgSend {

    /**
     * 主键自增ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 企业ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 发送消息公众号
     */
    @Column(name = "AGENT_ID")
    private String agentId;

    /**
     * 公众号秘钥
     */
    @Column(name = "AGENT_SECRET")
    private String agentSecret;

    /**
     * 公众号状态，默认0，0：开启，1：关闭
     */
    @Column(name = "STATE")
    private Integer state;

    /**
     * 应用类型，可能会存在多个应用类型，可以用type来进行区分
     */
    @Column(name = "TYPE")
    private Integer type;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
