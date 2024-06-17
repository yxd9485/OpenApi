package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: FinanceVirtualCardDebtorCourseRespDto</p>
 * <p>Description: 虚拟卡核销单科目映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 5:07 PM
 */
@Data
public class VirtualCardDebtorCourseMappingRespDto extends FinanceBaseRespDto {

    private VirtualCardDebtorCourseMappingRespData data;

    @Data
    public static class VirtualCardDebtorCourseMappingRespData {

        private Integer total;

        private List<VirtualCardDebtorCourseMappingDto> dataList;
    }
}
