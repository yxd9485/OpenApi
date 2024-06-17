package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: OrderUserInfo</p>
 * <p>Description: 订单用户信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/2 5:24 PM
 */
@Data
public class OrderUserInfo {

    private String id;

    private String name;

    private String phone;

    @JsonProperty("unit_id")
    private String userUnitId;

    @JsonProperty("unit_name")
    private String unitName;

    @JsonProperty("employee_number")
    private String employeeNumber;
}
