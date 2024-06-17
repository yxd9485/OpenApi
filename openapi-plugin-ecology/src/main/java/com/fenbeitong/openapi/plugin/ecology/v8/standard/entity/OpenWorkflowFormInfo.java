package com.fenbeitong.openapi.plugin.ecology.v8.standard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 泛微工作流表单配置信息
 * Created by zhangpeng on 2021/06/04.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_workflow_form_info")
public class OpenWorkflowFormInfo {

    /**
     *
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 公司id
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 审批类型 , 对应 CommonServiceTypeConstant 中的值
     */
    @Column(name = "approve_type")
    private String approveType;

    /**
     * 表单工作流id
     */
    @Column(name = "workflow_id")
    private String workflowId;

    /**
     * 表单工作流类型
     */
    @Column(name = "workflow_type")
    private String workflowType;

    /**
     *
     */
    @Column(name = "page_no")
    private int pageNo;

    /**
     *
     */
    @Column(name = "page_size")
    private int pageSize;

    /**
     *
     */
    @Column(name = "record_count")
    private int recordCount;

    /**
     *
     */
    @Column(name = "user_id")
    private int userId;

    /**
     *
     */
    @Column(name = "form_workflow_type")
    private int formWorkflowType;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;


}
