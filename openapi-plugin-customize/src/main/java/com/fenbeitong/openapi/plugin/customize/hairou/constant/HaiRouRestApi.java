package com.fenbeitong.openapi.plugin.customize.hairou.constant;

/**
 * @author :zhiqiang.zhang
 * @title: HaiRouRestApi
 * @projectName openapi-plugin
 * @description:
 * @date 2022/5/18
 */
public interface HaiRouRestApi {

    /**
     * 获取项目数量
     */
    String GET_PROJECT_COUNT = "/api/cube/restful/interface/getModeDataPageCount/getProjectCount";

    /**
     * 获取项目列表
     */
    String GET_PROJECT_LIST = "/api/cube/restful/interface/getModeDataPageList/getProjectList";
}
