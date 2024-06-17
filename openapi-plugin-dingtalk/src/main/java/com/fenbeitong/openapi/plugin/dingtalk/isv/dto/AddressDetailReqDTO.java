package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/2/18 下午6:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDetailReqDTO {

    private String id;
    private String companyAddressId;
    private String addressDetail;
    private String address;
    private double lng;
    private double lat;
    private String cityName;
    private String cityCode;
    private String userName;
    private String telephone;
    private String companyId;
    private String companyName;
    private String employeeId;
    private String employeeName;
    private String creatorId;
    private String creatorName;
    private String createTime;
    private String modifierId;
    private String modifierName;
    private String modifyTime;
    private boolean companyFlag;
    private boolean isDefault;
    private boolean isDelete;
    private int gender;

}
