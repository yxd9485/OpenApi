package com.fenbeitong.openapi.plugin.customize.wawj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: OpenWawjBusinessTypeConf</p>
 * <p>Description: 我爱我家业务类型配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/9 6:14 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_wawj_business_type_conf")
public class OpenWawjBusinessTypeConf {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 业务类型编码
     */
    @Column(name = "BUSINESS_TYPE_CODE")
    private String businessTypeCode;

    /**
     * 业务类型名称
     */
    @Column(name = "BUSINESS_TYPE_NAME")
    private String businessTypeName;

    /**
     * 城市公司编码
     */
    @Column(name = "CITY_COMPANY_CODE")
    private String cityCompanyCode;

    /**
     * 城市公司名称
     */
    @Column(name = "CITY_COMPANY_NAME")
    private String cityCompanyName;


}
