package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.base;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.company.service.IOpenCompanySourceTypeService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

/**
 * <p>Title: DingtalkApplyService<p>
 * <p>Description: 钉钉申请单相关服务工具类<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/6/17 16:01
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkApplyService {
    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;
    @Autowired
    private IOpenCompanySourceTypeService openCompanySourceTypeService;

    /**
     * 查询需要推送钉钉的表单processCode
     *
     * @param companyId    公司id
     * @param callbackType 表单对应的回调类型
     * @return 表单processCode
     */
    public String getProcessCode(String companyId, int callbackType) {
        return Optional.ofNullable(getThirdApply(companyId, callbackType))
            .map(ThirdApplyDefinition::getThirdProcessCode)
            .orElse(null);
    }

    /**
     * 查询需要推送钉钉的表单processCode，未查到抛异常
     *
     * @param companyId    公司id
     * @param callbackType 表单对应的回调类型
     * @return 表单processCode
     */
    public String getProcessCodeWithException(String companyId, int callbackType) {
        ThirdApplyDefinition thirdApply = getThirdApply(companyId, callbackType);
        if (!Optional.ofNullable(thirdApply).map(ThirdApplyDefinition::getThirdProcessCode).isPresent()) {
            log.warn("未配置有效的反向审批模版，companyId:{},callbackType:{}", companyId, callbackType);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_UNREGISTER_APPLY_PROCESS_CODE);
        }
        return thirdApply.getThirdProcessCode();
    }


    /**
     * 查询需要推送钉钉的表单信息
     *
     * @param companyId    公司id
     * @param callbackType 表单对应的回调类型
     * @return 三方表单配置信息
     */
    public ThirdApplyDefinition getThirdApply(String companyId, int callbackType) {
        return thirdApplyDefinitionDao.getThirdApply(companyId, callbackType);
    }

    /**
     * 根据分贝通公司id，查询公司来源信息
     *
     * @param companyId 公司id
     * @return 公司来源信息
     */
    public OpenCompanySourceType getCompanySourceByCompanyIdWithException(String companyId) {
        OpenCompanySourceType companySourceType = openCompanySourceTypeService.getOpenCompanySourceByCompanyId(companyId);
        if (!Optional.ofNullable(companySourceType).map(OpenCompanySourceType::getThirdCompanyId).isPresent()) {
            log.warn("未查询到公司来源信息，companyId:{}", companyId);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_UNREGISTER_COMPANY);
        }
        return companySourceType;
    }

}
