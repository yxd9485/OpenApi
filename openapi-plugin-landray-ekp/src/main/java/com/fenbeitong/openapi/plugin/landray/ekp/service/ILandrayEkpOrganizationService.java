package com.fenbeitong.openapi.plugin.landray.ekp.service;

import com.fenbeitong.openapi.plugin.landray.ekp.dto.LandaryEkpDepartmentInfoDTO;
import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;

import java.util.List;

/**
 * Created by lizhen on 2021/1/27.
 */
public interface ILandrayEkpOrganizationService {

    /**
     * 获取蓝凌全量部门
     *
     * @param openLandrayEkpConfig
     */
    List<LandaryEkpDepartmentInfoDTO> getAllDepartment(OpenLandrayEkpConfig openLandrayEkpConfig, String beginTime);

    /**
     * 过滤有效的部门
     *
     * @param departmentList
     * @param companyId
     * @return
     */
    List<LandaryEkpDepartmentInfoDTO> getAvailableDepartment(List<LandaryEkpDepartmentInfoDTO> departmentList, String companyId);

    /**
     * 过滤无效的部门
     *
     * @param departmentList
     * @return
     */
    List<LandaryEkpDepartmentInfoDTO> getUnAvailableDepartment(List<LandaryEkpDepartmentInfoDTO> departmentList);
}
