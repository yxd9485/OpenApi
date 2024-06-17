package com.fenbeitong.openapi.plugin.func.altman.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * Created by xiaowei on 2020/05/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAltmanOrderResDTO {

    List<OpenAltmanOrderListResDTO> results;
    private Integer pageIndex;
    private Integer pageSize;
    private Integer totalCount;
}
