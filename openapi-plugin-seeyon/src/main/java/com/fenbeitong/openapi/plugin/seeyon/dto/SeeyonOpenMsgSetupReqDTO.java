package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotNull;

/**
 * Created by hanshuqi on 2020/05/12.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonOpenMsgSetupReqDTO {
    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 选项编码
     */
    @JsonProperty("item_code")
    private String itemCode;
    /**
     * 默认值
     */
    @JsonProperty("is_checked")
    private Integer isChecked;
    /**
     * 
     */
    @JsonProperty("int_val1")
    private Integer intVal1;
    /**
     * 
     */
    @JsonProperty("int_val2")
    private Integer intVal2;
    /**
     * 
     */
    @JsonProperty("int_val3")
    private Integer intVal3;
    /**
     * 
     */
    @JsonProperty("str_val1")
    private String strVal1;
    /**
     * 
     */
    @JsonProperty("str_val2")
    private String strVal2;
    /**
     * 
     */
    @JsonProperty("str_val3")
    private String strVal3;
}
