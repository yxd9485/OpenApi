package com.fenbeitong.openapi.plugin.dingtalk.eia.service;


import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;

import java.util.List;

import static com.dingtalk.api.response.OapiDepartmentListResponse.Department;

/**
 * <p>Title: IDingtalkDepartmentApiService</p>
 * <p>Description: 钉钉部门服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 12:05 PM
 */
public interface IApiDepartmentService {

    /**
     * 获取钉钉部门列表
     *
     * @param corpId 钉钉企业id
     * @return 部门列表
     */
    List<Department> listDepartment(String corpId);


    /**
     * 获取钉钉部门详细信息
     */
    OapiDepartmentGetResponse getDepartmentInfo(String accessToken, String proxyUrl, String depId);

    /**
     * 查询钉钉部门的所有上级部门ID，按照级别次序依次排序
     * 假设部门的组织结构如下：
     * 1
     * |->123
     * |->456
     * |->789
     * 当传入部门id为789时，返回的结果按顺序依次为当前部门id及其所有父部门的ID，直到根部门，如[789,456,123,1]
     *
     * @param departmentId 钉钉部门ID
     * @param corpId       corpId
     * @return 父部门ID列表
     */
    List<Long> listParentDeptIds(Long departmentId, String corpId);


    /**
     * 根据公司性质过滤部门数据
     *
     * @param corpId
     * @return
     */
    List<OapiDepartmentListResponse.Department> listDepartmentRemoveNegative(String corpId);

    /**
     * 调用钉钉接口，返回钉钉原始信息，不做异常处理
     *
     * @param departmentId departmentId
     * @param corpId       corpId
     * @return
     */
    OapiDepartmentGetResponse getWithOriginal(String departmentId, String corpId);

    /**
     * 根据权限查询所有部门
     *
     * @param authedDept  授权部门ids
     * @param thirdCorpId 第三方id
     * @param companyName     公司名称
     * @return
     */
    List<Department> getAllDepartmentsByAuth(List<Long> authedDept, String thirdCorpId, String companyName);
}
