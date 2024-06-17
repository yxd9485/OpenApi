package com.fenbeitong.openapi.plugin.moka.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.moka.dto.DepartmentRespDto;
import com.fenbeitong.openapi.plugin.moka.dto.EmployeeOtherResDto;
import com.fenbeitong.openapi.plugin.moka.dto.EmployeeRespDto;
import com.fenbeitong.openapi.plugin.moka.dto.MokaSysConfigDto;
import com.fenbeitong.openapi.plugin.moka.util.SignHelper;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * <p>Title: MokaServiceApi</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/10/20 10:31 上午
 */
@Service
@Slf4j
public class MokaServiceApi {
    @Autowired
    AuthDefinitionDao authDefinitionDao;
    @Autowired
    private ExceptionRemind exceptionRemind;

    /**
     * 获取全量部门信息
     */
    public DepartmentRespDto getAllDepartment(MokaSysConfigDto mokaSysConfigDto, String companyId) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("entCode", mokaSysConfigDto.getDepartment().getEntCode());
        paraMap.put("apiCode", mokaSysConfigDto.getDepartment().getApiCode());
        paraMap.put("userName", mokaSysConfigDto.getUserName());
        paraMap.put("timestamp", System.currentTimeMillis());
        paraMap.put("nonce", System.currentTimeMillis());
        paraMap.put("sign", SignHelper.getSignStr(paraMap));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", mokaSysConfigDto.getAuthorization());
        try {
            String result = RestHttpUtils.get(mokaSysConfigDto.getDepartment().getUrl(), headers, paraMap);
            DepartmentRespDto departmentRespDto = JsonUtils.toObj(result, DepartmentRespDto.class);
            if (ObjectUtils.isEmpty(departmentRespDto) || !"200".equals(departmentRespDto.getCode())) {
                throw new FinhubException(0, result);
            }
            return departmentRespDto;
        } catch (Exception e) {
            AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
            String msg = String.format("请求三方接口异常\n企业id：[%s]\n企业名称：[%s]\n请求url：[%s]\n请求参数：[%s]\n异常信息：[%s]", companyId, authDefinition.getAppName(), mokaSysConfigDto.getDepartment().getUrl(), JsonUtils.toJson(paraMap), e);
            exceptionRemind.remindDingTalk(msg);
            log.warn("", e);
        }
        return null;
    }

    /**
     * 获取全量人员信息
     */
    public EmployeeRespDto getAllEmployee(MokaSysConfigDto mokaSysConfigDto, String companyId) {
        TreeMap<String, Object> paraMap = new TreeMap<>();
        paraMap.put("entCode", mokaSysConfigDto.getEmlpoyee().getEntCode());
        paraMap.put("apiCode", mokaSysConfigDto.getEmlpoyee().getApiCode());
        paraMap.put("userName", mokaSysConfigDto.getUserName());
        paraMap.put("timestamp", System.currentTimeMillis());
        paraMap.put("nonce", System.currentTimeMillis());
        paraMap.put("sign", SignHelper.getSignStr(paraMap));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", mokaSysConfigDto.getAuthorization());
        try {
            String result = RestHttpUtils.get(mokaSysConfigDto.getEmlpoyee().getUrl(), headers, paraMap);
            EmployeeRespDto employeeRespDto = JsonUtils.toObj(result, EmployeeRespDto.class);
            if (ObjectUtils.isEmpty(employeeRespDto) || !"200".equals(employeeRespDto.getCode())) {
                throw new FinhubException(0, result);
            }
            return JsonUtils.toObj(result, EmployeeRespDto.class);
        } catch (Exception e) {
            AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
            String msg = String.format("请求三方接口异常\n企业id：[%s]\n企业名称：[%s]\n请求url：[%s]\n请求参数：[%s]\n异常信息：[%s]", companyId, authDefinition.getAppName(), mokaSysConfigDto.getEmlpoyee().getUrl(), JsonUtils.toJson(paraMap), e);
            exceptionRemind.remindDingTalk(msg);
            log.warn("", e);
        }
        return null;
    }

    /**
     * 待完善
     * 员工相关数据：提供员工的第三方oa系统的id，比如企业微信、钉钉、飞书等
     */
    public EmployeeOtherResDto getEmployeeOther(MokaSysConfigDto mokaSysConfigDto) {
        return null;
    }
}
