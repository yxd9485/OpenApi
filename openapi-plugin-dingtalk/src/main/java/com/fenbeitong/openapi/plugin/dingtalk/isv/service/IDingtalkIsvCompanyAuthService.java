package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvMarketOrderDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvTryOutDTO;

/**
 * 企业授权
 *
 * @author lizhen
 */
public interface IDingtalkIsvCompanyAuthService {
    /**
     * 保存suite_ticket
     *
     * @param suiteTicket
     */
    void saveSuiteTicket(String suiteTicket);

    /**
     * 获取suite_ticket
     * 读库+缓存的方式
     * @return
     */
    String getSuiteTicket();
    /**
     * 获取suite_access_token
     *
     * @return
     */
    String getSuiteAccessToken();

    /**
     * suiteAccessToken失效，清除redis重新获取
     */
    void clearSuiteAccessToken();

    /**
     * corpAccessToken失效，清除redis重新获取
     *
     * @param corpId
     */
    void clearCorpAccessToken(String corpId);

    /**
     * 获取企业access_token
     *
     * @param corpId
     * @return
     */
    String getCorpAccessTokenByCorpId(String corpId);

    /**
     * 企业授权
     *
     * @param corpId
     */
    void companyAuth(String corpId);

    /**
     * 企业或个人授权
     *
     * @param dingtalkIsvMarketOrderDTO
     */
    void authCompanyOrPerson(DingtalkIsvMarketOrderDTO dingtalkIsvMarketOrderDTO);

    /**
     * 权限变更
     *
     * @param corpId
     */
    void updateCompanyAuth(String corpId);

    /**
     * 取消授权
     *
     * @param corpId
     */
    void companyCancelAuth(String corpId);

    /**
     * 获取SSOToken
     *
     * @return
     */
    String getSSOToken();

    /**
     * ssoToken失效，清除redis重新获取
     */
    void clearSSOToken();

    /**
     * 购买套餐
     *
     * @param dingtalkIsvMarketOrderDTO
     */
    void companyChangeEditon(DingtalkIsvMarketOrderDTO dingtalkIsvMarketOrderDTO);

    /**
     *  激活应用
     * @param corpId
     */
    void activateSuite(String corpId);

    /**
     * 试用
     * @param dingtalkIsvTryOutDTO
     */
    void tryout(DingtalkIsvTryOutDTO dingtalkIsvTryOutDTO);
}
