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
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.exception.ArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;

/**
 * @author lizhen
 * @date 2020/7/4
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaEmployeeServiceHelper extends AbstractFeiShuEmployeeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private FeiShuEiaHttpUtils feiShuEiaHttpUtils;

    @Autowired
    private FeiShuEiaOrganizationService feiShuEiaOrganizationService;


    @Override
    protected AbstractFeiShuHttpUtils getFeiShuHttpUtils() {
        return feiShuEiaHttpUtils;
    }

    @Override
    protected AbstractFeiShuOrganizationService getOrganizationService() {
        return feiShuEiaOrganizationService;
    }

    @Async
    public void syncFeiShuEiaOrgEmployee(String companyId) {
        log.info("【feishu eia】 syncFeiShuEiaOrgEmployee, 开始同步组织机构人员,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
                String corpId = pluginCorpDefinition.getThirdCorpId();
                String companyName = pluginCorpDefinition.getAppName();
                syncOrgEmployee(OpenType.FEISHU_EIA.getType(), corpId, companyId, companyName);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【feishu eia】 syncFeiShuEiaOrgEmployee, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }
}
