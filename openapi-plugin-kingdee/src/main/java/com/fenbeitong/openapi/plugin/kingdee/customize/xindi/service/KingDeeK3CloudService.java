package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service;

import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;

import java.util.List;

/**
 * <p>Title: JinDie3kCloudService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-10-14 11:13
 */
public interface KingDeeK3CloudService {

    /**
     * 组织架构同步
     */

    String syncOrganization(String companyId);

    /**
     * 查询kingdee三方数据接口
     */

    List<List> findKingdeeListData(ViewReqDTO viewReqDTO);
}
