package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.Data;

import java.util.List;

@Data
public class SupplierQueryRespDTO {

    private int code;

    private String msg;

    private String timestamp;

    private List<SupplierDataDTO> data;

    @Data
    public static class SupplierDataDTO {
        //客商编码
        private String idCorr;
        //客商名称
        private String nameCorr;
        //状态
        private String flagInvalid;
        //账套
        private String idCom;
        //税号
        private String varTaxno;
        //客商类型
        private int flagCorr;
        //备注
        private String varRemark;
        //修改时间
        private String dateEdit;

        private List<SupplierAccoutDTO> accouts;
    }

    @Data
    public static class SupplierAccoutDTO {
        //银行账户关键字
        private String idAcctpk;
        //开户银行
        private String varBank;
        //银行账号
        private String varBankacct;
        //收款户名
        private String varBankname;
        //联行号
        private String varBankno;
        //币种
        private String idCurr;
        //明细备注
        private String varDremark;
    }

}
