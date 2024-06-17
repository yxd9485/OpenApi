package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 付款方信息配置
 *
 * @author ctl
 * @date 2022/3/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayerMsgDTO implements Serializable {

    /**
     * 付款主体id
     */
    private String companyAccountId;

    /**
     * 开户行
     */
    private String showBankAccountName;
}
