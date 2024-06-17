package com.fenbeitong.openapi.plugin.rpc.api.func.service;

import com.fenbeitong.openapi.plugin.rpc.api.func.model.EmployeeDefaultAuthDto;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: IEmployeeDefaultAuthService</p>
 * <p>Description: 员工默认权限</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/7/21 10:40 AM
 */
public interface IEmployeeDefaultAuthService {

    /**
     * 加载员工默认权限
     *
     * @param companyId 公司id
     * @return
     */
    List<EmployeeDefaultAuthDto> listEmployeeDefaultAuth(String companyId);


    /**
     * @param companyId   公司
     * @param templateMap 权限模板
     */
    void updateEmployeeAuthTemplate(String companyId, Map<Integer, String> templateMap);
}
