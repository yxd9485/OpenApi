package com.fenbeitong.openapi.plugin.customize.wantai.service;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResultEntity;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.DepartmentSyncType;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.DepartmentRequestDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.TigerDepartmentRespDTO;
import com.fenbeitong.openapi.plugin.support.finance.dto.OptBusinessDeptDTO;
import com.fenbeitong.openapi.plugin.support.finance.dto.OptFinanceDeptCreateOrUpdateReqDTO;
import com.fenbeitong.openapi.plugin.support.finance.dto.OptFinanceDeptDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class WanTaiDepartmentServicce {


    @Value("${host.tiger}")
    private String tigerHost;

    public OpenapiResultEntity sync(DepartmentRequestDTO departmentRequest) {
        Integer type = departmentRequest.getType();
        if (DepartmentSyncType.CREATE.getKey().equals(type) || DepartmentSyncType.UPDATE.getKey().equals(type)) {
            //行政部门+自定义字段
            List<TigerDepartmentRespDTO.TigerDepartmentsResp> departmentResp =
                createOrUpdateDepartment(departmentRequest);
            //部门主管
            List<TigerDepartmentRespDTO.TigerDepartmentsResp> managerResp =
                setDepartmentManager(departmentRequest);
            //同步财务部门
            List<TigerDepartmentRespDTO.TigerDepartmentsResp> financeResp =
                createFinanceDepartment(departmentRequest);
            return buildResult(departmentResp, managerResp, financeResp);
        } else if (DepartmentSyncType.DELETE.getKey().equals(type)) {
            List<TigerDepartmentRespDTO.TigerDepartmentsResp> tigerDepartmentsResps =
                deleteDepartment(departmentRequest);
            return ObjectUtils.isEmpty(tigerDepartmentsResps) ? OpenapiResponseUtils.success(Maps.newHashMap()) :
                OpenapiResponseUtils.error(1, "部分错误,详情参见data", tigerDepartmentsResps);
        } else {
            throw new OpenApiArgumentException("type未识别");
        }
    }


    /**
     * 构建返回体
     *
     * @param departmentResp
     * @param managerResp
     * @param financeResp
     * @return
     */
    private OpenapiResultEntity buildResult(List<TigerDepartmentRespDTO.TigerDepartmentsResp> departmentResp,
        List<TigerDepartmentRespDTO.TigerDepartmentsResp> managerResp,
        List<TigerDepartmentRespDTO.TigerDepartmentsResp> financeResp) {
        if (ObjectUtils.isEmpty(departmentResp) && ObjectUtils.isEmpty(managerResp) && ObjectUtils.isEmpty(
            financeResp)) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        }
        Map<String, String> result = new HashMap();
        appendResultMsg(result, departmentResp);
        appendResultMsg(result, managerResp);
        appendResultMsg(result, financeResp);
        List<TigerDepartmentRespDTO.TigerDepartmentsResp> departmentResultList = Lists.newArrayList();
        result.forEach((k, v) -> {
            TigerDepartmentRespDTO.TigerDepartmentsResp departmentsResp =
                new TigerDepartmentRespDTO.TigerDepartmentsResp();
            departmentsResp.setThirdId(k);
            departmentsResp.setErrorMsg(v);
            departmentResultList.add(departmentsResp);
        });
        Map<String, Object> department = new HashMap<>();
        department.put("departments", departmentResultList);
        return OpenapiResponseUtils.error(1, "部分错误,详情参见data", department);
    }

    /**
     * 拼接msg
     *
     * @param result
     * @param departmentResp
     * @return
     */
    private Map<String, String> appendResultMsg(Map<String, String> result,
        List<TigerDepartmentRespDTO.TigerDepartmentsResp> departmentResp) {
        if (!ObjectUtils.isEmpty(departmentResp)) {
            Map<String, String> collect =
                departmentResp.stream().collect(
                    Collectors.toMap(TigerDepartmentRespDTO.TigerDepartmentsResp::getThirdId,
                        d -> d.getErrorMsg()));
            collect.forEach((k, v) -> {
                String msg = result.get(k);
                if (StringUtils.isEmpty(msg)) {
                    result.put(k, v);
                } else {
                    result.put(k, msg + ";" + v);
                }
            });
        }
        return result;
    }

    /**
     * 创建或更新部门
     *
     * @param departmentRequest
     * @return
     */
    private List<TigerDepartmentRespDTO.TigerDepartmentsResp> createOrUpdateDepartment(
        DepartmentRequestDTO departmentRequest) {
        String url = tigerHost + "/openapi/org/department/v1/create_or_update";
        return callTigerDepartment(departmentRequest, url, departmentRequest.getCompanyId());
    }

    /**
     * 删除部门
     *
     * @param departmentRequest
     * @return
     */
    private List<TigerDepartmentRespDTO.TigerDepartmentsResp> deleteDepartment(
        DepartmentRequestDTO departmentRequest) {
        String url = tigerHost + "/openapi/org/department/v1/delete";
        return callTigerDepartment(departmentRequest, url, departmentRequest.getCompanyId());
    }

    /**
     * 设置部门主管
     *
     * @param departmentRequest
     * @return
     */
    private List<TigerDepartmentRespDTO.TigerDepartmentsResp> setDepartmentManager(
        DepartmentRequestDTO departmentRequest) {
        List<DepartmentRequestDTO.DepartmentItem> collect = departmentRequest.getDepartments().stream().filter(d ->
            !ObjectUtils.isEmpty(d.getThirdManagerIds())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(collect)) {
            return null;
        }
        DepartmentRequestDTO departmentManagerRequestDTO = new DepartmentRequestDTO();
        departmentManagerRequestDTO.setDepartments(collect);
        departmentManagerRequestDTO.setCompanyId(departmentRequest.getCompanyId());
        String url = tigerHost + "/openapi/org/department/v1/set_manager";
        return callTigerDepartment(departmentManagerRequestDTO, url, departmentRequest.getCompanyId());
    }

    /**
     * 设置部门主管
     *
     * @param departmentRequest
     * @return
     */
    private List<TigerDepartmentRespDTO.TigerDepartmentsResp> createFinanceDepartment(
        DepartmentRequestDTO departmentRequest) {
        String url = tigerHost + "/openapi/finance/department/v1/create_or_update";
        OptFinanceDeptCreateOrUpdateReqDTO optFinanceDeptCreateOrUpdateReqDTO = buildFinanceDept(departmentRequest);
        if (ObjectUtils.isEmpty(optFinanceDeptCreateOrUpdateReqDTO.getDepartments())) {
            return null;
        }
        return callTigerDepartment(optFinanceDeptCreateOrUpdateReqDTO, url, departmentRequest.getCompanyId());
    }

    /**
     * 调用部门同步
     *
     * @param departmentRequest
     * @param url
     * @return
     */
    private List<TigerDepartmentRespDTO.TigerDepartmentsResp> callTigerDepartment(
        Object departmentRequest, String url, String companyId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("appId", companyId);
        String result = RestHttpUtils.postJson(url, httpHeaders, JsonUtils.toJson(departmentRequest));
        TigerDepartmentRespDTO tigerCreateDepartmentRespDTO = JsonUtils.toObj(result,
            TigerDepartmentRespDTO.class);
        Integer code = tigerCreateDepartmentRespDTO.getCode();
        if (code == -9999) {
            throw new OpenApiArgumentException(tigerCreateDepartmentRespDTO.getMsg());
        }
        TigerDepartmentRespDTO.TigerDepartmentRespData data = tigerCreateDepartmentRespDTO.getData();
        if (data == null) {
            return null;
        }
        return data.getDepartments();
    }

    private OptFinanceDeptCreateOrUpdateReqDTO buildFinanceDept(DepartmentRequestDTO departmentRequest) {
        //转换部门实体
        List<OptFinanceDeptDTO> departments = departmentRequest.getDepartments()
            .stream().filter(department -> !ObjectUtils.isEmpty(department.getFinanceDepartment()))
            .map(department -> {
                DepartmentRequestDTO.FinanceDepartment financeDepartment = department.getFinanceDepartment().get(0);
                OptFinanceDeptDTO financeDeptDTO = new OptFinanceDeptDTO();
                financeDeptDTO.setThirdId(financeDepartment.getCode());
                financeDeptDTO.setCode(financeDepartment.getCode());
                financeDeptDTO.setName(financeDepartment.getName());
                financeDeptDTO.setState(1);
                financeDeptDTO.setThirdEntityCode(financeDepartment.getOrgCode());
                financeDeptDTO.setThirdEntityName(financeDepartment.getOrgName());
                OptBusinessDeptDTO optBusinessDeptDTO = new OptBusinessDeptDTO();
                optBusinessDeptDTO.setCode(department.getCode());
                optBusinessDeptDTO.setName(department.getName());
                optBusinessDeptDTO.setThirdId(department.getThirdId());
                financeDeptDTO.setBusinessDepartments(Lists.newArrayList(optBusinessDeptDTO));
                return financeDeptDTO;
            }).collect(Collectors.toList());
        Map<String, List<OptFinanceDeptDTO>> financeDeptMap = departments.stream().filter(t-> !StringUtils.isEmpty(t.getCode())).collect(Collectors.groupingBy(OptFinanceDeptDTO::getCode));
        List<OptFinanceDeptDTO> financeDeptList=null;
        if(!ObjectUtils.isEmpty(financeDeptMap)){
            financeDeptList = financeDeptMap.values().stream().map(a -> {
               List<OptBusinessDeptDTO> collect = a.stream().map(OptFinanceDeptDTO::getBusinessDepartments).flatMap(Collection::stream).collect(Collectors.toList());
               a.get(0).setBusinessDepartments(collect);
               return a.get(0);
           }).collect(Collectors.toList());
       }
        OptFinanceDeptCreateOrUpdateReqDTO req = new OptFinanceDeptCreateOrUpdateReqDTO();
        req.setCompanyId(departmentRequest.getCompanyId());
        req.setDepartments(financeDeptList);
        return req;
    }
}
