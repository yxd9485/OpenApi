package com.fenbeitong.openapi.plugin.dingtalk.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: DingtalkApply</p>
 * <p>Description: 钉钉审批配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/20 19:41 AM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_apply")
public class DingtalkApply {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 钉钉流程编码
     */
    @Column(name = "PROCESS_CODE")
    private String processCode;

    /**
     * 流程名称
     */
    @Column(name = "PROCESS_NAME")
    private String processName;

    /**
     * 分贝通审批类型
     */
    @Column(name = "PROCESS_TYPE")
    private Integer processType;

    /**
     * 分贝通企业ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

}
