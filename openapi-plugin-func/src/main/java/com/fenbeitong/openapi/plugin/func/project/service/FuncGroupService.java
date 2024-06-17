package com.fenbeitong.openapi.plugin.func.project.service;

import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.usercenter.api.model.dto.costcenter.CenterGroupDTO;

import java.util.List;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/10/22 下午2:19
 */
public interface FuncGroupService {

    //新增项目分组
    Object addGroup( ApiRequestBase apiRequest ) throws Exception ;

    //更新项目分组
    Object updateGroup( ApiRequestBase apiRequest ) throws Exception ;

    //删除项目分组
    void deleteGroup( ApiRequestBase apiRequest ) throws Exception ;

    //查询项目分组列表
    List<CenterGroupDTO> list(ApiRequestBase apiRequest ) throws Exception ;

}
