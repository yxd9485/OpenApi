package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: VirtualCardDeductionTypeRespDto</p>
 * <p>Description: 虚拟卡抵扣类项配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:30 PM
 */
@Data
public class VirtualCardDeductionTypeRespDto extends FinanceBaseRespDto {

    private VirtualCardDeductionTypeRespData data;

    @Data
    public static class VirtualCardDeductionTypeRespData{

        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("deduction_list")
        private List<VirtualCardDeductionTypeDto> deductionList;
    }
}
