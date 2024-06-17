package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto;

import lombok.Data;

import java.util.List;

/**
 * 客户和销售线索通用数据
 * @author zhangpeng
 * @date 2021/5/19
 */
@Data
public class NingBoWeiLiCommonDetailsDto {

    public Integer code;
    public String msg;
    public List<String> ext;
    public Result result;

    @Data
    public static class Result {
        public Integer totalSize;
        public Integer count;
        public List<Records> records;

    }

    @Data
    public static class Records {

        /****** 客户信息 ******/

        private String accountName;

        private String phone;

        private String id;

        /****** 销售机会 ******/

        private String opportunityName;

        private String accountId;

        private String entityType;

        private String accountNameInOpportunity;

    }

}
