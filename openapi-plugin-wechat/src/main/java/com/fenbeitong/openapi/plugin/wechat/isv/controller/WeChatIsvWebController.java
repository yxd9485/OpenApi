package com.fenbeitong.openapi.plugin.wechat.isv.controller;

import com.fenbeitong.finhub.auth.annotation.FinhubRequiredAuth;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.invoice.api.model.third.InvoiceThirdChooseRespRpcVO;
import com.fenbeitong.openapi.plugin.core.util.ParseUcTokenUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.*;
import com.fenbeitong.openapi.plugin.wechat.isv.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * web前端业务
 * Created by lizhen on 2020/3/27.
 */
@Controller
@Slf4j
@RequestMapping("/wechat/isv/web")
public class WeChatIsvWebController {

    @Autowired
    private WeChatIsvWebService weChatIsvWebService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private WeChatIsvPullThirdOrgService weChatIsvPullThirdOrgService;

    @Autowired
    private WeChatIsvInvoiceService weChatIsvInvoiceService;

    @Autowired
    private WeChatIsvTransferService weChatIsvTransferService;

    @Autowired
    private WeChatIsvOpenPayService weChatIsvOpenPayService;

    /**
     * 获取token
     * @param request
     * @param response
     * @param companyId
     * @return
     * @throws Exception
     */
//    @RequestMapping("/getToken")
//    @ResponseBody
//    public Object getToken(HttpServletRequest request, HttpServletResponse response,
//                           @RequestParam(value = "company_id", required = true) String companyId) throws Exception {
//        return weChatIsvWebService.getToken(companyId);
//    }

    /**
     * 获取企业Jsapi签名
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getEnterpriseJsapiSign")
    @ResponseBody
    public Object getEnterpriseJsapiSign(HttpServletRequest request, HttpServletResponse response,
                                         @Valid JsapiSignRequest jsapiSignRequest) throws Exception {
        WeChatIsvJsapiSignResponse enterpriseJsapiSign = weChatIsvWebService.getEnterpriseJsapiSign(jsapiSignRequest.getCompanyId(), jsapiSignRequest.getData());
        return WechatResponseUtils.success(enterpriseJsapiSign);
    }

    /**
     * 获取应用Jsapi签名
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getAgentJsapiSign")
    @ResponseBody
    public Object getAgentJsapiSign(HttpServletRequest request, HttpServletResponse response,
                                    @Valid JsapiSignRequest jsapiSignRequest) throws Exception {
        if ("INVOICE".equals(jsapiSignRequest.getData())) {
            WeChatIsvJsapiSignResponse invoiceSign = weChatIsvWebService.getInvoiceSign(jsapiSignRequest.getCompanyId(), jsapiSignRequest.getData());
            return WechatResponseUtils.success(invoiceSign);
        }
        WeChatIsvJsapiSignResponse agentJsapiSign = weChatIsvWebService.getAgentJsapiSign(jsapiSignRequest.getCompanyId(), jsapiSignRequest.getData());
        return WechatResponseUtils.success(agentJsapiSign);
    }

    /**
     * 获取企业微信isv试用天数
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getTrialDay")
    @ResponseBody
    public Object getTrialDay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return weChatIsvWebService.getTrialDay();
    }

    /**
     * 同步微信isv组织人员
     *
     * @param request
     * @param response
     * @param companyId
     * @return
     * @throws Exception
     */
    @RequestMapping("/pullThirdOrgService")
    @ResponseBody
    public Object pullThirdOrgService(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam(value = "company_id", required = true) String companyId) throws Exception {
        weChatIsvPullThirdOrgService.pullThirdOrgByCompanyId(companyId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        return result;
    }

    @RequestMapping("/getEmployeeAndOrgUnitThirdIds")
    @ResponseBody
    @FinhubRequiredAuth
    public Object getEmployeeAndOrgUnitThirdIds(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody EmployeeAndOrgUnitThirdIdsRequest employeeAndOrgUnitThirdIdsRequest) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        EmployeeAndOrgUnitThirdIdsResponse employeeAndOrgUnitThirdIds = weChatIsvWebService.getEmployeeAndOrgUnitThirdIds(userInfo, employeeAndOrgUnitThirdIdsRequest);
        return WechatResponseUtils.success(employeeAndOrgUnitThirdIds);
    }

    @RequestMapping("/importWxChooseInvoice")
    @ResponseBody
    @FinhubRequiredAuth
    public Object importWxChooseInvoice(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody InvoiceInfoRequest invoiceInfoRequest) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        InvoiceThirdChooseRespRpcVO invoiceThirdChooseRespRpcVO = weChatIsvInvoiceService.importWxChooseInvoice(userInfo, invoiceInfoRequest);
        return WechatResponseUtils.success(invoiceThirdChooseRespRpcVO);
    }

    @RequestMapping("/searchContact")
    @ResponseBody
    @FinhubRequiredAuth
    public Object searchContact(HttpServletRequest request, HttpServletResponse response, @RequestParam String word, @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        EmployeeAndOrgUnitThirdIdsResponse weChatIsvSearchContactResponse = weChatIsvTransferService.searchContact(userInfo, word, offset, limit);
        return WechatResponseUtils.success(weChatIsvSearchContactResponse);
    }

    @RequestMapping("/getPreAuthUrl")
    @ResponseBody
    public Object getPreAuthUrl(HttpServletRequest request, HttpServletResponse response, String redirect_uri) throws Exception {
        String preAuthUrl = weChatIsvWebService.getPreAuthUrl(redirect_uri);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Location", preAuthUrl);
        return new ResponseEntity(headers, HttpStatus.FOUND);
    }

    @RequestMapping("/getRegisterUrl")
    @ResponseBody
    public Object getRegisterUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String registerUrl = weChatIsvWebService.getRegisterUrl();
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Location", registerUrl);
        return new ResponseEntity(headers, HttpStatus.FOUND);
    }

    @RequestMapping("/recharge")
    @ResponseBody
    @FinhubRequiredAuth
    public Object recharge(HttpServletRequest request, HttpServletResponse response, @RequestParam String total_price) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        String redirctUrl = weChatIsvOpenPayService.recharge(userInfo, total_price);
        return WechatResponseUtils.success(redirctUrl);

    }

    @RequestMapping("/getOrderStatus")
    @ResponseBody
    @FinhubRequiredAuth
    public Object getOrderStatus(HttpServletRequest request, HttpServletResponse response, @RequestParam String order_id) throws Exception {
        UserComInfoVO userInfo = ParseUcTokenUtils.getUserInfo(request);
        Integer orderStatus = weChatIsvOpenPayService.getWeChatOrderStatus(userInfo, order_id);
        return WechatResponseUtils.success(orderStatus);
    }

}
