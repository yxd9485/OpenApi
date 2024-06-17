package com.fenbeitong.openapi.plugin.zhongxin.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>Title:  CiticBankConfig</p>
 * <p>Description: 中银银行相关配置</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 下午4:42
 **/
@Data
@Component
public class CiticBankConfig {

    /**
     * 开放银行合作方APPID
     */
    @Value("${citic.isv.openMerCode}")
    private String openMerCode;

    /**
     * 开放银行合作方名称
     */
    @Value("${citic.isv.openMerName}")
    private String openMerName;

    /**
     * 商户业务类型
     */
    @Value("${citic.isv.openBusType}")
    private String openBusType;

    /**
     * 签名私钥
     */
    @Value("${citic.cipher.signKeyStr}")
    private String signKeyStr;

    /**
     * 解密私钥
     */
    @Value("${citic.cipher.decryptKeyStr}")
    private String decryptKeyStr;

    /**
     * 加密公钥
     */
    @Value("${citic.cipher.encryptKeyStr}")
    private String encryptKeyStr;

    /**
     * 验签公钥
     */
    @Value("${citic.cipher.verifyKeyStr}")
    private String verifyKeyStr;
}
