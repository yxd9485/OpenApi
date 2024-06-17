package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

/**
 * <p>Title: IApiTokenService</p>
 * <p>Description: 钉钉Token服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 11:25 AM
 */
public interface IApiTokenService {

    /**
     * 获取钉钉token
     *
     * @param corpId 钉钉公司id
     * @return 钉钉token
     */
    String getAccessToken(String corpId);

    /**
     * 清除缓存中的token
     *
     * @param corpId
     */
    void clearCorpAccessToken(String corpId);
}
