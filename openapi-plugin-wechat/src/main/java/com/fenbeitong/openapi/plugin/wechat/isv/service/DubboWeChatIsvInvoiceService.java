package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.rpc.api.wechat.model.GetInvoiceInfoReqDTO;
import com.fenbeitong.openapi.plugin.rpc.api.wechat.model.InvoiceUpdateStateDTO;
import com.fenbeitong.openapi.plugin.rpc.api.wechat.service.IWeChatIsvInvoiceService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResultEntity;
import com.fenbeitong.openapi.plugin.wechat.common.handler.WechatExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhen on 2020/5/27.
 */
@Component
@DubboService(timeout = 30000)
@Slf4j
public class DubboWeChatIsvInvoiceService  implements IWeChatIsvInvoiceService {

    @Autowired WeChatIsvInvoiceService weChatIsvInvoiceService;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    protected void checkReqParam(List reqList) {
        reqList.forEach(req -> {
            OpenApiBindException bindException = ValidatorUtils.checkValid(req);
            if (bindException != null) {
                handlerDubboException(bindException);
            }
        });
    }


    protected void handlerDubboException(Exception e) {
        WechatResultEntity wechatResultEntity = WechatExceptionHandler.handlerException(e);
        throw new FinhubException(wechatResultEntity.getCode(), wechatResultEntity.getMsg());
    }

    @Override
    public List<InvoiceUpdateStateDTO> updateInvoiceStatus(List<InvoiceUpdateStateDTO> invoiceUpdateStateDTO) {
        checkReqParam(invoiceUpdateStateDTO);
        List<InvoiceUpdateStateDTO> failedInvoiceInfoList = new ArrayList<>();
        List<InvoiceUpdateStateDTO> successedInvoiceInfoList = new ArrayList<>();
        for (InvoiceUpdateStateDTO invoiceInfo : invoiceUpdateStateDTO) {
            try {
                weChatIsvInvoiceService.updateInvoiceStatus(invoiceInfo.getCardId(), invoiceInfo.getEncryptCode(), invoiceInfo.getReimburseStatus(), invoiceInfo.getCompanyId());
                successedInvoiceInfoList.add(invoiceInfo);
            } catch (Exception e) {
                log.warn("微信更新发票状态失败", e);
                failedInvoiceInfoList.add(invoiceInfo);
            }
        }
        if (failedInvoiceInfoList.size() > 0) {
            log.warn("微信更新发票状态失败,invoiceInfo={}", JsonUtils.toJson(failedInvoiceInfoList));
        }
        return successedInvoiceInfoList;
    }

    @Override
    public String getInvoiceStatus(GetInvoiceInfoReqDTO getInvoiceInfoReqDTO) {
        return weChatIsvInvoiceService.getInvoiceStatus(getInvoiceInfoReqDTO.getCardId(), getInvoiceInfoReqDTO.getEncryptCode(), getInvoiceInfoReqDTO.getCompanyId());
    }
}
