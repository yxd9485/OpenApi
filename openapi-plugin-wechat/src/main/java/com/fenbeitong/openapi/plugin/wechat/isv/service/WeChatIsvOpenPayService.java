package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.noc.api.service.acct.model.dto.req.AcctCompanyInfoDTO;
import com.fenbeitong.noc.api.service.acct.model.dto.req.AcctOrderCreateReqRpcDTO;
import com.fenbeitong.noc.api.service.acct.model.dto.req.AcctPayCallbackReqDTO;
import com.fenbeitong.noc.api.service.acct.model.dto.resp.AcctOrderCreateResRpcDTO;
import com.fenbeitong.noc.api.service.acct.model.dto.resp.AcctOrderResDTO;
import com.fenbeitong.noc.api.service.acct.model.dto.resp.FbAcctPayCallbackResDTO;
import com.fenbeitong.noc.api.service.acct.service.IAcctOrderService;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvGetOrderRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvGetOrderResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvOpenPayRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvOpenPayResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author lizhen
 * @date 2020/9/25
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvOpenPayService {

    @Value("${wechat.api-host}")
    private String wechatHost;
    @Value("${host.fbtweb}")
    private String webHost;
    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private UserCenterService userCenterService;

    @DubboReference(check = false)
    private IAcctOrderService acctOrderService;

    @DubboReference(check = false)
    private ICommonService iCommonService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private WeChatIsvHttpUtils weChatIsvHttpUtils;

    /**
     * 充值
     *
     * @param user
     * @param totalPrice
     */
    public String recharge(UserComInfoVO user, String totalPrice) {
        if (user == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.TOKEN_INFO_IS_ERROR));
        }
        BigDecimal totalPriceBig = BigDecimalUtils.obj2big(totalPrice);
        if (totalPriceBig.compareTo(BigDecimal.ZERO) < 1) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_RECHARGE_PRICE_ERROR));
        }
        //人员分贝id转三方id
        //type 1：分贝id 2：第三方id, businessType 业务类型：1：部门 2：项目 3：员工
        List<CommonIdDTO> commonIdDTOS = iCommonService.queryIdDTO(user.getCompany_id(), Lists.newArrayList(user.getUser_id()), 1, 3);
        String employeeThirdId = commonIdDTOS.get(0).getThirdId();
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(user.getCompany_id());
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        String corpId = weChatIsvCompany.getCorpId();
        //1.创建分贝订单
        AcctOrderCreateResRpcDTO accOrder = createAccOrder(user, totalPriceBig);
        //2.调用微信下单接口
        WeChatIsvOpenPayResponse weChatIsvOpenPayResponse = openOrder(employeeThirdId, corpId, totalPriceBig, accOrder.getOrderId());
        String payToken = weChatIsvOpenPayResponse.getToken();
        String redirectUri = MessageFormat.format(WeChatIsvConstant.WECHAT_ISV_OPEN_PAY_REDIRECT_UL, accOrder.getOrderId());
        try {
            redirectUri = URLEncoder.encode(webHost + redirectUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        Map<String, Object> signParam = new HashMap<>();
        signParam.put("token", payToken);
        signParam.put("redirect_uri", redirectUri);
        String sign = sign(signParam);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
            payToken = URLEncoder.encode(payToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        String url = "https://open.work.weixin.qq.com/3rdapp/pay?token=" + payToken + "&redirect_uri=" + redirectUri + "&sig=" + sign;
        return url;
    }

    /**
     * 创建分贝订单
     *
     * @param user
     * @return
     */
    public AcctOrderCreateResRpcDTO createAccOrder(UserComInfoVO user, BigDecimal totalPrice) {
        AcctOrderCreateReqRpcDTO acctOrderCreateReqRpcDTO = new AcctOrderCreateReqRpcDTO();
        acctOrderCreateReqRpcDTO.setCompanyId(user.getCompany_id());
        acctOrderCreateReqRpcDTO.setUserName(user.getUser_name());
        acctOrderCreateReqRpcDTO.setUserPhone(user.getUser_phone());
        acctOrderCreateReqRpcDTO.setUserId(user.getUser_id());
        acctOrderCreateReqRpcDTO.setOrderName("充值");
        acctOrderCreateReqRpcDTO.setOrderSnapshot("充值");
        acctOrderCreateReqRpcDTO.setTotalPrice(totalPrice);
        AcctCompanyInfoDTO acctCompanyInfoDTO = new AcctCompanyInfoDTO();
        acctCompanyInfoDTO.setUserName(user.getUser_name());
        acctCompanyInfoDTO.setUserUnitId(user.getOrgUnit_list().get(0).getId());
        acctCompanyInfoDTO.setUserPhone(user.getUser_phone());
        acctCompanyInfoDTO.setUserUnitName(user.getOrgUnit_list().get(0).getName());
        acctCompanyInfoDTO.setCompanyName(user.getCompany_name());
        acctOrderCreateReqRpcDTO.setAcctCompanyInfoDTO(acctCompanyInfoDTO);
        AcctOrderCreateResRpcDTO accOrder = acctOrderService.createAccOrder(acctOrderCreateReqRpcDTO);
        return accOrder;
    }

    public WeChatIsvOpenPayResponse openOrder(String thirdEmployeeId, String corpId, BigDecimal totalPrice, String orderId) {
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        Long timestamp = System.currentTimeMillis() / 1000;
        WeChatIsvOpenPayRequest weChatIsvOpenPayRequest = new WeChatIsvOpenPayRequest();
        weChatIsvOpenPayRequest.setOrderid(orderId);
        weChatIsvOpenPayRequest.setBuyerCorpid(corpId);
        weChatIsvOpenPayRequest.setBuyerUserid(thirdEmployeeId);
        weChatIsvOpenPayRequest.setProductId("1");
        weChatIsvOpenPayRequest.setProductName("充值");
        weChatIsvOpenPayRequest.setProductDetail("充值");
        weChatIsvOpenPayRequest.setUnitName("次");
        weChatIsvOpenPayRequest.setUnitPrice(BigDecimalUtils.yuan2fen(totalPrice).longValue());
        weChatIsvOpenPayRequest.setNum(1);
        weChatIsvOpenPayRequest.setNonceStr(noncestr);
        weChatIsvOpenPayRequest.setTs(timestamp);
        HashMap<String, Object> signParam = JsonUtils.toObj(JsonUtils.toJson(weChatIsvOpenPayRequest), HashMap.class);
        String sign = sign(signParam);
        weChatIsvOpenPayRequest.setSig(sign);
        String url = wechatHost + "/cgi-bin/service/openpay/open_order?suite_access_token=";
        String res = weChatIsvHttpUtils.postJsonWithSuiteAccessToken(url, JsonUtils.toJson(weChatIsvOpenPayRequest));
        WeChatIsvOpenPayResponse weChatIsvOpenPayResponse = JsonUtils.toObj(res, WeChatIsvOpenPayResponse.class);
        if (weChatIsvOpenPayResponse == null || weChatIsvOpenPayResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_OPEN_PAY_FAILED));
        }
        return weChatIsvOpenPayResponse;
    }

    public WeChatIsvGetOrderResponse getOrder(String orderId) {
        String url = wechatHost + "/cgi-bin/service/openpay/get_order?suite_access_token=";
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        Long timestamp = System.currentTimeMillis() / 1000;
        WeChatIsvGetOrderRequest weChatIsvGetOrderRequest = new WeChatIsvGetOrderRequest();
        weChatIsvGetOrderRequest.setNonceStr(noncestr);
        weChatIsvGetOrderRequest.setTs(timestamp);
        weChatIsvGetOrderRequest.setOrderid(orderId);
        HashMap<String, Object> signParam = JsonUtils.toObj(JsonUtils.toJson(weChatIsvGetOrderRequest), HashMap.class);
        String sign = sign(signParam);
        weChatIsvGetOrderRequest.setSig(sign);
        String res = weChatIsvHttpUtils.postJsonWithSuiteAccessToken(url, JsonUtils.toJson(weChatIsvGetOrderRequest));
        WeChatIsvGetOrderResponse weChatIsvGetOrderResponse = JsonUtils.toObj(res, WeChatIsvGetOrderResponse.class);
        if (weChatIsvGetOrderResponse == null || weChatIsvGetOrderResponse.getErrcode() != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_GET_ORDER_FAILED));
        }
        return weChatIsvGetOrderResponse;
    }

    /**
     * 回调处理
     *
     * @param orderId
     */
    public TaskResult callbackOrder(String orderId) {
        WeChatIsvGetOrderResponse weChatIsvGetOrderResponse = getOrder(orderId);
        Integer orderStatus = weChatIsvGetOrderResponse.getOrderStatus();
        /**
         * 当前订单的订单状态，固定化，用于业务进行常规逻辑处理
         * order_status=0： 初始状态，未支付
         * order_status=1： 已经支付
         * order_status=2： 未支付，已关闭
         * order_status=3： 未支付且已过支付期限
         * order_status=4： 申请退款过程中，目前不可能出现此状态
         * order_status=5： 申请退款成功
         * order_status=6： 申请退款被拒绝。目前无此状态
         * order_status=7： 申请部分退款成功
         */
        if (orderStatus == 1) {
            AcctPayCallbackReqDTO acctPayCallbackReqDTO = new AcctPayCallbackReqDTO();
            acctPayCallbackReqDTO.setFbOrderId(orderId);
            FbAcctPayCallbackResDTO fbAcctPayCallbackResDTO = acctOrderService.payCallback(acctPayCallbackReqDTO);
            if (fbAcctPayCallbackResDTO.getCallResult() == 0) {
                return TaskResult.SUCCESS;
            }
        } else {
            log.info("订单未完成支付");
            return TaskResult.EXPIRED;
        }
        return TaskResult.FAIL;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        String noncestr = UUID.randomUUID().toString().replace("-", "");
//        System.out.println(noncestr.length());
//        Long timestamp = System.currentTimeMillis() / 1000;
//        WeChatIsvGetOrderRequest weChatIsvGetOrderRequest = new WeChatIsvGetOrderRequest();
//        weChatIsvGetOrderRequest.setNonceStr(noncestr);
//        weChatIsvGetOrderRequest.setTs(timestamp);
//        weChatIsvGetOrderRequest.setOrderid("OACC20200925");
//        HashMap hashMap = JsonUtils.toObj(JsonUtils.toJson(weChatIsvGetOrderRequest), HashMap.class);
//        String signContent = SignTool.getSignContent(hashMap, null);
//        String paySecret = "_Lpjzd0RO236eIytC9_3vpU2e988nDSkqUpTPAfMR64";
//        System.out.println(signContent);
//        String sig = SignTool.sha256HMAC(signContent, paySecret);
//        weChatIsvGetOrderRequest.setSig(sig);
//        System.out.println(JsonUtils.toJson(weChatIsvGetOrderRequest));
//        String thirdEmployeeId = "15311410634";
//        String corpId = "ww557cec61d4919573";
//        BigDecimal totalPrice = BigDecimal.valueOf(0.01);
//        String orderId = "OACC092520106022";
//        testOpenOrder(thirdEmployeeId, corpId, totalPrice, orderId);
        Map<String, Object> signParam = new HashMap<>();
        String uri = "https://fbt-dev.fenbeijinfu.com/weixin/recharge/success";
        //uri = Base64.getUrlEncoder().encodeToString(StringUtils.getBytesUtf8(uri));
        uri = URLEncoder.encode(uri, "UTF-8");
        //uri = "http%3a%2f%2ffbt-dev.fenbeijinfu.com%2faaa";
        String token = "o_VDTRJLVXPSayYl04jY7xw-hCfo90PR1SiNaiScfZY8V1gREQyxmyQkZku3lIVLkU1CK37iJc5tGhPGfrwncaw3WLGqphcOGMbZBuCpBEI";
        //token = URLEncoder.encode(token, "UTF-8");
        signParam.put("token", token);
        signParam.put("redirect_uri", uri);
        String signContent = SignTool.getSignContent(signParam, null);
        System.out.println(signContent);
        String sig = SignTool.sha256HMAC(signContent, "_Lpjzd0RO236eIytC9_3vpU2e988nDSkqUpTPAfMR64");
        sig = URLEncoder.encode(sig, "UTF-8");
        System.out.println("https://open.work.weixin.qq.com/3rdapp/pay?" + signContent + "&sig=" + sig);


    }

    /**
     * 获取分贝订单状态
     *
     * @param orderId
     * @return
     */
    public Integer getFbtOrderStatus(UserComInfoVO user, String orderId) {
        if (user == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.TOKEN_INFO_IS_ERROR));
        }
        try {
            AcctOrderResDTO order = acctOrderService.getOrderByOrderId(orderId);
            Integer orderStatus = order.getOrderStatus();
            return orderStatus;
        } catch (Exception e) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_GET_FBT_ORDER_FAILED));
        }
    }

    public Integer getWeChatOrderStatus(UserComInfoVO user, String orderId) {
        if (user == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.TOKEN_INFO_IS_ERROR));
        }
        WeChatIsvGetOrderResponse weChatIsvGetOrderResponse = getOrder(orderId);
        Integer orderStatus = weChatIsvGetOrderResponse.getOrderStatus();
        /**
         * 当前订单的订单状态，固定化，用于业务进行常规逻辑处理
         * order_status=0： 初始状态，未支付
         * order_status=1： 已经支付
         * order_status=2： 未支付，已关闭
         * order_status=3： 未支付且已过支付期限
         * order_status=4： 申请退款过程中，目前不可能出现此状态
         * order_status=5： 申请退款成功
         * order_status=6： 申请退款被拒绝。目前无此状态
         * order_status=7： 申请部分退款成功
         */
        if (orderStatus == 1) {
            /**
             * 前端订单状态 1待支付 2已支付 80已完成
             */
            orderStatus = 2;
        } else {
            orderStatus = 1;
        }
        return orderStatus;

    }

    public void testRecharge(UserComInfoVO user, String totalPrice) {
        if (user == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.TOKEN_INFO_IS_ERROR));
        }
        BigDecimal totalPriceBig = BigDecimalUtils.obj2big(totalPrice);
        if (totalPriceBig.compareTo(BigDecimal.ZERO) < 1) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_RECHARGE_PRICE_ERROR));
        }
        //人员分贝id转三方id
        //type 1：分贝id 2：第三方id, businessType 业务类型：1：部门 2：项目 3：员工
        List<CommonIdDTO> commonIdDTOS = iCommonService.queryIdDTO(user.getCompany_id(), Lists.newArrayList(user.getUser_id()), 1, 3);
        String employeeThirdId = commonIdDTOS.get(0).getThirdId();
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(user.getCompany_id());
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        String corpId = weChatIsvCompany.getCorpId();
        //1.创建分贝订单
        AcctOrderCreateResRpcDTO accOrder = createAccOrder(user, totalPriceBig);
        AcctPayCallbackReqDTO acctPayCallbackReqDTO = new AcctPayCallbackReqDTO();
        acctPayCallbackReqDTO.setFbOrderId(accOrder.getOrderId());
        FbAcctPayCallbackResDTO fbAcctPayCallbackResDTO = acctOrderService.payCallback(acctPayCallbackReqDTO);
        //getFbtOrderStatus(accOrder.getOrderId());

    }

    public static void testOpenOrder(String thirdEmployeeId, String corpId, BigDecimal totalPrice, String orderId) {
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        Long timestamp = System.currentTimeMillis() / 1000;
        WeChatIsvOpenPayRequest weChatIsvOpenPayRequest = new WeChatIsvOpenPayRequest();
        weChatIsvOpenPayRequest.setOrderid(orderId);
        weChatIsvOpenPayRequest.setBuyerCorpid(corpId);
        weChatIsvOpenPayRequest.setBuyerUserid(thirdEmployeeId);
        weChatIsvOpenPayRequest.setProductId("1");
        weChatIsvOpenPayRequest.setProductName("充值");
        weChatIsvOpenPayRequest.setProductDetail("充值");
        weChatIsvOpenPayRequest.setUnitName("次");
        weChatIsvOpenPayRequest.setUnitPrice(BigDecimalUtils.yuan2fen(totalPrice).longValue());
        weChatIsvOpenPayRequest.setNum(1);
        weChatIsvOpenPayRequest.setNonceStr(noncestr);
        weChatIsvOpenPayRequest.setTs(timestamp);
        String paySecret = "_Lpjzd0RO236eIytC9_3vpU2e988nDSkqUpTPAfMR64";
        HashMap hashMap = JsonUtils.toObj(JsonUtils.toJson(weChatIsvOpenPayRequest), HashMap.class);
        String signContent = SignTool.getSignContent(hashMap, null);
        System.out.println(signContent);
        String sig = SignTool.sha256HMAC(signContent, paySecret);
        weChatIsvOpenPayRequest.setSig(sig);
        System.out.println(JsonUtils.toJson(weChatIsvOpenPayRequest));
    }

    private String sign(Map<String, Object> param) {
        String signContent = SignTool.getSignContent(param, null);
//        String paySecret = "_Lpjzd0RO236eIytC9_3vpU2e988nDSkqUpTPAfMR64";
        log.info("signContent:{}", signContent);
        String paySecret = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WECHAT_ISV_PAY_SECRET.getCode());
        String sig = SignTool.sha256HMAC(signContent, paySecret);
        return sig;
    }

}
