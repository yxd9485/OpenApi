package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCompanyInfoRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author lizhen
 * @date 2020/7/3
 */
@Slf4j
public abstract class AbstractFeiShuCompanyAuthService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;


    /**
     * 获取AppAccessToken
     *
     * @return
     */
    public abstract String getAppAccessToken();

    /**
     * 清除AppAccessToken
     */
    public abstract void clearAppAccessToken();
    /**
     * 获取TenantAccessToken
     *
     * @param corpId
     * @return
     */
    public abstract String getTenantAccessTokenByCorpId(String corpId);

    /**
     * 获取TenantAccessToken
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public abstract String getTenantAccessTokenByAppIdAndSecret(String appId ,String appSecret);

    /**
     * 清除TenantAccessToken
     * @param corpId
     */
    public abstract void clearTenantAccessToken(String corpId);


    /**
     * 获取AppAccessToken(内部应用）
     *
     * @return
     */
    public String getAppAccessToken(String corpId){
        return null;
    }

    /**
     * 清除AppAccessToken(内部应用）
     */
    public void clearAppAccessToken(String corpId) {
    }

    /**
     * 获取企业信息
     *
     * @param corpId
     * @return
     */
    protected String getCompanyName(String corpId) {
        String url = feishuHost + FeiShuConstant.TENANT_QUERY;
        try{
            String res = feiShuIsvHttpUtils.getWithTenantAccessToken(url, null, corpId);
            FeiShuCompanyInfoRespDTO feiShuCompanyInfoRespDTO = JsonUtils.toObj(res, FeiShuCompanyInfoRespDTO.class);
            if (feiShuCompanyInfoRespDTO == null || 0 != feiShuCompanyInfoRespDTO.getCode()) {
                //throw new OpenApiFeiShuException(FeiShuResponseCode.COMPANY_INFO_FAILED);
                log.info(" 查询企业名称失败 ，corpId={}", corpId);
                return corpId;
            }
            FeiShuCompanyInfoRespDTO.CompanyInfo companyInfo = feiShuCompanyInfoRespDTO.getData();
            if( companyInfo != null && companyInfo.getTenant() != null){
                return companyInfo.getTenant().getName();
            }
        }catch(Exception e){
            log.info(" 查询企业名称失败 ，发生异常={}", e.getMessage());
            log.info(" 查询企业名称失败 ，corpId={}", corpId);
        }

        return corpId;
    }

}
