package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.yiduijie.model.account.Account;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: YiDuiJieListAccountResp</p>
 * <p>Description: 易对接科目列表响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 6:37 PM
 */
@Data
public class YiDuiJieListAccountResp {

    @JsonProperty("body")
    private List<Account> accountList;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }
}
