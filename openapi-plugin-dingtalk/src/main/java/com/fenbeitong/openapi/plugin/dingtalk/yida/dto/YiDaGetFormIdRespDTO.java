package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * create on 2021-08-13 16:52:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiDaGetFormIdRespDTO {

    private Integer idCursor;

    private List<String> data;

    private Integer totalCount;

    private Integer currentPage;

    private Integer tatalPage;

}