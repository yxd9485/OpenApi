package com.fenbeitong.openapi.plugin.customize.common.service.impl;

import com.fenbeitong.openapi.plugin.customize.common.service.ProjectListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdProjectVo;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description 可配置化的组织架构监听
 * @Author duhui
 * @Date 2020-11-26
 **/
@ServiceAspect
@Service
public class DefaultCustomProjectListener implements ProjectListener {
    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;

    @Override
    public List<SupportUcThirdProjectReqDTO> fileOpenThirdEmployeeDto(List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTOList, String companyId) {
        return null;
    }

    @Override
    public OpenThirdProjectVo getProjectMaping(OpenCustomizeConfig openCustomizeConfig, String respData) {
        return null;
    }

    @Override
    public void setHead(Map<String, String> map, String companyId) {

    }

    @Override
    public void setBody(Map<String, String> map, String companyId) {

    }
}
