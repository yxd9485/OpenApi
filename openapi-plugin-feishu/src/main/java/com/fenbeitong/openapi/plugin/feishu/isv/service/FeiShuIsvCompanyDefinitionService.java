package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.feishu.isv.dao.FeishuIsvCompanyDao;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

/**
 * 飞书企业def
 *
 * @author lizhen
 * @date 2020/6/1
 */
@ServiceAspect
@Service
public class FeiShuIsvCompanyDefinitionService {

    @Autowired
    private FeishuIsvCompanyDao feishuIsvCompanyDao;


    public FeishuIsvCompany getFeiShuIsvCompanyByCorpId(String corpId) {
        return feishuIsvCompanyDao.getFeiShuIsvCompanyByCorpId(corpId);
    }

    public FeishuIsvCompany getFeiShuIsvCompanyByCompanyId(String companyId) {
        return feishuIsvCompanyDao.getFeiShuIsvCompanyByCompanyId(companyId);
    }

    public void saveFeiShuIsvCompany(FeishuIsvCompany feishuIsvCompany) {
        feishuIsvCompanyDao.saveSelective(feishuIsvCompany);
    }

    public void updateFeiShuIsvCompany(FeishuIsvCompany feishuIsvCompany) {
        feishuIsvCompanyDao.updateById(feishuIsvCompany);
    }

    public List<FeishuIsvCompany> getFeiShuIsvAllCompany() {
        return feishuIsvCompanyDao.getFeiShuIsvAllCompany();
    }

    public List<FeishuIsvCompany> getFeiShuIsvByIdAndTime(List<String> companyIds,String createTimeBegin,String createTimeEnd) {
        return feishuIsvCompanyDao.getFeiShuIsvByIdAndTime(companyIds,createTimeBegin,createTimeEnd);
    }
}
