package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 归档任务dto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilingJobReqDTO {


    /**
     * 锁Key
     */
    @JsonProperty("lock_key")
    private String lockKey;

    @JsonProperty("filing_month")
    private Integer filingMonth;

    private String host;

    @JsonProperty("access_key")
    private String accessKey;

    @JsonProperty("secret_key")
    private String secretKey;


    /**
     * 档案类型
     */
    @JsonProperty("third_archive_type")
    private List<String> thirdArchiveType;

    @JsonProperty("archive_type_mapping")
    private Map<String, String> archiveTypeMapping;

    /**
     * 账簿信息
     */
    @JsonProperty("book_info")
    private List<BookInfo> bookInfo;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfo {

        @JsonProperty("company_id")
        private String companyId;

        @JsonProperty("start_time")
        private String startTime;

        @JsonProperty("end_time")
        private String endTime;
        /**
         * 公司编码
         */
        @JsonProperty("org_code")
        private String orgCode;

        /**
         * 账簿编码
         */
        @JsonProperty("book_code")
        private List<String> bookCode;
    }
}
