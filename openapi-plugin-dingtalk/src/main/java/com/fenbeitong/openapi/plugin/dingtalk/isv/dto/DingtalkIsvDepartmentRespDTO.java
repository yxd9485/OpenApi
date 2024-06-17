package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/7/16
 */
@Data
public class DingtalkIsvDepartmentRespDTO {

    private List<OapiDepartmentListResponse.Department> departmentInfos;


    private List<Long> getSortIdList(OapiDepartmentListResponse.Department dept, List<OapiDepartmentListResponse.Department> departmentList) {
        List<Long> sortIdList = Lists.newArrayList();
        sortIdList.add(dept.getId());
        List<OapiDepartmentListResponse.Department> subDeptList = departmentList.stream().filter(d -> d.getParentid() != null && d.getParentid().equals(dept.getId())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(subDept -> {
//                if (StringUtils.isEmpty(dept.getSourceIdentifier())) {
//                    dept.setSourceIdentifier(dept.getName());
//                }
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
    public List<OapiDepartmentListResponse.Department> getIsvDepartmentList(String companyName) {
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

    public static void main(String[] args) {
        String s = "{\"errcode\":0,\"errmsg\":\"ok\",\"departmentInfos\":[{\"id\":2,\"name\":\"xxx\",\"parentid\":1,\"createDeptGroup\":true,\"autoAddUser\":true},{\"id\":3,\"name\":\"服务端开发组\",\"parentid\":2,\"createDeptGroup\":false,\"autoAddUser\":false}]}";
        DingtalkIsvDepartmentRespDTO dingtalkIsvDepartmentRespDTO = JsonUtils.toObj(s, DingtalkIsvDepartmentRespDTO.class);
        System.out.println(JsonUtils.toJson(dingtalkIsvDepartmentRespDTO.getIsvDepartmentList("蛋黄")));
    }

}
