package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuEmployeeService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuOrganizationService;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.util.FeiShuEiaHttpUtils;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.exception.ArgumentException;

import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author lizhen
 * @date 2020/7/4
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaEmployeeService extends AbstractFeiShuEmployeeService {

    @Autowired
    FeiShuEiaEmployeeServiceHelper feiShuEiaEmployeeServiceHelper;

    @Autowired
    private PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;

    @Autowired
    private FeiShuEiaOrganizationService feiShuEiaOrganizationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuEiaHttpUtils;
    }

    @Override
    protected AbstractFeiShuOrganizationService getOrganizationService() {
        return feiShuEiaOrganizationService;
    }

    /**
     * 飞书内部应用
     *
     * @param companyId
     */
//    @Async
    public void syncFeiShuEiaOrgEmployee(String companyId) {
        feiShuEiaEmployeeServiceHelper.syncFeiShuEiaOrgEmployee(companyId);
    }


    public void syncThirdOrgManagers(String companyId) {
        log.info("【feishu eia】 syncThirdOrgManagers, 开始同步部门主管,companyId={}", companyId);
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if (!ObjectUtils.isEmpty(pluginCorpDefinition)) {
            super.syncThirdOrgManagers(companyId, pluginCorpDefinition.getThirdCorpId(), pluginCorpDefinition.getAppName());
        } else {
            throw new FinhubException(0, "企业:" + companyId + "dingtalk_corp 未配置");
        }
    }
}
