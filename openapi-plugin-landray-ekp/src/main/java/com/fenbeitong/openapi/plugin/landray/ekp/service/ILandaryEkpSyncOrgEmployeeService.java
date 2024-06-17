package com.fenbeitong.openapi.plugin.landray.ekp.service;

import com.fenbeitong.openapi.plugin.landray.ekp.dto.LandaryEkpDepartmentInfoDTO;

import java.util.List;

/**
 * Created by lizhen on 2021/1/27.
 */
public interface ILandaryEkpSyncOrgEmployeeService {
    void syncThirdOrgEmployee(String companyId);

    void syncThirdOrgEmployeeV2(String companyId, String rootId);

    List<LandaryEkpDepartmentInfoDTO> queryLandaryEkpDepartmentInfo(String companyId);
}
