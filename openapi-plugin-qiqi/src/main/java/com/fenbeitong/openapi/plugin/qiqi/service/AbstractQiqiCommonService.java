package com.fenbeitong.openapi.plugin.qiqi.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.opensdk.BaseRequest;
import com.amazonaws.opensdk.SdkRequestConfig;
import com.amazonaws.opensdk.config.ConnectionConfiguration;
import com.amazonaws.opensdk.config.TimeoutConfiguration;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao.QiqiCorpInfoDao;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.q7link.openapi.Openapi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @ClassName AbstractQiqiCommonService
 * @Description 企企公共方法
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/14 上午10:53
 **/
@Component
@Slf4j
public class AbstractQiqiCommonService  extends AbstractEmployeeService {

    private static Openapi openapi;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private QiqiCorpInfoDao qiqiCorpInfoDao;

     public static SdkRequestConfig getSdkRequestConfig(BaseRequest request,String accessKeyId,String openId) {
        return getSdkRequestConfig(request,accessKeyId,openId,false);

    }

    /**
     * 获取sdk请求配置
     *
     * @param request    请求
     * @param ignoreWarn 是否忽略警告：true为忽略
     * @return SdkRequestConfig
     */
     static SdkRequestConfig getSdkRequestConfig(BaseRequest request, String accessKeyId,String openId,boolean ignoreWarn) {
        return getSdkRequestConfig(request,accessKeyId,openId,ignoreWarn, false);
    }

    /**
     * 获取sdk请求配置
     * <pre>
     *   支持设置忽略警告
     *   支持单据保存为草稿
     * </pre>
     *
     * @param request     请求
     * @param ignoreWarn  是否忽略警告：true为忽略
     * @param saveAsDraft 是否保存为草稿：true为保存为草稿
     * @return SdkRequestConfig
     */
    private static SdkRequestConfig getSdkRequestConfig(BaseRequest request,String accessKeyId,String openId,boolean ignoreWarn, boolean saveAsDraft) {
        return request.sdkRequestConfig().copyBuilder()
//            .customHeader("Authorization",sign)
            .customHeader("Content-Type", "application/json")
            .customHeader("Access-Key-Id", accessKeyId)
            .customHeader("Open-Id", openId)
            .customHeader("Ignore-Warn", String.valueOf(ignoreWarn))
            .customHeader("Save-As-Draft", String.valueOf(saveAsDraft))
            .build();
    }

    public static Openapi openapi(String accessKeyId,String secret) {
        int second = 1000;
        if (openapi == null) {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secret);
            openapi = Openapi.builder()
                .connectionConfiguration(new ConnectionConfiguration()
                    .maxConnections(100)
                    .connectionMaxIdleMillis(10 * second))
                .timeoutConfiguration(new TimeoutConfiguration()
                    .httpRequestTimeout(3000)
                    .totalExecutionTimeout(10000)
                    .socketTimeout(2000))
                .iamCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
        }
        return openapi;
    }

    public QiqiCorpInfo getCorpInfo(String companyId){
        if(StringUtils.isEmpty(companyId)){
            log.info("companyId为空");
            throw new OpenApiQiqiException(QiqiResponseCode.COMPANY_NOT_EXIST);
        }
        //校验企业是否存在
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        if (ObjectUtils.isEmpty(authDefinition)) {
            log.info("企业信息不存在,companyId:"+companyId);
            throw new OpenApiQiqiException(QiqiResponseCode.COMPANY_NOT_EXIST);
        }
        //查询企企的企业配置表，获取openid
        QiqiCorpInfo qiqiCorpInfo = qiqiCorpInfoDao.getCorpIdByCompanyId(companyId);
        if(ObjectUtils.isEmpty(qiqiCorpInfo)){
            throw new OpenApiQiqiException(QiqiResponseCode.COMPANY_SETTING_NOT_EXIST);
        }
        return qiqiCorpInfo;
    }
}
