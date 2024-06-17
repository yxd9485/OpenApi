package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiEmpDelListRequest
 *
 * <p>OpenApi 人员请求
 *
 * @author dave.hansins
 * @version 1.0 Created by dave.hansins on 3/13/19 - 11:31 AM.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonApiEmpRequest {

    /**
     * name : 张5s phone : 13718432812 third_org_unit_id : 5747fbc10f0e60e0709d8d7d third_employee_id :
     * 57ab054c2528226a805bd5e1 role : 3 air_policy :
     * {"unemployee_air":false,"air_other_flag":false,"air_priv_flag":false,"air_verify_flag":true,"air_rule_limit_flag":true,"air_rule_id":"575263e982f880a6d686ce11","exceed_buy_type":1}
     * intl_air_policy :
     * {"unemployee_air":false,"air_other_flag":false,"air_priv_flag":false,"air_verify_flag":true,"air_rule_limit_flag":true,"air_rule_id":"575263e982f880a6d686ce11","exceed_buy_type":1}
     * hotel_policy :
     * {"unemployee_hotel":false,"hotel_other_flag":true,"hotel_priv_flag":true,"hotel_verify_flag":false,"hotel_rule_limit_flag":true,"hotel_rule_id":"575263e982f880a6d686ce11","exceed_buy_type":1}
     * train_policy :
     * {"unemployee_train":false,"train_other_flag":true,"train_priv_flag":true,"train_verify_flag":false,"train_rule_limit_flag":true,"train_rule_id":"575263e982f880a6d686ce11","exceed_buy_type":1}
     * car_policy :
     * {"car_priv_flag":true,"rule_limit_flag":true,"rule_id":2,"exceed_buy_type":1,"allowShuttle":false}
     * mall_policy :
     * {"mall_priv_flag":true,"rule_limit_flag":true,"rule_id":"ofaijwf","personal_pay":true,"exceed_buy_flag":false}
     * dinner_policy :
     * {"dinner_priv_flag":true,"rule_limit_flag":true,"rule_id":"ofaijwf","exceed_buy_type":1}
     */
    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("name")
    private String name;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("phone")
    private String phone;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("third_org_unit_id")
    private String thirdOrgUnitId;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("role")
    private Integer role;
    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("email")
    private String email;
    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("role_type")
    private Integer roleType;
    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("gender")
    private Integer gender;

}
