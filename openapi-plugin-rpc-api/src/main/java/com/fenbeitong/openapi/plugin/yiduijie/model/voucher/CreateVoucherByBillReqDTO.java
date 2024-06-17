package com.fenbeitong.openapi.plugin.yiduijie.model.voucher;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Title: CreateVoucherByBillReqDTO</p>
 * <p>Description: 账单生成凭证请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/06/10 12:11 PM
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVoucherByBillReqDTO implements Serializable {

    /**
     * 公司id
     */
    @NotBlank(message = "公司id(company_id)不可为空")
    private String companyId;

    /**
     * 批次id
     */
    @NotBlank(message = "批次id(batch_id)不可为空")
    private String batchId;

    /**
     * 回调地址
     */
    @NotBlank(message = "回调地址(call_back_url)不可为空")
    private String callBackUrl;

    /**
     * 操作人
     */
    @NotBlank(message = "操作人(operator)不可为空")
    private String operator;


}
