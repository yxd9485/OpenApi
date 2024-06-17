package com.fenbeitong.openapi.plugin.definition.controller.enforcement;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.plugin.corp.CompanyEmailConfigRespDTO;
import com.fenbeitong.openapi.plugin.definition.service.CompanyEmailConfigService;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业邮件通知开关配置
 *
 * @author lizhen
 */
@RestController
@RequestMapping("/definitions/company/email")
public class CompanyEmailConfigController {

    @Autowired
    private CompanyEmailConfigService companyEmailConfigService;

    /**
     * 查询UC邮件通知开关
     *
     * @param companyId
     * @return
     */
    @RequestMapping("/queryCompanyEmailConfig")
    public Object queryCompanyEmailConfig(@RequestParam("companyId") String companyId) {
        CompanyEmailConfigRespDTO companyEmailConfigRespDTO =
            companyEmailConfigService.queryCompanyEmailConfig(companyId);
        return DefinitionResultDTO.success(companyEmailConfigRespDTO);
    }

    /**
     * 更新邮件通知开关
     * type=1关闭通知，type=3开启通知
     *
     * @return
     */
    @RequestMapping("/updateCompanyEmailConfig")
    public Object updateCompanyEmailConfig(@RequestParam("companyId") String companyId,
        @RequestParam("type") Integer type) {
        companyEmailConfigService.updateCompanyEmailConfig(companyId, type);
        return DefinitionResultDTO.success(Maps.newHashMap());
    }
}
