package com.fenbeitong.openapi.plugin.func.organization.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName OpenLegalEntityReqDTO
 * @Description 法人主体实体类
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/4/13 下午7:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenCreateLegalEntityReqDTO {

    /**
     * 三方法人实体id
     */
    @JsonProperty("third_id")
    @NotBlank(message = "三方法人实体id【third_id】不可为空")
    private String thirdId;

    /**
     * 法人实体名称
     */
    @JsonProperty("name")
    @NotBlank(message = "法人实体名称【name】不可为空")
    private String name;

    /**
     * 业务编码
     */
    @JsonProperty("code")
    private String code;

    /**
     * 上级三方法人实体id
     */
    @JsonProperty("third_parent_id")
    private String thirdParentId;

    /**
     * 纳税人识别号
     */
    @JsonProperty("identification_number")
    @NotBlank(message = "纳税人识别号【identification_number】不可为空")
    private String identificationNumber;

    /**
     * 纳税人类型 1 一般纳税人; 2 小规模纳税人
     */
    @JsonProperty("type")
    @NotNull(message = "纳税人类型【type】不可为空")
    private Integer type;

    /**
     * 开户行
     */
    @JsonProperty("bank_name")
    private String bankName;

    /**
     * 开户行银行账号
     */
    @JsonProperty("bank_code")
    private String bankCode;

    /**
     * 注册地址
     */
    @JsonProperty("address")
    private String address;

    /**
     * 固定电话
     */
    @JsonProperty("phone")
    private String phone;

    /**
     * 状态 1启用0停用，不传默认为启用
     */
    @JsonProperty("state")
    private Integer state=1;
}
