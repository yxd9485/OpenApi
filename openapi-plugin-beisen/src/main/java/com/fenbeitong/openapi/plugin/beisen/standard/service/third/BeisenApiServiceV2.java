package com.fenbeitong.openapi.plugin.beisen.standard.service.third;

import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenConstant;
import com.fenbeitong.openapi.plugin.beisen.common.dto.*;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author duhui
 * @Date 2022/3/3
 **/
@ServiceAspect
@Service
@Slf4j
public class BeisenApiServiceV2 extends BeiSenApiGetToken {

    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    /**
     * 获取部门信息
     */
    public List<BeisenOrgListDTO.OrgDto> getOrgListData(BeisenParamConfig beisenParamConfig) {
        Integer pageIndex = 1;
        Integer pageSize = 300;
        OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(beisenParamConfig.getCompanyId(), EtlScriptType.DEPARTMENT_SYNC, 1);
        BeisenOrgListParam beisenOrgListParam = new BeisenOrgListParam();
        if (!ObjectUtils.isEmpty(departmentConfig)) {
            beisenOrgListParam = JsonUtils.toObj(departmentConfig.getParamJson(), BeisenOrgListParam.class);
        } else {
            beisenOrgListParam.setStartTime(START_DATE);
            beisenOrgListParam.setStopTime(DateUtils.toSimpleStr(new Date(), false));
            beisenOrgListParam.setWithDisabled(false);
            beisenOrgListParam.setColumns(new String[]{"name", "POIdOrgAdmin", "ShortName", "Status", "CreatedTime", "ModifiedTime", "personInCharge"});
        }
        List<BeisenOrgListDTO.OrgDto> data = new ArrayList<>();
        beisenOrgListParam.setPageSize(pageSize);
        Integer remainDateCount = 1;
        int count = 1;
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    beisenOrgListParam.setPageIndex(pageIndex);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    String result = RestHttpUtils.postJson(BeiSenConstant.org_list_url_new, httpHeaders, JsonUtils.toJson(beisenOrgListParam));
                    if (!StringUtils.isBlank(result)) {
                        if (!ObjectUtils.isEmpty(departmentConfig)) {
                            String finalResult = result;
                            result = EtlUtils.etlFilter(departmentConfig, new HashMap<String, Object>() {{
                                put("result", finalResult);
                            }});
                        }
                        BeisenOrgListDTO beisenOrgListDTO = JsonUtils.toObj(result, BeisenOrgListDTO.class);
                        data.addAll(beisenOrgListDTO.getData());
                        remainDateCount = beisenOrgListDTO.getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return data;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen getOrgListData error", e);
                count++;
            }
        }
        return data;
    }


    /**
     * 获取人员信息
     */
    public List<BeisenEmployeeListDTO.EmployeeDto> getEmployeeListData(BeisenParamConfig beisenParamConfig) {
        final String beisenTokenKey = MessageFormat.format(OPEN_PLUGIN_BEISEN_REDIS_KEY, beisenParamConfig.getCompanyId());
        Integer pageIndex = 1;
        Integer pageSize = 300;
        Integer remainDateCount = 1;
        List<BeisenEmployeeListDTO.EmployeeDto> data = new ArrayList<>();
        BeisenEmployeeListParam beisenEmployeeListParam = new BeisenEmployeeListParam();
        OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(beisenParamConfig.getCompanyId(), EtlScriptType.EMPLOYEE_SYNC, 1);
        if (!ObjectUtils.isEmpty(employeeConfig)) {
            beisenEmployeeListParam = JsonUtils.toObj(employeeConfig.getParamJson(), BeisenEmployeeListParam.class);
        } else {
            beisenEmployeeListParam.setStartTime(START_DATE);
            beisenEmployeeListParam.setStopTime(DateUtils.toSimpleStr(new Date(), false));
            beisenEmployeeListParam.setWithDisabled(false);
            // 0:内部员工、1:外部人员、2:实习生
            beisenEmployeeListParam.setEmployType(new int[]{0, 1, 2});
            // 1:待入职、2:试用、3:正式、4:调出、5:待调入、6:退休、8:离职、12:非正式
            beisenEmployeeListParam.setEmpStatus(new int[]{2, 3});
            beisenEmployeeListParam.setPageSize(pageSize);
            beisenEmployeeListParam.setColumns(new String[]{"Name", "Email", "IDNumber", "MobilePhone", "Gender", "ModifiedTime", "OIdDepartment", "EmployeeStatus"});
        }
        int count = 1;
        while (count <= BEISEN_TRY_COUNT) {
            try {
                while (remainDateCount > 0) {
                    beisenEmployeeListParam.setPageIndex(pageIndex);
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenParamConfig));
                    String result = RestHttpUtils.postJson(BeiSenConstant.employee_list_url_new, httpHeaders, JsonUtils.toJson(beisenEmployeeListParam));
                    if (!StringUtils.isBlank(result)) {
                        BeisenEmployeeListDTO beisenEmployeeListDTO = new BeisenEmployeeListDTO();
                        if (!ObjectUtils.isEmpty(employeeConfig)) {
                            String finalResult = result;
                            result = EtlUtils.etlFilter(employeeConfig, new HashMap<String, Object>() {{
                                put("result", finalResult);
                            }});
                        }
                        beisenEmployeeListDTO = JsonUtils.toObj(result, BeisenEmployeeListDTO.class);
                        data.addAll(beisenEmployeeListDTO.getData());
                        remainDateCount = beisenEmployeeListDTO.getTotal() - pageSize * pageIndex;
                        pageIndex++;
                    } else {
                        count++;
                    }
                }
                return data;
            } catch (Exception e) {
                if (e.getMessage().contains("401")) {
                    redisTemplate.delete(beisenTokenKey);
                }
                log.error("get beisen getEmployeeData error", e);
                count++;
            }
        }
        return data;
    }

}
