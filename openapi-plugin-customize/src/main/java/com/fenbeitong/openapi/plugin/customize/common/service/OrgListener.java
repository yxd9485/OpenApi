package com.fenbeitong.openapi.plugin.customize.common.service;

import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdEmployeeVo;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdOrgUnitVo;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description 组织架构同步监听类接口
 * @Author duhui
 * @Date 2020-12-01
 **/
public interface OrgListener {

    /**
     * 部门数据过滤监听前置处理
     *
     * @param openThirdOrgUnitDTOList 过滤的数据
     * @return List<OpenThirdOrgUnitDTO>
     */
    List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList, String companyId, String topId, String companyName);


    /**
     * 部门数据过滤监听后置处理
     *
     * @param openThirdOrgUnitDTOList 过滤的数据
     * @return List<OpenThirdOrgUnitDTO>
     */
    List<OpenThirdOrgUnitDTO> filterOpenThirdOrgUnitDtoAfter(List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList);

    /**
     * 人员数据过滤
     *
     * @param openThirdEmployeeDTOList 过滤的数据
     * @return List<OpenThirdEmployeeDTO>
     */
    List<OpenThirdEmployeeDTO> fileOpenThirdEmployeeDto(List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList);


    /**
     * 人员数据映射
     *
     * @param etlConfigId etl配置的ID
     * @param respData    需要映射的数据
     * @return OpenThirdEmployeeVo
     */
    OpenThirdEmployeeVo getEmployeeMaping(Long etlConfigId, String respData);


    /**
     * 部门数据映射
     *
     * @param etlConfigId etl配置的ID
     * @param respData    需要映射的数据
     * @return OpenThirdOrgUnitVo
     */
    OpenThirdOrgUnitVo getOrgMaping(Long etlConfigId, String respData);


    /**
     * 设置部门负责人
     *
     * @param companyId 公司ID
     * @param openType  每个企业对象的类型值 查询OpenType类
     */
    void setDepManage(String companyId, int openType);


    /**
     * 更新部门负责人到中间库
     */
    void updateDepManage(List<OpenThirdOrgUnitDTO> departmentList, String companyId, int openType);

    /**
     * 设置请求参数,用于获取token等
     *
     * @param map 请求参数
     */
    void setHead(Map<String, String> map,String companyId);


    /**
     * 设置签名等
     *
     * @param map 请求参数
     */
    void setBody(Map<String, String> map,String companyId);

}
