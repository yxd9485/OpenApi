package com.fenbeitong.openapi.plugin.zhongxin.isv.util;

import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

/**
 * <p>Title: ZhongxinBankUtil </p>
 * <p>Description: 中信银行相关工具类</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 上午10:57
 **/
@Slf4j
public class ZhongxinBankUtil {

    /**
     * 获取加密后的字符串
     * @param businessJsonStr
     * @param publicKeyStr
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static String getEncodeStr(String businessJsonStr, String publicKeyStr, String privateKeyStr) throws Exception {
        JSONObject jsonObject = JSONObject.fromObject(businessJsonStr);
        //1.对报文中的key字段进行ASCII排序
        StringBuffer stringBuffer = CiticBankRSAUtil.sortJSONObject(jsonObject);
        businessJsonStr = stringBuffer.toString().replaceAll("\\}\\{", "\\},\\{");

        //2.使用私钥对排序后的明文进行签名
        String signData = CiticBankRSAUtil.sign(businessJsonStr, privateKeyStr);
        //生成16位随机字符串作为对称密钥AESKEY
        String aesKey = CiticBankAESUtil.getRandomAESKey();
        //使用aesKey加密明文报文
        byte[] encryptBusinessBytes= CiticBankAESUtil.encrypt(businessJsonStr, aesKey);
        //将byte数组转换为BASE64字符串
        String encryptBusiness = new String(CiticBase64Util.encode(encryptBusinessBytes),"UTF-8");

        //3.使用开放银行提供的加密公钥加密对称密钥AESKEY
        //读取成公钥实体
        PublicKey encryptPublicKey = CiticBankRSAUtil.getPublicKey(publicKeyStr);
        //加密对称密钥aeskey
        byte[] encryptKeyByte= CiticBankRSAUtil.encrypt((RSAKey) encryptPublicKey, aesKey.getBytes("UTF-8"));
        String encryptKeyString=new String(CiticBase64Util.encode(encryptKeyByte),"UTF-8");

        //4.组合后得到encryptBody报文
        String encryptBody=encryptBusiness+"@@"+encryptKeyString;
        //组装加密加签报文JSON字符串
        String str = "{\"sign\":\"" + signData + "\",\"encryptBody\":\"" + encryptBody + "\"}";
        return str;
    }

    /**
     * 获取明文
     * @param encryptBody
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static String getDecodeStr(String encryptBody, String privateKeyStr) throws Exception {
        //1.获取服务端返回的encryptBody，并用"@@"做分割：
        String[] encryptBodyArr=encryptBody.split("@@");

        //2.对encryptBodyArr[1]进行RSA解密，得到对称密钥AESKEY：
        //加载私钥生成私钥实体
        RSAPrivateKey privateKey = (RSAPrivateKey) CiticBankRSAUtil.getPrivateKey(privateKeyStr);
        //使用RSA算法解密encryptBodyArr[1]
        byte[] aeskeyBytes= CiticBankRSAUtil.decrypt(privateKey, CiticBase64Util.decode(encryptBodyArr[1].getBytes("UTF-8")));
        //得到对称密钥AESKEY
        String aeskey=new String(aeskeyBytes,aeskeyBytes.length-16,16,"UTF-8");
        log.info("解密出来的AES_KEY值为：{}", aeskey);

        //3.使用aeskey解密encryptBodyArr[0]得到明文报文：
        //Base64解密
        byte[] businessBytes=CiticBase64Util.decode(encryptBodyArr[0].getBytes("UTF-8"));
        //AES解密
        byte[] decryptBusinessBytes= CiticBankAESUtil.decrypt(businessBytes, aeskey);
        //将byte数组转换为明文字符串
        String decryptBusiness = new String(decryptBusinessBytes,"UTF-8");

        return decryptBusiness;
    }

    /**
     * 数据验签
     * @param decryptBusiness
     * @param publicKeyStr
     * @param signData
     * @return
     */
    public static boolean verify(String decryptBusiness, String publicKeyStr, String signData){
        //1.处理明文数据：
        //解密后的明文decryptBusiness，去掉换行符
        String businessJsonStr=decryptBusiness.replaceAll("\r|\n", "");
        //转化为jsonobject
        net.sf.json.JSONObject jsonObject = JSONObject.fromObject(businessJsonStr);
        //对报文中的key字段进行ASCII排序
        StringBuffer stringBuffer = CiticBankRSAUtil.sortJSONObject(jsonObject);
        businessJsonStr = stringBuffer.toString().replaceAll("\\}\\{", "\\},\\{");

        //2.验证签名：
        return CiticBankRSAUtil.verify(businessJsonStr,publicKeyStr, signData);
    }

    /**
     * 获取请求流水号
     * @return
     */
    public static String getFlowId(Date date){
        StringBuffer sb = new StringBuffer("FBTISV1012KF");
        sb.append(DateUtils.toStr(date, "yyyyMMddHHmmss"));
        sb.append(RandomUtils.randomNum(6));
        return sb.toString();
    }
}
