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
 * <p>Title: OpenEcologyWorkflow</p>
 * <p>Description: 泛微工作流</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/17 4:54 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_ecology_workflow")
public class OpenEcologyWorkflow {

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
     * 请求ID
     */
    @Column(name = "REQUEST_ID")
    private String requestId;

    /**
     * 请求标题
     */
    @Column(name = "REQUEST_NAME")
    private String requestName;

    /**
     * 请求重要级别
     */
    @Column(name = "REQUEST_LEVEL")
    private String requestLevel;

    /**
     * 创建者ID
     */
    @Column(name = "CREATOR_ID")
    private String creatorId;

    /**
     * 创建者名称
     */
    @Column(name = "CREATOR_NAME")
    private String creatorName;

    /**
     * 三方员工id
     */
    @Column(name = "THIRD_EMPLOYEE_ID")
    private String thirdEmployeeId;

    /**
     * 分贝通员工id
     */
    @Column(name = "EMPLOYEE_ID")
    private String employeeId;

    /**
     * 当前节点Id
     */
    @Column(name = "CURRENT_NODE_ID")
    private String currentNodeId;

    /**
     * 当前节点名称
     */
    @Column(name = "CURRENT_NODE_NAME")
    private String currentNodeName;

    /**
     * 流程状态
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 0:初始状态;1:审批通过;2:审批未通过
     */
    @Column(name = "AGREED")
    private Integer agreed;

    /**
     * 工作流ID
     */
    @Column(name = "WORKFLOW_ID")
    private String workflowId;

    /**
     * 工作流标题
     */
    @Column(name = "WORKFLOW_NAME")
    private String workflowName;

    /**
     * 工作流类型ID
     */
    @Column(name = "WORKFLOW_TYPE_ID")
    private String workflowTypeId;

    /**
     * 工作流类型名称
     */
    @Column(name = "WORKFLOW_TYPE_NAME")
    private String workflowTypeName;

    /**
     * 开始日期
     */
    @Column(name = "START_DATE")
    private String startDate;

    /**
     * 开始时间
     */
    @Column(name = "START_TIME")
    private String startTime;

    /**
     * 结束日期
     */
    @Column(name = "END_DATE")
    private String endDate;

    /**
     * 结束时间
     */
    @Column(name = "END_TIME")
    private String endTime;

    /**
     * 表单数据
     */
    @Column(name = "FORM_DATA")
    private String formData;

    /**
     * 表单详情数据1
     */
    @Column(name = "DETAIL_FORM_1")
    private String detailForm1;

    /**
     * 表单详情数据2
     */
    @Column(name = "DETAIL_FORM_2")
    private String detailForm2;

    /**
     * 表单详情数据3
     */
    @Column(name = "DETAIL_FORM_3")
    private String detailForm3;

    /**
     * 表单详情数据4
     */
    @Column(name = "DETAIL_FORM_4")
    private String detailForm4;

    /**
     * 表单详情数据5
     */
    @Column(name = "DETAIL_FORM_5")
    private String detailForm5;

    /**
     * 审批数据
     */
    @Column(name = "PROCESS_DATA")
    private String processData;

    /**
     * 创建日期
     */
    @Column(name = "CREATE_DATE")
    private Date createDate;

    /**
     * 流程状态 0:初始状态;1:已完成;2:已关闭
     */
    @Column(name = "STATE")
    private Integer state;

    /**
     * 扩展信息
     */
    @Column(name = "EXT_INFO")
    private String extInfo;

    /**
     * 审批单类别。1差旅，2订单，12用车
     */
    @Column(name = "APPLY_TYPE")
    private Integer applyType;
}
