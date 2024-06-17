package com.fenbeitong.openapi.plugin.definition.service;

import com.fenbeitong.openapi.plugin.dingtalk.eia.dao.DingTalkRouteDefinitionDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingTalkRouteDefinition;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;

/**
 * 钉钉
 * Created by log.chang on 2019/12/24.
 */
@ServiceAspect
@Service
public class DingTalkRouteDefinitionService {

    @Autowired
    private DingTalkRouteDefinitionDao dingTalkRouteDefinitionDao;

    public void createDingTalkRouteDefinition(String corpId, String proxyUrl, String desc, Date now) {
        if (now == null)
            now = DateUtils.now();
        DingTalkRouteDefinition dingTalkRouteDefinition = DingTalkRouteDefinition.builder()
                .corpId(corpId)
                .proxyUrl(proxyUrl)
                .description(desc)
                .createTime(now)
                .updateTime(now)
                .build();
        dingTalkRouteDefinitionDao.saveSelective(dingTalkRouteDefinition);
    }

}
