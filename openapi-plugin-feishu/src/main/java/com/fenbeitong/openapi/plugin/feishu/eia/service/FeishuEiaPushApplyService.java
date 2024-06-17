package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCreateApprovalInstanceReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractFeiShuApprovalService;
import com.fenbeitong.openapi.plugin.feishu.common.service.AbstractPushApplyService;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.impl.CommonPluginCorpAppDefinitionService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @author xiaohai
 * @date 2022/07/04
 */
@Slf4j
@ServiceAspect
@Service
public class FeishuEiaPushApplyService extends AbstractPushApplyService {

    @Autowired
    private FeiShuEiaApprovalService feiShuEiaApprovalService;

    @Autowired
    private CommonPluginCorpAppDefinitionService commonPluginCorpAppDefinitionService;

    @Override
    protected AbstractFeiShuApprovalService getFeiShuApprovalService() {
        return feiShuEiaApprovalService;
    }

    @Override
    protected AbstractFeiShuApprovalService getFeiShuApprovalService(String thirdEmployeeId, FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO) {
        feiShuCreateInstanceReqDTO.setUserId( thirdEmployeeId );
        return feiShuEiaApprovalService;
    }

    @Override
    protected int getOpenType() {
        return OpenType.FEISHU_EIA.getType();
    }

    @Override
    protected String getCorpId( String companyId ){
        //查询企业授权信息
        PluginCorpDefinition pluginCorp = commonPluginCorpAppDefinitionService.getPluginCorpByCompanyId(companyId);
        if (pluginCorp == null) {
            log.info("【push信息】非飞书 eia企业,companyId:{}", companyId);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        return pluginCorp.getThirdCorpId();
    }
}
