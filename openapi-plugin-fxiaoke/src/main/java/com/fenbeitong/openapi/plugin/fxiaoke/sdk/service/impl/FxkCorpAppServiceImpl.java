package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseCode;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.exception.OpenApiFxkException;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeCorpAppDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkCorpAppService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;

/**
 * @author lizhen
 * @date 2020/12/23
 */
@Slf4j
@ServiceAspect
@Service
public class FxkCorpAppServiceImpl implements IFxkCorpAppService {

    @Autowired
    private FxiaokeCorpAppDao fxiaokeCorpAppDao;

    @Override
    public FxiaokeCorpApp getFxkCorpAppByCorpId(String corpId) {
        // 获取配置信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("corpId", corpId);
        FxiaokeCorpApp fxiaokeCorpApp = fxiaokeCorpAppDao.getFxiaokeCorpApp(map);
        if (fxiaokeCorpApp == null) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_CORP_UN_REGIST);
        }
        return fxiaokeCorpApp;
    }

    @Override
    public FxiaokeCorpApp getFxkCorpAppByAppId(String appId) {
        // 获取配置信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("appId", appId);
        FxiaokeCorpApp fxiaokeCorpApp = fxiaokeCorpAppDao.getFxiaokeCorpApp(map);
        if (fxiaokeCorpApp == null) {
            throw new OpenApiFxkException(FxkResponseCode.FXK_CORP_UN_REGIST);
        }
        return fxiaokeCorpApp;
    }

}
