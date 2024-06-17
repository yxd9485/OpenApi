package com.fenbeitong.openapi.plugin.kingdee.common.service;

import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeProjectDto;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;

/**
 * @Auther zhang.peng
 * @Date 2021/6/3
 */
public interface KingDeeProjectService {

    /**
     * 根据公司id获取本地项目信息
     * @param companyId 公司id
     * @return 本地项目信息
     */
    ListThirdProjectRespDTO getProjectByCompanyId(String companyId);

    /**
     * 向分贝通更新或添加项目信息
     * @param listThirdProjectRespDTO 原有项目信息
     * @param projectListDTo 三方项目信息
     * @param companyId 公司id
     * @return true 成功 ; false 失败
     */
    boolean addOrUpdateProjectInfo(ListThirdProjectRespDTO listThirdProjectRespDTO , KingDeeProjectDto projectListDTo , String companyId);
}
