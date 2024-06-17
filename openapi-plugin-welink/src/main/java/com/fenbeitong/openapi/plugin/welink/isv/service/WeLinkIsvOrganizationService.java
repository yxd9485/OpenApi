package com.fenbeitong.openapi.plugin.welink.isv.service;

import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.welink.common.WeLinkResponseCode;
import com.fenbeitong.openapi.plugin.welink.common.exception.OpenApiWeLinkException;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvDepartmentsListRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvDepartmentsRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.util.WeLinkIsvHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by lizhen on 2020/4/17.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvOrganizationService extends AbstractOrganizationService {

    @Value("${welink.api-host}")
    private String welinkHost;

    @Autowired
    private WeLinkIsvHttpUtils weLinkIsvHttpUtils;

    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;

    /**
     * 查询子部门信息
     * welink根部门无法自动遍历，需要先查根部门，再手动遍历
     * 查询结果里无根部门，需要手动补全
     *
     * @param corpId
     * @param deptCode 0为根部门
     * @param offset
     * @return
     */
    public List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> weLinkDepartmentsList(String corpId, String deptCode, String companyName, Integer offset) {
        String url = welinkHost + WeLinkIsvConstant.DEPARTMENTS_LIST_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("deptCode", deptCode);
        // 根部门不支持自动遍历
        if ("0".equals(deptCode)) {
            param.put("recursiveflag", "0");
        } else {
            param.put("recursiveflag", "1");
        }
        param.put("offset", offset);
        param.put("limit", "100");
        String res = weLinkIsvHttpUtils.getJsonWithAccessToken(url, param, corpId);
        WeLinkIsvDepartmentsListRespDTO weLinkIsvDepartmentsListRespDTO = JsonUtils.toObj(res, WeLinkIsvDepartmentsListRespDTO.class);
        if (weLinkIsvDepartmentsListRespDTO == null || (!"0".equals(weLinkIsvDepartmentsListRespDTO.getCode()) && !"47009".equals(weLinkIsvDepartmentsListRespDTO.getCode()))) {
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_DEPARTMENTS_LIST_FAILED);
        }
        Integer totalCount = weLinkIsvDepartmentsListRespDTO.getTotalCount();//总条数
        totalCount = totalCount == null ? 0 : totalCount;
        Integer totalPage = totalCount / 100 + 1; //总页数
        //递归获取剩余分页
        if (offset < totalPage) {
            List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> weLinkIsvDepartmentinfos = weLinkDepartmentsList(corpId, deptCode, companyName, offset + 1);
            weLinkIsvDepartmentsListRespDTO.getDepartmentInfo().addAll(weLinkIsvDepartmentinfos);
        }
        // 手动遍历根部门
        if ("0".equals(deptCode)) {
            List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> weLinkIsvDepartmentInfos = new ArrayList<>();
            CollectionUtils.addAll(weLinkIsvDepartmentInfos, weLinkIsvDepartmentsListRespDTO.getDepartmentInfo());
            for (WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo weLinkIsvDepartmentInfo : weLinkIsvDepartmentInfos) {
                List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> weLinkIsvDepartmentinfos = weLinkDepartmentsList(corpId, weLinkIsvDepartmentInfo.getDeptCode(), companyName, 1);
                weLinkIsvDepartmentsListRespDTO.getDepartmentInfo().addAll(weLinkIsvDepartmentinfos);
            }
            return weLinkIsvDepartmentsListRespDTO.getIsvDepartmentList(companyName);
        }
        return weLinkIsvDepartmentsListRespDTO.getDepartmentInfo();
    }

    /**
     * 按照部门级别排序
     *
     * @param departmentIds departmentIds
     * @param corpId        corpId
     * @return
     */
    public List<String> getSortedDepartments(List<String> departmentIds, String corpId) {
        List<WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo> allDepts = this.weLinkDepartmentsList(corpId, "0", "",1);
        // 排序后的部门ID
        return allDepts.stream()
                .filter(dept -> departmentIds.contains(dept.getDeptCode()))
                .map(WeLinkIsvDepartmentsListRespDTO.WeLinkIsvDepartmentInfo::getDeptCode)
                .collect(toList());
    }


    /**
     * 查询子部门信息
     */
    public WeLinkIsvDepartmentsRespDTO weLinkDepartment(String deptCode, String corpId) {
        String url = welinkHost + WeLinkIsvConstant.DEPARTMENTS_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("deptCode", deptCode);
        String res = weLinkIsvHttpUtils.getJsonWithAccessToken(url, param, corpId);
        WeLinkIsvDepartmentsRespDTO weLinkIsvDepartmentsRespDTO = JsonUtils.toObj(res, WeLinkIsvDepartmentsRespDTO.class);
        if (weLinkIsvDepartmentsRespDTO == null || !"0".equals(weLinkIsvDepartmentsRespDTO.getCode())) {
            throw new OpenApiWeLinkException(WeLinkResponseCode.WELINK_DEPARTMENTS_LIST_FAILED);
        }
        return weLinkIsvDepartmentsRespDTO;
    }
}
