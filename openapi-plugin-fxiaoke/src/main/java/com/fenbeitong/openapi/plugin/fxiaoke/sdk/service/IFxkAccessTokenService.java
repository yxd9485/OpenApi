package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCorpAccessTokenRespDTO;

/**
 * <p>Title: IFxkAccessTokenService</p>
 * <p>Description: 纷享销客token服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 8:09 PM
 */
public interface IFxkAccessTokenService {

    /**
     * 获取纷享销客公司token
     *
     * @param req 请求参数
     * @return 纷享销客corpAccessToken
     */
    FxkGetCorpAccessTokenRespDTO getCorpAccessToken(FxkGetCorpAccessTokenReqDTO req);

    /**
     * 获取token
     *
     * @param corpId
     * @return
     */
    String getFxkCorpAccessTokenByCorpId(String corpId);

    /**
     * 清除token
     *
     * @param corpId
     */
    void clearCorpAccaessTokenByCorpId(String corpId);
}
