package com.fenbeitong.openapi.plugin.customize.common.service;

import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdProjectVo;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description 项目同步监听类接口
 * @Author duhui
 * @Date 2020-12-01
 **/
public interface ProjectListener {


    /**
     * 项目数据过滤
     *
     * @param addThirdProjectReqDTOList 过滤的数据
     * @return List<AddThirdProjectReqDTO>
     */
    List<SupportUcThirdProjectReqDTO> fileOpenThirdEmployeeDto(List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTOList, String companyId);


    /**
     * 项目数据映射
     *
     * @param openCustomizeConfig 配置信息
     * @param respData            需要映射的数据
     * @return AddThirdProjectReqDTO
     */
    OpenThirdProjectVo getProjectMaping(OpenCustomizeConfig openCustomizeConfig, String respData);


    /**
     * 设置请求参数,用于获取token等
     *
     * @param map 请求参数
     */
    void setHead(Map<String, String> map, String companyId);


    /**
     * 设置签名等
     *
     * @param map 请求参数
     */
    void setBody(Map<String, String> map, String companyId);

}
