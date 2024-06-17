package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;

/**
 * @author lizhen
 */
public interface IDingtalkIsvCompanyDefinitionService {

    DingtalkIsvCompany getDingtalkIsvCompanyByCorpId(String corpId);

    DingtalkIsvCompany getDingtalkIsvCompanyByCompanyId(String companyId);

    void saveDingtalkIsvCompany(DingtalkIsvCompany dingtalkIsvCompany);

    void updateDingtalkIsvCompany(DingtalkIsvCompany dingtalkIsvCompany);
}
