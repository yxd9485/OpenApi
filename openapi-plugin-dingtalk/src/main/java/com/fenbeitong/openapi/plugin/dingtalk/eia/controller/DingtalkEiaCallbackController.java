package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.fenbeitong.finhub.common.utils.NumericUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpAppService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkEiaCallbackService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.luastar.swift.base.json.JsonUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by lizhen on 2020/8/20.
 */
@RestController
@RequestMapping("/dingtalk/callback")
@Api(value = "回调", tags = "回调", description = "回调")
@Slf4j
public class DingtalkEiaCallbackController {


    @Value("${dingtalk.callback.token}")
    private String dingtalkCallbackToken;

    @Value("${dingtalk.callback.aeskey}")
    private String dingtalkCallbackAeskey;

    @Autowired
    private IDingtalkCorpAppService dingtalkCorpAppService;

    @Autowired
    private IDingtalkEiaCallbackService dingtalkEiaCallbackService;

    /**
     * 接收钉钉回调接口
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/receive")
    public Object receive(String signature, String timestamp, String nonce, String corpId, @RequestBody String body) {
        log.info("接收到钉钉回调事件： corpId:{},signature:{},timestamp:{},nonce:{}", corpId, signature, timestamp, nonce);
        /**查询出应用APP-key*/
        PluginCorpAppDefinition dingtalkCorpApp = dingtalkCorpAppService.getByCorpId(corpId);
        if (dingtalkCorpApp == null) {
            log.info("钉钉回调的企业不存在");
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED));
        }
        /**post数据包数据中的加密数据**/
        Map<String, String> jsonEncrypt = JsonUtils.toObj(body, Map.class);
        String encrypt = jsonEncrypt.get("encrypt");

        /**对encrypt进行解密**/
        DingTalkEncryptor dingTalkEncryptor = null;
        String plainText;
        try {
            dingTalkEncryptor = new DingTalkEncryptor(dingtalkCallbackToken,
                    dingtalkCallbackAeskey, dingtalkCorpApp.getThirdAppKey());
            plainText = dingTalkEncryptor.getDecryptMsg(signature, timestamp, nonce, encrypt);
        } catch (DingTalkEncryptException e) {
            log.error("钉钉回调数据解码错误", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR));
        }

        log.info("获取到回调数据{}", plainText);

        try {
            // 请求派发,入库进入任务队列，由任务系统处理
            dingtalkEiaCallbackService.dispatch(plainText);

            /**对返回信息进行加密**/
            long timeStampLong = Long.parseLong(timestamp);
            Map<String, String> responseMap = dingTalkEncryptor.getEncryptedMap("success", timeStampLong, nonce);
            // 返回成功信息给钉钉
            return responseMap;
        } catch (DingTalkEncryptException e) {
            log.error("加密返回信息失败", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR));
        }
    }
}
