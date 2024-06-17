package com.fenbeitong.openapi.plugin.dingtalk.common.message.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2020/11/13.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_msg_recipient")
public class DingtalkMsgRecipient {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 钉钉企业ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 应用agentId
     */
    @Column(name = "AGENT_ID")
    private String agentId;

    /**
     * 接收人用户ID（钉钉）
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 消息接收人姓名
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 接收消息的事件列表，用逗号分隔。不填表示所有都接收
     */
    @Column(name = "EVENT_TAGS")
    private String eventTags;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 备注
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * 状态：0：无效 1：有效
     */
    @Column(name = "STATUS")
    private Long status;


}
