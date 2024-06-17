package com.fenbeitong.openapi.plugin.dingtalk.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: DingtalkProcessInstance</p>
 * <p>Description: 钉钉审批实例</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/20 19:41 AM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_process_instance")
public class DingtalkProcessInstance {

    /**
     *
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
     * 流程编码
     */
    @Column(name = "PROCESS_CODE")
    private String processCode;

    /**
     * 用户ID-钉钉
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 审批单名称
     */
    @Column(name = "TITLE")
    private String title;

    /**
     * 流程实例ID
     */
    @Column(name = "INSTANCE_ID")
    private String instanceId;

    /**
     * 业务ID，同一个审批单相同
     */
    @Column(name = "BUSINESS_ID")
    private String businessId;

    /**
     * 操作类型
     */
    @Column(name = "BIZ_ACTION")
    private String bizAction;


}
