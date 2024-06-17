package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.Data;

@Data
public class NCCFilingReqDTO {

    /**
     * 系统编码
     */
    private String syscode;

    /**
     * 密钥
     */
    private String secretkey;

    private String time;

    /**
     * 指令ID
     */
    private String id;

    /**
     * 档案类型编码
     */
    private String arctype;

    private String sign;

    private NCCFilingReqData data;


    @Data
    public static class NCCFilingReqData {
        /**
         * 公司编码
         */
        private String orgcode;

        /**
         * 账簿编码
         */
        private String bookcode;

        /**
         * 会计期间
         */
        private String yearperiod;

        /**
         * 数据开始时间
         */
        private String begindate;
        /**
         * 数据结束时间
         */
        private String enddate;

    }
}
