package com.fenbeitong.openapi.plugin.kingdee.common.service;

import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;

import java.util.List;

/**
 * 金蝶三方接口
 * @Auther zhang.peng
 * @Date 2021/6/3
 */
public interface KingDeeService {

    /**
     * 获取 token
     * @param jinDieConfigDTo 金蝶配置信息
     * @return token
     */
    String getToken(KingDeeConfigDTO jinDieConfigDTo);

    /**
     * 获取金蝶接口数据
     * @param viewReqDTO 金蝶表单信息
     * @param kingDeeUrlConfig 金蝶配置信息
     * @return
     */
    List<List> getData(ViewReqDTO viewReqDTO , OpenKingdeeUrlConfig kingDeeUrlConfig, String cookie);

    /**
     * 获取金蝶 cookie
     * @param kingDeeUrlConfig 金蝶配置信息
     * @return
     */
    String getCookie(OpenKingdeeUrlConfig kingDeeUrlConfig);

}
