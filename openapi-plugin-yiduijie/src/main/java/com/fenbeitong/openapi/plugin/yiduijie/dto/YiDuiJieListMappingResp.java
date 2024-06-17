package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.yiduijie.model.mapping.MappingDTO;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: YiDuiJieListMappingResp</p>
 * <p>Description: 易对接</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/14 6:52 PM
 */
@Data
public class YiDuiJieListMappingResp {

    @JsonProperty("body")
    private List<MappingDTO> mappingList;

    private String message;

    private Integer status;

    private Integer total;

    private Integer pageIndex;

    private Integer pageSize;

    public boolean success() {
        return status != null && status == 0;
    }
}
