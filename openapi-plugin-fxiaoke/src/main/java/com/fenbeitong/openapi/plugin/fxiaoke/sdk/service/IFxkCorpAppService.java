package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;

/**
 * @author lizhen
 * @date 2020/12/23
 */
public interface IFxkCorpAppService {

    FxiaokeCorpApp getFxkCorpAppByCorpId(String corpId);

    FxiaokeCorpApp getFxkCorpAppByAppId(String appId);
}
