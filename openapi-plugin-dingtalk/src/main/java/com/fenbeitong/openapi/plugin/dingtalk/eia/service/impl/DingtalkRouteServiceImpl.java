package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.CacheConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dao.DingtalkRouteDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkRoute;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.concurrent.TimeUnit;

/**
 * <p>Title: DingtalkRouteServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 11:42 AM
 */
@ServiceAspect
@Service
public class DingtalkRouteServiceImpl implements IDingtalkRouteService {

    @Autowired
    private DingtalkRouteDao dingtalkRouteDao;

    private static Cache<String, DingtalkRoute> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(CacheConstant.DINGTALK_ROUTE_EXPIRED, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    /**
     * 根据钉钉企业ID获取路由信息
     *
     * @param corpId 钉钉企业id
     * @return
     */
    @Override
    public DingtalkRoute getRouteByCorpId(String corpId) {
        String tokenKey = StringUtils.formatString(CacheConstant.DINGTALK_ROUTE_KEY, corpId);
        DingtalkRoute route = cache.getIfPresent(tokenKey);
        if (route != null) {
            return route;
        }
        return dingtalkRouteDao.getRouteByCorpId(corpId);
    }

    /**
     * 失效缓存
     *
     * @param corpId corpId
     */
    public void invalidateCache(String corpId) {
        cache.invalidate(StringUtils.formatString(CacheConstant.DINGTALK_ROUTE_KEY, corpId));
    }
}
