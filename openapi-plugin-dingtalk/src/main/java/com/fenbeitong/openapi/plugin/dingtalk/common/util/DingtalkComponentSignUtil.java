package com.fenbeitong.openapi.plugin.dingtalk.common.util;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by lizhen on 2021/3/16.
 */
@Component
public class DingtalkComponentSignUtil {

    @Value("${dingtalk.isv.apiSecret}")
    private String apiSecret;


    public void checkSign(HttpServletRequest request) {
        String timestamp = request.getHeader("x-ddpaas-signature-timestamp");
        String signature = request.getHeader("x-ddpaas-signature");
        String realSign = calcSignature(apiSecret, NumericUtils.obj2long(timestamp));
        if (!signature.equals(realSign)) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SIGN_ERROR);
        }
    }
    /**
     * 验签
     * @param apiSecret
     * @param ts
     * @return
     */
    private static String calcSignature(String apiSecret, long ts) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
            mac.init(key);
            return Base64.getEncoder().encodeToString(mac.doFinal(Long.toString(ts).getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SIGN_ERROR);
        }
    }


}
