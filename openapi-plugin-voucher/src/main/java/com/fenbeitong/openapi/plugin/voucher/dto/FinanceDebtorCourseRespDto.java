package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceDebtorCourseRespDto</p>
 * <p>Description: 财务账单服务费借方科目映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 4:56 PM
 */
@Data
public class FinanceDebtorCourseRespDto {

    private FinanceDebtorCourseRespData data;

    @Data
    public static class FinanceDebtorCourseRespData {

        private FinanceCourseDto courseType7;
    }
}
