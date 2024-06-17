package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeJobConfigDTO;
import com.fenbeitong.openapi.sdk.dto.project.AddThirdProjectReqDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description 组织架构同步监听类接口
 * @Author duhui
 * @Date 2020-12-01
 **/
public interface IFxkProjectListener {

    /**
     * 项目同步数据前置处理
     *
     * @return List<OpenThirdOrgUnitDTO>
     */
    List<AddThirdProjectReqDTO> filterProjectBefore(FxiaokeJobConfigDTO fxiaokeJobConfigDTO, List<Map<String, Object>> dataList);

}
