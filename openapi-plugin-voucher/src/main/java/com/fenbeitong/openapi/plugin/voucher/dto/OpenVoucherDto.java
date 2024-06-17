package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: OpenVoucherDto</p>
 * <p>Description: 凭证数据</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/3/4 7:38 PM
 */
@Data
public class OpenVoucherDto {

    private Long id;

    private Integer year;

    private Integer month;

    private String accountCode;

    private String accountName;

    private Integer voucherType;

    private String voucherTypeName;

    private String summary;

    private BigDecimal credit;

    private BigDecimal debit;

    private String employeeCode;

    private String employeeName;

    private String deptCode;

    private String deptName;

    private String projectCode;

    private String projectName;

    private String supplierName;

    private String supplierCode;

    private String operatorId;

    private String operatorName;

    private String voucherDate;

    private String createTime;

    private String updateTime;

    private String ext1;

    private String ext2;

    private String ext3;

    private String ext4;

    private String ext5;

    private String ext6;

    private String ext7;

    private String ext8;

    private String ext9;

    private String ext10;

}
