package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fenbeitong.noc.api.service.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PublicPayBankOrderDetaiDTO extends BaseModel {

    /**
     * 订单相关信息
     */
    private BankOrderDetaiDTO orderDetail;
}
