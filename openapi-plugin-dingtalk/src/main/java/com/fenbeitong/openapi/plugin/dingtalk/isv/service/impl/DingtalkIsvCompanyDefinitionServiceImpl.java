package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.DingtalkIsvCompanyDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 飞书企业def
 *
 * @author lizhen
 * @date 2020/6/1
 */
@ServiceAspect
@Service
public class DingtalkIsvCompanyDefinitionServiceImpl implements IDingtalkIsvCompanyDefinitionService {

    @Autowired
    private DingtalkIsvCompanyDao dingtalkIsvCompanyDao;


    @Override
    public DingtalkIsvCompany getDingtalkIsvCompanyByCorpId(String corpId) {
        return dingtalkIsvCompanyDao.getDingtalkIsvCompanyByCorpId(corpId);
    }

    @Override
    public DingtalkIsvCompany getDingtalkIsvCompanyByCompanyId(String companyId) {
        return dingtalkIsvCompanyDao.getDingtalkIsvCompanyByCompanyId(companyId);
    }

    @Override
    public void saveDingtalkIsvCompany(DingtalkIsvCompany dingtalkIsvCompany) {
        dingtalkIsvCompanyDao.saveSelective(dingtalkIsvCompany);
    }

    @Override
    public void updateDingtalkIsvCompany(DingtalkIsvCompany dingtalkIsvCompany) {
        dingtalkIsvCompanyDao.updateById(dingtalkIsvCompany);
    }

}
