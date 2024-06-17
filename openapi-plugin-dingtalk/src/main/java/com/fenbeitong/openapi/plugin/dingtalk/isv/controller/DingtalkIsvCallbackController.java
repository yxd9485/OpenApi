package com.fenbeitong.openapi.plugin.dingtalk.isv.controller;

import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.fenbeitong.finhub.common.utils.CheckUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCallbackService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 钉钉回调
 *
 * @author lizhen
 * @date 2020/7/9
 */
@Controller
@Slf4j
@RequestMapping("/dingtalk/isv/callback")
public class DingtalkIsvCallbackController {

    @Value("${dingtalk.isv.token}")
    private String token;

    @Value("${dingtalk.isv.aeskey}")
    private String aeskey;

    @Value("${dingtalk.isv.suitekey}")
    private String suitekey;

    @Autowired
    private IDingtalkIsvCallbackService dingtalkIsvCallbackService;
    /**
     * 接收钉钉回调接口
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/business")
    @ResponseBody
    public String receive(HttpServletRequest request, HttpServletResponse response) {
        String msgSignature = request.getParameter("signature");
        String timeStamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        log.info("接收到钉钉回调事件：signature:{},timestamp:{},nonce:{}", msgSignature, timeStamp, nonce);
        CheckUtils.create().addCheckEmpty(msgSignature, "signature 不能为空")
                .addCheckEmpty(timeStamp, "timestamp 不能为空")
                .addCheckEmpty(nonce, "nonce 不能为空")
                .check();
        /**post数据包数据中的加密数据**/
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        Map<String, String> jsonEncrypt = JsonUtils.toObj(requestBody, Map.class);
        String encrypt = jsonEncrypt.get("encrypt");
        /**对encrypt进行解密**/
        DingTalkEncryptor dingTalkEncryptor = null;
        String plainText;
        try {
            dingTalkEncryptor = new DingTalkEncryptor(token,
                    aeskey, suitekey);
            plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
        } catch (DingTalkEncryptException e) {
            log.error("钉钉回调数据解码错误", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR));
        }
        log.info("获取到回调数据{}", plainText);
        try {
            // 请求派发,入库进入任务队列，由任务系统处理
            dingtalkIsvCallbackService.dispatch(plainText);
            /**对返回信息进行加密**/
            long timeStampLong = Long.parseLong(timeStamp);
            Map<String, String> responseMap = dingTalkEncryptor.getEncryptedMap("success", timeStampLong, nonce);
            // 返回成功信息给钉钉
            return JsonUtils.toJson(responseMap);
        } catch (DingTalkEncryptException e) {
            log.error("加密返回信息失败", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR));
        }
    }
}
