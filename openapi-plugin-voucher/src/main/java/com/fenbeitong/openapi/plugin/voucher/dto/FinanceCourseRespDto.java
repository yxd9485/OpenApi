package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: FinanceCourseRespDto</p>
 * <p>Description: 财务科目清单响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 4:09 PM
 */
@Data
public class FinanceCourseRespDto extends FinanceBaseRespDto {

    private FinanceCourseRespData data;

    @Data
    public static class FinanceCourseRespData {

        private Integer pageIndex;

        private Integer pageSize;

        private Integer total;

        private List<FinanceCourseDto> data;
    }
}
