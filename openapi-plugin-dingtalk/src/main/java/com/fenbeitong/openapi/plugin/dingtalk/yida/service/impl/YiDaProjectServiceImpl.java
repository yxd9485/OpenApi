package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dao.DingtalkYidaCorpAppDao;
import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorpApp;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaFormService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaProjectService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.util.YiDaPostClientUtil;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.project.AddThirdProjectReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: YiDaApplyServiceImpl</p>
 * <p>Description: 易搭审批service</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 3:40 下午
 */
@Slf4j
@Component
public class YiDaProjectServiceImpl implements IYiDaProjectService {

    @Autowired
    private YiDaPostClientUtil yiDaPostClientUtil;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private IYiDaFormService yiDaFormService;

    @Autowired
    private OpenProjectService openProjectService;

    @Autowired
    private DingtalkYidaCorpAppDao dingtalkYidaCorpDao;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Override
    public void syncProject(String formId, String companyId) {
        if (ObjectUtils.isEmpty(formId) || ObjectUtils.isEmpty(companyId)) {
            return;
        }
        DingtalkYidaCorpApp dingtalkYidaCorpApp = dingtalkYidaCorpDao.getDingtalkYidaCorpAppByCompanyId(companyId);
        if (dingtalkYidaCorpApp == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.CORP_INALID);
        }

        List<Map<String, Object>> projectData = yiDaFormService.listFormDataByFormId(formId, dingtalkYidaCorpApp.getCorpId());
        log.info("易搭项目同步,companyId:{},数据{}", companyId, JsonUtils.toJson(projectData));
        List<AddThirdProjectReqDTO> addThirdProjectReqDTOList = projectScript(companyId, projectData);
        if (ObjectUtils.isEmpty(addThirdProjectReqDTOList)) {
            return;
        }
        // 查询全部项目
        ListThirdProjectRespDTO listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(companyId);
        // 项目绑定
        openProjectService.bindProject(listThirdProjectRespDTO, addThirdProjectReqDTOList, companyId);
        // 项目同步
        openProjectService.projectUpdateOrAddByEach(listThirdProjectRespDTO, addThirdProjectReqDTOList, companyId);
    }


    private List<AddThirdProjectReqDTO> projectScript(String companyId, List<Map<String, Object>> projectData) {
        OpenThirdScriptConfig tripConfig = openThirdScriptConfigDao.geConfig(companyId, EtlScriptType.PROJECT_SYNC.getType());
        if (ObjectUtils.isEmpty(tripConfig)) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("projectData", projectData);
            put("companyId", companyId);
            put("thirdEmployeeId", superAdminUtils.superAdminThirdEmployeeId(companyId));
        }};
        if (StringUtils.isNotBlank(tripConfig.getParamJson()) && JsonUtils.toObj(tripConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(tripConfig.getParamJson(), Map.class));
        }
        List<Map<String, Object>> result = (List<Map<String, Object>>) EtlUtils.execute(tripConfig.getScript(), params);
        String resultJson = JsonUtils.toJson(result);
        log.info("projectScript执行结果：{}", resultJson);
        List<AddThirdProjectReqDTO> addThirdProjectReqDTO = JsonUtils.toObj(resultJson, new TypeReference<List<AddThirdProjectReqDTO>>() {
        });
        return addThirdProjectReqDTO;
    }

}
