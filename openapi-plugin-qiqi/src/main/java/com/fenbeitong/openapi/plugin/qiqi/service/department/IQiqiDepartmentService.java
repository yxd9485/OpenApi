package com.fenbeitong.openapi.plugin.qiqi.service.department;

import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCommonReqDetailDTO;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiDepartmentReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;

import java.util.List;

/**
 * @ClassName QiqiDepartmentService
 * @Description  企企部门数据同步
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/12 下午5:25
 **/

public interface IQiqiDepartmentService {

    /**
     * 全量拉取部门数据同步
     * @author helu
     * @date 2022/5/13 下午2:09
     * @param companyId 分贝通公司id
     * @return Object 同步部门数据返回
     */
    void syncQiqiOrgEmployee(String companyId) throws Exception;

    /**
     * 转换部门
     * @param departmentInfos 三方部门数据
     * @param companyId 分贝通公司id
     * @param companyName 客户公司名称
     * @return
     */
    List<OpenThirdOrgUnitDTO> departmentConvert(List<QiqiDepartmentReqDTO> departmentInfos, String companyId, String companyName);

    /**
     * 转换人员
     * @param userInfos 三方人员数据
     * @param corpId    三方openId
     * @param companyId 分贝通公司id
     * @return
     */
    List<OpenThirdEmployeeDTO> employeeConvert(List<QiqiEmployeeReqDTO> userInfos, String corpId, String companyId);

    /**
     * 树形参数封装
     * @return
     */
    List<QiqiCommonReqDetailDTO> getTreeParam();

}
