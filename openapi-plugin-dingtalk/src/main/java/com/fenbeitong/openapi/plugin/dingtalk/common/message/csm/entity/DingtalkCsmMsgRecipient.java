package com.fenbeitong.openapi.plugin.dingtalk.common.message.csm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2020/11/12.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_csm_msg_recipient")
public class DingtalkCsmMsgRecipient {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 分贝通钉钉企业ID，通过钉钉的ID获取钉钉相关权限信息
     */
    @Column(name = "FBT_CORP_ID")
    private String fbtCorpId;

    /**
     * 分贝通公司ID，分贝通在分贝通系统中的公司ID
     */
    @Column(name = "FBT_COMPANY_ID")
    private String fbtCompanyId;

    /**
     * 客户企业ID，客户在钉钉或企业微信中的ID
     */
    @Column(name = "CLIENT_CORP_ID")
    private String clientCorpId;

    /**
     * 客户分贝通公司ID，客户在分贝通的公司ID
     */
    @Column(name = "CLIENT_COMPANY_ID")
    private String clientCompanyId;

    /**
     * 分贝通钉钉应用ID，用于获取权限信息，发送消息相关参数必传
     */
    @Column(name = "AGENT_ID")
    private String agentId;

    /**
     * 客户成功钉钉ID
     */
    @Column(name = "CSM_DINGTALK_ID")
    private String csmDingtalkId;

    /**
     * 分贝通客户成功名称，一个公司对应着一个客户成功。用于错误消息推送通知
     */
    @Column(name = "CSM_NAME")
    private String csmName;

    /**
     * 描述相关信息
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * 公司与客户成功对应关系状态，默认1，0:失效，1:有效
     */
    @Column(name = "STATUS")
    private Long status;

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
