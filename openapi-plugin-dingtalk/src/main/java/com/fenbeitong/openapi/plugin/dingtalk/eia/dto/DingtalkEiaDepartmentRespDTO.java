package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ctl
 * @date 2021/4/27
 */
@Data
public class DingtalkEiaDepartmentRespDTO {

    private List<OapiDepartmentListResponse.Department> departmentInfos;

    private List<Long> getSortIdList(OapiDepartmentListResponse.Department dept, List<OapiDepartmentListResponse.Department> departmentList) {
        List<Long> sortIdList = Lists.newArrayList();
        sortIdList.add(dept.getId());
        List<OapiDepartmentListResponse.Department> subDeptList = departmentList.stream().filter(d -> d.getParentid() != null && d.getParentid().equals(dept.getId())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(subDept -> {
                subDept.setSourceIdentifier(dept.getSourceIdentifier() + "/" + subDept.getName());
                sortIdList.addAll(getSortIdList(subDept, departmentList));
            });
        }
        return sortIdList;
    }

    public List<OapiDepartmentListResponse.Department> getDepartmentList() {
        OapiDepartmentListResponse.Department rootDept = departmentInfos.stream().filter(d -> 1L == d.getId()).findFirst().get();
        rootDept.setSourceIdentifier(rootDept.getName());
        List<Long> sortIdList = getSortIdList(rootDept, departmentInfos);
        Map<Long, OapiDepartmentListResponse.Department> deptMap = departmentInfos.stream().collect(Collectors.toMap(OapiDepartmentListResponse.Department::getId, d -> d));
        List<OapiDepartmentListResponse.Department> sortDeptList = Lists.newArrayList();
        sortIdList.forEach(id -> sortDeptList.add(deptMap.get(id)));
        return sortDeptList;
    }

    /**
     * 自动创建根节点
     * 无父节点的挂根节点
     * 因三方应用可能无根节点或父节点
     *
     * @return
     */
    public List<OapiDepartmentListResponse.Department> getEiaDepartmentList(String companyName) {
        if (departmentInfos != null) {
            //无根节点的添加一个根节点
            if (!departmentInfos.stream().filter(d -> 1 == d.getId()).findFirst().isPresent()) {
                OapiDepartmentListResponse.Department departmentInfo = new OapiDepartmentListResponse.Department();
                departmentInfo.setId(1L);
                departmentInfo.setName(companyName);
                departmentInfo.setParentid(null);
                departmentInfos.add(departmentInfo);
            }
            //父节点找不到的挂根节点
            List<Long> departmentIds = departmentInfos.stream().map(OapiDepartmentListResponse.Department::getId).collect(Collectors.toList());
            for (OapiDepartmentListResponse.Department department : departmentInfos) {
                if (1 != department.getId() && !departmentIds.contains(department.getParentid())) {
                    department.setParentid(1L);
                }
            }
            return getDepartmentList();
        }
        return null;
    }

}
