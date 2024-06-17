package com.fenbeitong.openapi.plugin.yiduijie.model.voucher;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: CreateVoucherByApplyReqDTO</p>
 * <p>Description: 申请单生成凭证请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 12:11 PM
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "申请单生成凭证", description = "申请单生成凭证请求对象")
public class CreateVoucherByApplyReqDTO implements Serializable {

    /**
     * 公司id
     */
    @NotBlank(message = "公司id(company_id)不可为空")
    @ApiModelProperty(value = "公司id", name = "company_id", required = true)
    private String companyId;

    /**
     * 批次id
     */
    @NotBlank(message = "批次id(batch_id)不可为空")
    @ApiModelProperty(value = "批次id", name = "batch_id", required = true)
    private String batchId;

    /**
     * 回调地址
     */
    @NotBlank(message = "回调地址(call_back_url)不可为空", groups = {CreateVoucherGroup.class})
    @ApiModelProperty(value = "回调地址", name = "call_back_url", required = true)
    private String callBackUrl;

    /**
     * 操作人
     */
    @NotBlank(message = "操作人(operator)不可为空")
    @ApiModelProperty(value = "操作人", name = "operator", required = true)
    private String operator;

    public interface CreateVoucherGroup {

    }

}
