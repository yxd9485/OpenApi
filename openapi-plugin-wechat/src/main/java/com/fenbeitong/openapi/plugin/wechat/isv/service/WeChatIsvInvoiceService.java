package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.invoice.api.model.third.InvoiceThirdChooseRespRpcVO;
import com.fenbeitong.openapi.plugin.wechat.common.service.WeChatInvoiceService;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.invoice.api.model.dto.ChooseInvoiceInfoRPCDTO;
import com.fenbeitong.invoice.api.model.dto.EmployeeInfoRPCDTO;
import com.fenbeitong.invoice.api.model.dto.WxChooseInvoiceReqRPCDTO;
import com.fenbeitong.invoice.api.service.IFbtInvoiceService;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.InvoiceInfoRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvInvoiceRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvInvoiceResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * 发票
 * Created by lizhen on 2020/5/26.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvInvoiceService {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private WeChatIsvHttpUtils wechatIsvHttpUtil;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;

    @DubboReference(check = false)
    private IFbtInvoiceService iFbtInvoiceService;

    @Autowired
    private WeChatInvoiceService weChatInvoiceService;

    /**
     * 查询微信发票信息
     *
     * @param cardId
     * @param encryptCode
     * @param corpId
     */
    public String getInvoiceInfo(String cardId, String encryptCode, String corpId) {
        String url = wechatHost + WeChatIsvConstant.GET_INVOICE_INFO_URL;
        WeChatIsvInvoiceRequest weChatIsvInvoiceRequest = new WeChatIsvInvoiceRequest();
        weChatIsvInvoiceRequest.setCardId(cardId);
        weChatIsvInvoiceRequest.setEncryptCode(encryptCode);
        String res = wechatIsvHttpUtil.postJsonWithAccessToken(url, JsonUtils.toJson(weChatIsvInvoiceRequest), corpId);
        WeChatIsvInvoiceResponse weChatIsvInvoiceResponse = (WeChatIsvInvoiceResponse) JsonUtils.toObj(res, WeChatIsvInvoiceResponse.class);
        if (weChatIsvInvoiceResponse == null || Optional.ofNullable(weChatIsvInvoiceResponse.getErrcode()).orElse(-1) != 0) {
            String msg = weChatIsvInvoiceResponse == null ? "" : weChatIsvInvoiceResponse.getErrmsg();
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_GET_INVOICE_INFO_FAILED), msg);
        }
        return res;
    }

    /**
     * 保存微信发票信息到fbt
     *
     * @param user
     * @param invoiceInfoRequest
     */
    public InvoiceThirdChooseRespRpcVO importWxChooseInvoice(UserComInfoVO user, InvoiceInfoRequest invoiceInfoRequest) {
        return weChatInvoiceService.importWxChooseInvoice( user , invoiceInfoRequest);
    }

    /**
     * 更新发票状态
     *
     * @param cardId
     * @param encryptCode
     * @param companyId
     */
    public WeChatIsvInvoiceResponse updateInvoiceStatus(String cardId, String encryptCode, String reimburseStatus, String companyId) {
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        String url = wechatHost + WeChatIsvConstant.UPDATE_INVOICE_STATUS_URL;
        WeChatIsvInvoiceRequest weChatIsvInvoiceRequest = new WeChatIsvInvoiceRequest();
        weChatIsvInvoiceRequest.setCardId(cardId);
        weChatIsvInvoiceRequest.setEncryptCode(encryptCode);
        weChatIsvInvoiceRequest.setReimburseStatus(reimburseStatus);
        String res = wechatIsvHttpUtil.postJsonWithAccessToken(url, JsonUtils.toJson(weChatIsvInvoiceRequest), weChatIsvCompany.getCorpId());
        WeChatIsvInvoiceResponse weChatIsvInvoiceResponse = (WeChatIsvInvoiceResponse) JsonUtils.toObj(res, WeChatIsvInvoiceResponse.class);
        if (weChatIsvInvoiceResponse == null || Optional.ofNullable(weChatIsvInvoiceResponse.getErrcode()).orElse(-1) != 0) {
            String msg = weChatIsvInvoiceResponse == null ? "" : weChatIsvInvoiceResponse.getErrmsg();
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_UPDATE_INVOICE_STATUS_FAILED), msg);
        }
        return weChatIsvInvoiceResponse;
    }

    /**
     * 获取微信发票状态
     *
     * @param cardId
     * @param encryptCode
     * @param companyId
     * @return
     */
    public String getInvoiceStatus(String cardId, String encryptCode, String companyId) {
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        String invoiceInfoRes = getInvoiceInfo(cardId, encryptCode, weChatIsvCompany.getCorpId());
        WeChatIsvInvoiceResponse weChatIsvInvoiceResponse = (WeChatIsvInvoiceResponse) JsonUtils.toObj(invoiceInfoRes, WeChatIsvInvoiceResponse.class);
        return weChatIsvInvoiceResponse.getUserInfo().getReimburseStatus();
    }

}
