package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;

import java.util.List;

/**
 * @Description 项目同步
 * @Author duhui
 * @Date 2021/7/12
 **/
public interface IFxkProjectService {

    /**
     * 项目全量同步
     */
    String syncProject(FxiaokeJobConfigDTO fxiaokeJobConfigDTO);

}
