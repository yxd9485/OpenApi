package com.fenbeitong.openapi.plugin.landray.ekp.service;

import com.fenbeitong.openapi.plugin.landray.ekp.dto.LandaryEkpEmployeeDTO;
import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;

import java.util.List;

/**
 * 蓝凌EKP人员
 * Created by lizhen on 2021/1/27.
 */
public interface ILandrayEkpEmployeeService {

    /**
     * 获取全量人员
     *
     * @param openLandrayEkpConfig
     * @param beginTime
     * @return
     */
    List<LandaryEkpEmployeeDTO> getAllEmployee(OpenLandrayEkpConfig openLandrayEkpConfig, String beginTime);

    /**
     * 获取有效人员
     *
     * @param employeeList
     * @return
     */
    List<LandaryEkpEmployeeDTO> getAvailableEmployee(List<LandaryEkpEmployeeDTO> employeeList);

    /**
     * 获取无效人员
     *
     * @param employeeList
     * @return
     */
    List<LandaryEkpEmployeeDTO> getUnAvailableEmployee(List<LandaryEkpEmployeeDTO> employeeList);
}
