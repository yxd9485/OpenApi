package com.fenbeitong.openapi.plugin.definition.service;


import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.definition.dto.plugin.corp.CompanyEmailConfigRespDTO;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyEmailDTO;
import com.fenbeitong.usercenter.api.service.company.ICompanyInfoService;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 企业邮件通知配置
 * @author lizhen
 */
@ServiceAspect
@Service
public class CompanyEmailConfigService {

    @DubboReference(check = false)
    private ICompanyInfoService companyInfoService;

    /**
     * 查询UC邮件通知开关
     * @param companyId
     * @return
     */
    public CompanyEmailConfigRespDTO queryCompanyEmailConfig(String companyId) {
        Boolean mailNotify = companyInfoService.queryCompanyEmailConfig(companyId);
        return CompanyEmailConfigRespDTO.builder().mailNotify(mailNotify).build();
    }

    /**
     * 更新邮件通知开关
     * type=1关闭通知，type=3开启通知
     * @param companyId
     * @param type
     */
    public void updateCompanyEmailConfig(String companyId, Integer type) {
        CompanyEmailDTO companyEmailDTO = CompanyEmailDTO.builder().companyId(companyId).type(type).build();
        companyInfoService.updateCompanyEmailConfig(companyEmailDTO);
    }
}
