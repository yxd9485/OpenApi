package com.fenbeitong.openapi.plugin.customize.wantai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2022/08/08.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_archive_fiscal_period")
public class OpenArchiveFiscalPeriod {

    /**
     *
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 企业ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 账期(yyyy-MM)
     */
    @Column(name = "fiscal_period")
    private String fiscalPeriod;

    /**
     * 上次同步时间
     */
    @Column(name = "last_sync_time")
    private Date lastSyncTime;

    /**
     * 公司编码
     */
    @Column(name = "org_code")
    private String orgCode;

    /**
     * 系统标识（NCC、ERP）
     */
    @Column(name = "sys_code")
    private String sysCode;


    /**
     * 三方档案类型
     */
    @Column(name = "third_archive_type")
    private String thirdArchiveType;


}
