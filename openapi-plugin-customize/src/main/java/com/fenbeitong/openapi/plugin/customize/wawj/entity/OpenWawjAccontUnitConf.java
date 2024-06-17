package com.fenbeitong.openapi.plugin.customize.wawj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: OpenWawjAccontUnitConf</p>
 * <p>Description: 我爱我家核算单位配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/9 6:14 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_wawj_accont_unit_conf")
public class OpenWawjAccontUnitConf {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 核算单位编码
     */
    @Column(name = "ACCOUNT_UNIT_CODE")
    private String accountUnitCode;

    /**
     * 核算单位名称
     */
    @Column(name = "ACCOUNT_UNIT_NAME")
    private String accountUnitName;

    /**
     * 法人公司编码
     */
    @Column(name = "INCORPORATED_COMPANY_CODE")
    private String incorporatedCompanyCode;

    /**
     * 法人公司名称
     */
    @Column(name = "INCORPORATED_COMPANY_NAME")
    private String incorporatedCompanyName;

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
