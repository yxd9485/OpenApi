package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeJobConfigDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCustomDataListReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCustomDataListRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkProjectListener;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkProjectService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenTemplateConfigConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.sdk.dto.project.AddThirdProjectReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 项目同步
 * @Author duhui
 * @Date 2021/7/12
 **/
@ServiceAspect
@Service
@Slf4j
public class FxkProjectServiceImpl implements IFxkProjectService {
    @Autowired
    IFxkAccessTokenService iFxkAccessTokenService;
    @Autowired
    FxkCustomDataServiceImpl fxkCustomDataService;
    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;
    @Autowired
    OpenProjectService openProjectService;
    @Autowired
    FxkCommonApiService fxkCommonApiService;

    @Override
    public String syncProject(FxiaokeJobConfigDTO fxiaokeJobConfigDTO) {
        // 获取token
        String token = iFxkAccessTokenService.getFxkCorpAccessTokenByCorpId(fxiaokeJobConfigDTO.getCorpId());
        log.info("纷享销客项目同步,companyid:{},toke:{}", fxiaokeJobConfigDTO.getCompanyId(), token);
        // 递归查询数据
        FxkGetCustomDataListReqDTO reqDTO = new FxkGetCustomDataListReqDTO();
        reqDTO.setCorpAccessToken(token);
        reqDTO.setCorpId(fxiaokeJobConfigDTO.getCorpId());
        reqDTO.setCurrentOpenUserId(fxiaokeJobConfigDTO.getCurrentOpenUserId());
        FxkGetCustomDataListReqDTO.FxkGetCustomDataListCondition fxkGetCustomDataListCondition = JsonUtils.toObj(fxiaokeJobConfigDTO.getReqData(), FxkGetCustomDataListReqDTO.FxkGetCustomDataListCondition.class);
        reqDTO.setData(fxkGetCustomDataListCondition);
        List<Map<String, Object>> listMap = new ArrayList<>();
        fxkCommonApiService.getAllCustomData(reqDTO, listMap);
        // 监听处理数据映射
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(fxiaokeJobConfigDTO.getCompanyId(), OpenTemplateConfigConstant.TYPE.project, OpenType.FXIAOKE_EIA.getType());
        IFxkProjectListener iFxkProjectListener = getProjectLister(openTemplateConfig);
        List<AddThirdProjectReqDTO> addThirdProjectReqDTOList = iFxkProjectListener.filterProjectBefore(fxiaokeJobConfigDTO, listMap);
        log.info("纷享销客项目同步,companyid:{},转换后数据{}", fxiaokeJobConfigDTO.getCompanyId(), JsonUtils.toJson(addThirdProjectReqDTOList));
        // 查询全部项目
        ListThirdProjectRespDTO listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(fxiaokeJobConfigDTO.getCompanyId());
        // 项目绑定
        openProjectService.bindProject(listThirdProjectRespDTO, addThirdProjectReqDTOList, fxiaokeJobConfigDTO.getCompanyId());
        // 项目同步
        openProjectService.projectUpdateOrAddByEach(listThirdProjectRespDTO, addThirdProjectReqDTOList, fxiaokeJobConfigDTO.getCompanyId());
        return "success";
    }

    /**
     * 反射获取监听类
     */
    public IFxkProjectListener getProjectLister(OpenTemplateConfig openTemplateConfig) {
        String className = null;
        if (!ObjectUtils.isEmpty(openTemplateConfig) && !ObjectUtils.isEmpty(openTemplateConfig.getListenerClass())) {
            className = openTemplateConfig.getListenerClass();
        }
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof IFxkProjectListener) {
                    return ((IFxkProjectListener) bean);
                }
            }
        }
        return SpringUtils.getBean(DefaultProjectListener.class);
    }


}
