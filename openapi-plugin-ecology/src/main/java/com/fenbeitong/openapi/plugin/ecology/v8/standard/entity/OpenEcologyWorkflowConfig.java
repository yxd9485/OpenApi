package com.fenbeitong.openapi.plugin.ecology.v8.standard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: OpenEcologyWorkflow</p>
 * <p>Description: 泛微工作流配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 4:54 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_ecology_workflow_config")
public class OpenEcologyWorkflowConfig {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 公司ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 公司名称
     */
    @Column(name = "COMPANY_NAME")
    private String companyName;


    /**
     * webservice地址
     */
    @Column(name = "WS_URL")
    private String wsUrl;

    /**
     * webservice限制的ip地址
     */
    @Column(name = "WS_IP")
    private String wsIp;

    /**
     * 工作流监听
     */
    @Column(name = "WORK_FLOW_LISTENER")
    private String workFlowListener;

    /**
     * 行程数据表单method
     */
    @Column(name = "TRIP_FORM_METHOD")
    private String tripFormMethod;

    /**
     * 标识key
     */
    @Column(name = "TOKEN_KEY")
    private String tokenKey;

    /**
     * 查询参数
     */
    @Column(name = "PARAM_JSON")
    private String paramJson;

}
