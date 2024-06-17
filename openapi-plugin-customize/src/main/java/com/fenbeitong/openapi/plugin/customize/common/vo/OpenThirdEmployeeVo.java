package com.fenbeitong.openapi.plugin.customize.common.vo;

import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenThirdEmployeeVo {

    Integer pageNumber;
    Integer pageCount;
    Integer totalCount;
    List<OpenThirdEmployeeDTO> openThirdEmployeeDTOS;


}
