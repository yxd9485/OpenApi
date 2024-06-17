package com.fenbeitong.openapi.plugin.func.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenCommonPageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 供应商查询分页结果
 *
 * @author ctl
 * @date 2022/4/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuncSupplierListPageDTO extends OpenCommonPageDTO {

    /**
     * 供应商信息列表
     */
    @JsonProperty("suppliers")
    List<FuncSupplierResDTO> suppliers;

}
