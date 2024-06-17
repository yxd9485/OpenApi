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
 * Created by hanshuqi on 2020/03/27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yunzhijia_apply")
public class YunzhijiaApply {

    /**
     * 自增主键
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
     * 审批应用ID
     */
    @Column(name = "AGENT_ID")
    private String agentId;

    /**
     * 审批应用开发者key
     */
    @Column(name = "AGENT_KEY")
    private String agentKey;

    /**
     * 审批应用开发者secret
     */
    @Column(name = "AGENT_SECRET")
    private String agentSecret;

    /**
     * 审批应用状态，0：可用，1：不可用
     */
    @Column(name = "STATE")
    private Integer state;

    /**
     * 审批回调地址
     */
    @Column(name = "CALL_BACK_URL")
    private String callBackUrl;

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
