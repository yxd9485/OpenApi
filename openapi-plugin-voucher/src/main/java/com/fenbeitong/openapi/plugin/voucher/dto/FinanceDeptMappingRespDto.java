package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: FinanceDeptMappingRespDto</p>
 * <p>Description: 部门映射响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 3:52 PM
 */
@Data
public class FinanceDeptMappingRespDto extends FinanceBaseRespDto {

    private FinanceDeptMappingRespData data;

    @Data
    public static class FinanceDeptMappingRespData{

        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("org_unit_list")
        private List<FinanceDeptMappingDto> orgUnitList;
    }
}
