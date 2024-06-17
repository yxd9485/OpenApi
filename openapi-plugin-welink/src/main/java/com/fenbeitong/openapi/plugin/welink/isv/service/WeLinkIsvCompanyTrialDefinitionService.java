package com.fenbeitong.openapi.plugin.welink.isv.service;

import com.fenbeitong.openapi.plugin.welink.isv.dao.WeLinkIsvCompanyTrialDao;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * Created by lizhen on 2020/4/14.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvCompanyTrialDefinitionService {

    @Autowired
    private WeLinkIsvCompanyTrialDao welinkIsvCompanyTrialDao;


    public WeLinkIsvCompanyTrial getWelinkIsvCompanyTrialByCorpId(String corpId) {
        return welinkIsvCompanyTrialDao.getWelinkIsvCompanyTrialByCorpId(corpId);
    }

    public WeLinkIsvCompanyTrial getWelinkIsvCompanyTrialByCompanyId(String companyId) {
        return welinkIsvCompanyTrialDao.getWelinkIsvCompanyTrialByCompanyId(companyId);
    }

    public void saveWeLinkIsvCompanyTrial (WeLinkIsvCompanyTrial weLinkIsvCompanyTrial) {
        welinkIsvCompanyTrialDao.saveSelective(weLinkIsvCompanyTrial);
    }

    public void updateWeLinkIsvCompanyTrial (WeLinkIsvCompanyTrial weLinkIsvCompanyTrial) {
        welinkIsvCompanyTrialDao.updateById(weLinkIsvCompanyTrial);
    }

}
