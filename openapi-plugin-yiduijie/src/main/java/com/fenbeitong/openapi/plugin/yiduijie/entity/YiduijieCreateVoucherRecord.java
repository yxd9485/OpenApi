package com.fenbeitong.openapi.plugin.yiduijie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: YiduijieCreateVoucherRecord</p>
 * <p>Description: 易对接生成凭证记录</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/03/10 15:08 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yiduijie_create_voucher_record")
public class YiduijieCreateVoucherRecord {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 操作人
     */
    @Column(name = "OPERATOR")
    private String operator;

    /**
     * 操作人ID
     */
    @Column(name = "OPERATOR_ID")
    private String operatorId;

    /**
     * 公司ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 业务类型
     */
    @Column(name = "BUSINESS_TYPE")
    private String businessType;

    /**
     * 批次ID
     */
    @Column(name = "BATCH_ID")
    private String batchId;

    /**
     * 回调地址
     */
    @Column(name = "CALLBACK_URL")
    private String callbackUrl;

    /**
     * 状态编码
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 单据类型
     */
    @Column(name = "TYPE")
    private String type;

    /**
     * 外部系统单据号
     */
    @Column(name = "LOCAL_ID")
    private String localId;

    /**
     * 错误原因
     */
    @Column(name = "MESSAGE")
    private String message;

    /**
     * 易对接xcel链接地址
     */
    @Column(name = "EXCEL_URL")
    private String excelUrl;

    /**
     * 分贝通excel链接地址
     */
    @Column(name = "FBT_EXCEL_URL")
    private String fbtExcelUrl;


}
