package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service;


import com.fenbeitong.openapi.plugin.support.organization.entity.OpenLegalEntity;

import java.util.List;

/**
 * @ClassName IOpenLegalEntityService
 * @Description 法人主体元气林森定制化接口
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/6/13
 **/
public interface IOpenLegalEntityService {
    /**
     * 法人主体全量同步
     *
     * @param openLegalEntityDTOList 法人实体列表
     */
    void syncAllLegal(List<OpenLegalEntity> openLegalEntityDTOList);
}
