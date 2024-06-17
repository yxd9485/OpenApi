package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>Title: AirOrderDetailReqDTO</p>
 * <p>Description: 机票订单详情请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/15 4:57 PM
 */
@Data
public class EimburseReqDTO {

    /**
     * 公司id
     */
    private String companyId;

    @NotBlank(message = "发票申请单号[applyCode]不可为空")
    @JsonProperty("applyCode")
    private String applyCode;

    @JsonProperty("pageIndex")
    private Integer pageIndex;

    @JsonProperty("pageSize")
    private Integer pageSize;
}
