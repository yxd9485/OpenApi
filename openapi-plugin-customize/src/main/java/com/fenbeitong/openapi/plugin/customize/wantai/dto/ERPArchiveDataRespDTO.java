package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 */
@Data
public class ERPArchiveDataRespDTO {

    private int code;

    private String msg;

    private String timestamp;

    private ERPArchiveDataDTO data;

    @Data
    public static class ERPArchiveDataDTO {
        private List<Map<String, Object>> records;

        private String pageIndex;

        private String pageSize;

        private String pageCount;

        private String total;
    }

}
