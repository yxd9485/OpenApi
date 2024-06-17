package com.fenbeitong.openapi.plugin.func.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: OpenCompanyBillExtConfig</p>
 * <p>Description: 公司账单扩展配置表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/3 4:04 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_company_bill_ext_config")
public class OpenCompanyBillExtConfig {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 场景类型
     */
    @Column(name = "TYPE")
    private Integer type;

    /**
     * 场景名称
     */
    @Column(name = "TYPE_NAME")
    private String typeName;

    /**
     * 请求方法 1:http;2:dubbo
     */
    @Column(name = "REQUEST_METHOD")
    private Integer requestMethod;

    /**
     * 请求参数
     */
    @Column(name = "REQUEST_PARAM")
    private String requestParam;

    /**
     * etl配置ID
     */
    @Column(name = "ETL_CONFIG_ID")
    private Long etlConfigId;

    /**
     * 后置处理器
     */
    @Column(name = "AFTER_PROCESSOR")
    private String afterProcessor;

}
