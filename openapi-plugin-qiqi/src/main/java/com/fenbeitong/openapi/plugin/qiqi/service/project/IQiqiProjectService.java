package com.fenbeitong.openapi.plugin.qiqi.service.project;

import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCommonReqDetailDTO;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;

import java.util.List;


/**
 * @ClassName IQiqiProjectService
 * @Description 企企同步项目数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
public interface IQiqiProjectService {
    /**
     * 全量拉取项目数据
     * @param companyId
     * @throws Exception
     */
    void syncQiqiProject(String companyId) throws Exception;

    /**
     * 增量同步数据转换
     * @param companyId
     * @param qiqiProjectDTO
     * @return
     * @throws Exception
     */
    SupportUcThirdProjectReqDTO projectConvertForAdd(String companyId, QiqiProjectReqDTO qiqiProjectDTO) throws Exception;

    /**
     * 项目实体的树形参数封装
     * @return
     */
    List<QiqiCommonReqDetailDTO> getTreeParam();
}
