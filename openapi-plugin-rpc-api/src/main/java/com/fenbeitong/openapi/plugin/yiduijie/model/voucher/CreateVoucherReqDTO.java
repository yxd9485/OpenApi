package com.fenbeitong.openapi.plugin.yiduijie.model.voucher;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>Title: CreateVoucherReqDTO</p>
 * <p>Description: 生成凭证请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/3/3 12:11 PM
 */
@Data
public class CreateVoucherReqDTO implements Serializable {

    /**
     * 凭证类型 1:虚拟卡凭证 2:账单凭证;3:对公付款凭证;100:已生成底表凭证
     */
    @NotNull(message = "凭证业务类型(business_type)不可为空")
    private Integer businessType;

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
    @NotBlank(message = "回调地址(call_back_url)不可为空", groups = {CreateVoucherGroup.class})
    private String callBackUrl;

    /**
     * 操作人名称
     */
    @NotBlank(message = "操作人名称(operator)不可为空")
    private String operator;

    /**
     * 操作人id
     */
    @NotBlank(message = "操作人Id(operator_id)不可为空")
    private String operatorId;

    public interface CreateVoucherGroup {

    }

}
