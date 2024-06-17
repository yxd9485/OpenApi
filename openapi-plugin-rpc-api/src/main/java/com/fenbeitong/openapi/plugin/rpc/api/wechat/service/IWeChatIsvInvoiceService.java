package com.fenbeitong.openapi.plugin.rpc.api.wechat.service;

import com.fenbeitong.openapi.plugin.rpc.api.wechat.model.GetInvoiceInfoReqDTO;
import com.fenbeitong.openapi.plugin.rpc.api.wechat.model.InvoiceUpdateStateDTO;

import java.util.List;

/**
 * 微信isv发票服务
 *
 * @author lizhen
 * @date 2020/5/26
 */
public interface IWeChatIsvInvoiceService {
    /**
     * 更新发票状态，并返回处理成功的发票
     * @param invoiceUpdateStateDTO
     */
    List<InvoiceUpdateStateDTO> updateInvoiceStatus(List<InvoiceUpdateStateDTO> invoiceUpdateStateDTO);

    /**
     * 查询微信发票状态
     * @param getInvoiceInfoReqDTO
     * @return
     */
    String getInvoiceStatus(GetInvoiceInfoReqDTO getInvoiceInfoReqDTO);
}
