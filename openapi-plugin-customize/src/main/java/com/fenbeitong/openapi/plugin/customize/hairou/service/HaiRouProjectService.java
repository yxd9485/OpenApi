package com.fenbeitong.openapi.plugin.customize.hairou.service;

import com.fenbeitong.openapi.plugin.customize.hairou.dto.HaiRouProjectJobConfigDTO;

/**
 * @author :zhiqiang.zhang
 * @title: ProjectService
 * @projectName openapi-plugin
 * @description: 分贝通过泛微OA提供的项目接口进行拉取 项目编码、项目名称、项目三方id
 * @date 2022/5/18
 */
public interface HaiRouProjectService {
    /**
     * 获取海柔项目数量
     *
     * @param jobConfig 接口调用参数
     * @return 获取项目数量
     */
    int getProjectCount(HaiRouProjectJobConfigDTO jobConfig);

    /**
     * 获取海柔项目列表
     *
     * @param jobConfig 接口调用参数
     */
    void getProjectListSync(HaiRouProjectJobConfigDTO jobConfig);
}
