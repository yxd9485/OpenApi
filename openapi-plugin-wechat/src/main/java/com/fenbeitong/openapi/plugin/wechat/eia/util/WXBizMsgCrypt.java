/**
 * 对企业微信发送给企业后台的消息加解密示例代码.
 *
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 * <p>
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 * <p>
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 * <p>
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 */

// ------------------------------------------------------------------------

/**
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 */
package com.fenbeitong.openapi.plugin.wechat.eia.util;

import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.AesException;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.common.util.SHA1;
import com.fenbeitong.openapi.plugin.wechat.common.util.XMLParse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 提供接收和推送给企业微信消息的加解密接口(UTF8编码的字符串).
 * <ol>
 * 	<li>第三方回复加密消息给企业微信</li>
 * 	<li>第三方收到企业微信发送的消息，验证消息的安全性，并对消息进行解密。</li>
 * </ol>
 * 说明：异常java.security.InvalidKeyException:illegal Key Size的解决方案
 * <ol>
 * 	<li>在官方网站下载JCE无限制权限策略文件（JDK7的下载地址：
 *      http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 * 	<li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li>
 * 	<li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * 	<li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li>
 * </ol>
 */
@EnableAutoConfiguration
@Slf4j
@Component
public class WXBizMsgCrypt {
    static Charset CHARSET = Charset.forName("utf-8");
    Base64 base64 = new Base64();
    byte[] aesKey;
    String token;
    String receiveid;
//    @Value("${wechat.callback-tag}")
//    private String wechatCallbackTag;

    /**
     * 构造函数
     * @param token 企业微信后台，开发者设置的token
     * @param encodingAesKey 企业微信后台，开发者设置的EncodingAESKey
     * @param receiveid, 不同场景含义不同，详见文档
     *
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public WXBizMsgCrypt(String token, String encodingAesKey, String receiveid) throws AesException {
        if (encodingAesKey.length() != 43) {
            throw new AesException(AesException.IllegalAesKey);
        }

        this.token = token;
        this.receiveid = receiveid;
        aesKey = Base64.decodeBase64(encodingAesKey + "=");
    }

    public WXBizMsgCrypt() {
    }

    // 生成4个字节的网络字节序
    byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    // 还原4个字节的网络字节序
    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    // 随机生成16位字符串
    String getRandomStr() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     * @throws AesException aes加密失败
     */
    String encrypt(String randomStr, String text) throws AesException {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] receiveidBytes = receiveid.getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + receiveid
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(receiveidBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            String base64Encrypted = base64.encodeToString(encrypted);
            return base64Encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.EncryptAESError);
        }
    }

    /**
     * 对密文进行解密.
     *
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws AesException aes解密失败
     */
    public String decrypt(String text) {
        byte[] original =null;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            byte[] bytes = Arrays.copyOfRange(aesKey, 0, 16);
            log.info("bytes {} ",bytes);
            IvParameterSpec iv = new IvParameterSpec(bytes);
            try{
                cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);
            }catch (Exception e){
                log.info("cipher初始化异常 {}",e.getMessage());
                e.printStackTrace();
            }

            log.info("cipher初始化完成 ");
            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(text);
            // 解密
            original = cipher.doFinal(encrypted);
//            log.info("original {}",original);
        } catch (Exception e) {
            log.info("解析original异常 {}",e.getMessage());
            e.printStackTrace();
        }
        String xmlContent ="";
        String from_receiveid ="";
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);
            // 分离16位随机字符串,网络字节序和receiveid
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int xmlLength = recoverNetworkBytesOrder(networkOrder);
            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            log.info("解析具体xml数据 {}",xmlContent);
            from_receiveid = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length),
                    CHARSET);
        } catch (Exception e) {
            log.info("解析from_receiveid异常");
            e.printStackTrace();
        }
        // receiveid不相同的情况
        if (!from_receiveid.equals(receiveid)) {
            log.info("from_receiveidy与receiveid不同异常");
        }
        return xmlContent;

    }

    /**
     * 将企业微信回复用户的消息加密打包.
     * <ol>
     * 	<li>对要发送的消息进行AES-CBC加密</li>
     * 	<li>生成安全签名</li>
     * 	<li>将消息密文和安全签名打包成xml格式</li>
     * </ol>
     *
     * @param replyMsg 企业微信待回复用户的消息，xml格式的字符串
     * @param timeStamp 时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce 随机串，可以自己生成，也可以用URL参数的nonce
     *
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce, encrypt的xml格式的字符串
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String encryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        // 加密
        String encrypt = encrypt(getRandomStr(), replyMsg);

        // 生成安全签名
        if (timeStamp == "") {
            timeStamp = Long.toString(System.currentTimeMillis());
        }

        String signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt);

        // System.out.println("发送给平台的签名是: " + signature[1].toString());
        // 生成发送的xml
        String result = XMLParse.generate(encrypt, signature, timeStamp, nonce);
        return result;
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * 	<li>利用收到的密文生成安全签名，进行签名验证</li>
     * 	<li>若验证通过，则提取xml中的加密消息</li>
     * 	<li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param postData 密文，对应POST请求的数据
     *
     * @return 解密后的原文
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptMsg(String msgSignature, String timeStamp, String nonce, String postData)
            throws AesException {
//        TODO 后期进行不同环境的读取，临时为dev
//        String callBackTag = weChatPros.getCallBackTag();
//        String callBackTag="dev";
//        log.info("获取环境标识 ：{}",wechatCallbackTag);
//        String callBackTag = PropertyUtils.getString("qywx.open.callback.tag");
        // 密钥，公众账号的app secret
        // 提取密文
        Object[] encrypt = XMLParse.extract(postData);
        // 验证安全签名
        String signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt[1].toString());
        // 和URL中的签名比较是否相等
        // System.out.println("第三方收到URL中的签名：" + msg_sign);
//		 System.out.println("第三方校验签名 {}" + signature);
        log.info("第三方校验签名 {}", signature);
//        if (!(wechatCallbackTag.equals("dev") || wechatCallbackTag.equals("test"))) {
            if (!signature.equals(msgSignature)) {
                log.info("签名不相同 签名数据: {},验证安全签名: {}",signature,msgSignature );
                throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.SIGN_ERROR), "企微回调失败");
            }
//        }
        // 解密
        String result = decrypt(encrypt[1].toString());
        log.info("解密xml数据 {}", result);
        return result;
    }


    /**
     * 验证URL
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param echoStr 随机串，对应URL参数的echostr
     *
     * @return 解密之后的echostr
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String verifyURL(String msgSignature, String timeStamp, String nonce, String echoStr)
            throws AesException {
        String signature = SHA1.getSHA1(token, timeStamp, nonce, echoStr);
        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.ValidateSignatureError);
        }
        return decrypt(echoStr);
    }

    public static void main(String[] args) throws Exception {
        String aesKey="rgQ4GPwuxTtugjx6F7cVQdBDP4QPiu0bKYGk1zxHUIC";
        String token="OQ3RDJh2t3RW1YVH2KVnLKHIDWs3CIl6";
        String receiveid="ww6c3ee365000d8b58";
        String msgSignature="91fc5503b8e26cca289dbef0d3c133a2749a22a0";
        String timestamp="1563439218";
        String nonce="1563814677";
        String echostr="rnOqtqOMWmhTnFHWegMnNvaWE5ulgVpHC9E0JzXvdlFT/IiAonZ15c8ae+knTEvJ0AD0A5jlkKpF3tyNf2cN+A==";
        WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(token,aesKey,receiveid);
        String s ="";
        try{
//             s = wxBizMsgCrypt.VerifyURL(msgSignature, timestamp, nonce, echostr);
             String nullStr = "2";
            String substring = nullStr.substring(10);
        }catch (Exception e){
//            FinhubLogger.info("具体原因 {}",  e.getMessage());
            e.printStackTrace();
        }

        System.out.println(s);


    }

}
