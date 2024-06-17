package com.fenbeitong.openapi.plugin.customize.common.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.ProjectListener;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectServiceV2;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title: AbstractPrimaryProjectServiceImpl</p>
 * <p>Description: 项目同步 可配置化主类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
@Slf4j
@ServiceAspect
@Service
public class PrimaryProjectServiceImpl extends PrimaryCommonImpl {
    @Autowired
    OpenProjectServiceV2 openProjectServiceV2;

    public String syncProject(String companyId, Integer type, boolean isForceUpdate) {
        // 获取项目配置
        OpenCustomizeConfig openCustomizeConfig = getOpenCustomizeConfig(companyId, type);
        List<SupportUcThirdProjectReqDTO> supportUcThirdProjectReqDTOS = new ArrayList<>();
        if (!ObjectUtils.isEmpty(openCustomizeConfig)) {
            ProjectListener projectListener = getProjectListener(openCustomizeConfig);
            // 获取全量项目
            supportUcThirdProjectReqDTOS = getAllProject(companyId, openCustomizeConfig, projectListener);
        }
        // 监听处理前置
        log.info("项目数据{}", JsonUtils.toJson(supportUcThirdProjectReqDTOS));
        // 项目同步
        openProjectServiceV2.projectUpdateOrAddByEach(supportUcThirdProjectReqDTOS, companyId, isForceUpdate);
        return "success";
    }


}
