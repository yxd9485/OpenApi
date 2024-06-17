package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: FinanceProjectMappingRespDto</p>
 * <p>Description: 项目映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 11:45 AM
 */
@Data
public class FinanceProjectMappingRespDto extends FinanceBaseRespDto {

    private FinanceProjectMappingRespData data;

    @Data
    public static class FinanceProjectMappingRespData {

        private Integer total;

        private List<FinanceProjectMappingDto> dataList;
    }
}
