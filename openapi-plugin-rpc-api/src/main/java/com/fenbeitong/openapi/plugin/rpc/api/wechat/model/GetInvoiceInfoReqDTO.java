package com.fenbeitong.openapi.plugin.rpc.api.wechat.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @author lizhen
 * @date 2020/5/29
 */
@Data
public class GetInvoiceInfoReqDTO implements Serializable {

    @NotBlank(message = "企业id[companyId]不可为空")
    private String companyId;

    /**
     * 所选发票卡券的 cardId
     */
    @NotBlank(message = "所选发票卡券的[cardId]不可为空")
    private String cardId;
    /**
     * 所选发票卡券的加密 code ，报销方可以通过 cardId 和 encryptCode 获得报销发票的信息
     */
    @NotBlank(message = "所选发票卡券的加密code[encryptCode]不可为空")
    private String encryptCode;
}
