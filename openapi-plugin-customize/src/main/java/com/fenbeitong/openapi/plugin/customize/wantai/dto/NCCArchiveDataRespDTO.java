package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NCCArchiveDataRespDTO {

    private String id;

    private String status;

    private String message;

    private List<Map<String, Object>> data;

}
