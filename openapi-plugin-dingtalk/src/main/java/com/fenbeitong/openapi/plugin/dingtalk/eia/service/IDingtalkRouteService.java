package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkRoute;

/**
 * <p>Title: IDingtalkRouteService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 11:42 AM
 */
public interface IDingtalkRouteService {

    /**
     * 根据钉钉企业id获取对应的路由信息
     *
     * @param corpId 钉钉企业id
     * @return
     */
    DingtalkRoute getRouteByCorpId(String corpId);
}
