package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: FinanceBillBizDebtorCourseMappingRespDto</p>
 * <p>Description: 账单业务线借方科目映射响应信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 4:35 PM
 */
@Data
public class FinanceBillBizDebtorCourseMappingRespDto extends FinanceBaseRespDto{

    private FinanceBillBizDebtorCourseMappingRespData data;

    @Data
    public static class FinanceBillBizDebtorCourseMappingRespData{

        private Integer total;

        private List<FinanceBillBizDebtorCourseMappingDto> dataList;
    }
}
