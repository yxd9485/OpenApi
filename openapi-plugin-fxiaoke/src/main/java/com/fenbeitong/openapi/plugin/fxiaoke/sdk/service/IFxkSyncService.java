package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;

import java.util.List;

/**
 * <p>Title: IFxkSync</p>
 * <p>Description: 数据同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-31 15:47
 */
public interface IFxkSyncService {

    /**
     * 组织架构同步
     */
    String syncOrganization(FxiaokeOrgConfigDTO fxiaokeOrgConfigDTO);

    /**
     * 获取部门全量信息
     */

    List<FxiaokeDepartmentRespDTO.DepartmentInfo> getAllDepartments(FxkGetCorpAccessTokenRespDTO fxkGetCorpAccessTokenRespDTO);

    /**
     * 获取人员增量信息
     */
    List<FxiaokePersonnelRespDTO.PersonnelInfo> getAllPersonnel(FxkGetCorpAccessTokenRespDTO fxkGetCorpAccessTokenRespDTO);

    /**
     * 获取人员信息
     * @param openUserId
     * @param corpId
     * @return
     */
    FxiaokeGetUserInfoRespDTO getUserByOpenUserId(String openUserId, String corpId);

    /**
     * 通过员工昵称获取人员信息
     * @param nickName
     * @param corpId
     * @return
     */
    FxiaokeGetByNickNameRespDTO getUserByNickName(String nickName, String corpId);
}
