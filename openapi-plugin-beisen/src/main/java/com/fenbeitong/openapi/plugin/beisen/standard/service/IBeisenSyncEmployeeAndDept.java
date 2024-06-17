package com.fenbeitong.openapi.plugin.beisen.standard.service;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenJobParamDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;

import java.util.List;

/**
 * <p>Title: IBeisenSyncEmployeeAndOrg<p>
 * <p>Description: 北森同步部门人员接口<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/9/12 13:52
 */
public interface IBeisenSyncEmployeeAndDept extends IBeisenSyncOrg {
    /**
     * 获取北森部门信息并转换
     *
     * @param jobParamDTO 定时任务参数
     * @param beisenCorp  北森公司配置
     * @return 第三方部门DTO对象列表
     */
    List<OpenThirdOrgUnitDTO> getDeptList(BeisenJobParamDTO jobParamDTO, BeisenCorp beisenCorp);

    /**
     * 获取北森人员信息并转换
     *
     * @param jobParamDTO 定时任务参数
     * @param beisenCorp  北森公司配置
     * @return 第三方人员DTO对象
     */
    List<OpenThirdEmployeeDTO> getEmployeeList(BeisenJobParamDTO jobParamDTO, BeisenCorp beisenCorp);


}
