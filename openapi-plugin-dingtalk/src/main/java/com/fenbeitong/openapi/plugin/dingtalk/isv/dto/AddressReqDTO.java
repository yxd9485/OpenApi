package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/2/18 下午6:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressReqDTO {

    private List<AddressDetailReqDTO> companyAddressList;
    private List<AddressDetailReqDTO> employeeAddressList;
    private boolean haveCompanyAddress;

}
