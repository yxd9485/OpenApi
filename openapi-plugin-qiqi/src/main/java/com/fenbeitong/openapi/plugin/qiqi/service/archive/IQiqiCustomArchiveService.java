package com.fenbeitong.openapi.plugin.qiqi.service.archive;

import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCustomArchiveReqDTO;
import com.fenbeitong.openapi.plugin.support.archive.entity.OpenThirdCustomArchiveProject;

import java.util.List;

/**
 * @ClassName QiqiCustomArchiveService
 * @Description 企企同步自定义档案数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/18
 **/
public interface IQiqiCustomArchiveService {
    /**
     * @param companyId
     * @return Object 同步自定义档案数据返回
     * @description 全量拉取自定义档案数据同步
     * @author wangxd
     * @date 2022/05/18
     **/
    void syncQiqiCustomArchive(String companyId) throws Exception;

    /**
     * 自定义档案数据转换
     * @param customArchiveInfos 三方自定义档案数据
     * @param companyId 分贝通公司id
     * @return
     * @throws Exception
     */
    List<OpenThirdCustomArchiveProject> customArchiveConvert(List<QiqiCustomArchiveReqDTO> customArchiveInfos, String companyId) throws Exception;
}
