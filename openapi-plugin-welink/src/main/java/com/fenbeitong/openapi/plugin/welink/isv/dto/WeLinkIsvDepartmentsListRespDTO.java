package com.fenbeitong.openapi.plugin.welink.isv.dto;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create on 2020-04-17 16:9:52
 */
@Data
public class WeLinkIsvDepartmentsListRespDTO {

    private String code;

    private String message;

    private Integer offset;

    private Integer limit;

    private Integer totalCount;

    private List<WeLinkIsvDepartmentInfo> departmentInfo;

    @Data
    public static class WeLinkIsvDepartmentInfo {

        private String deptCode;

        private String deptNameCn;

        private String deptNameEn;

        private String fatherCode;

        private String deptLevel;

        private Integer orderNo;

        private String thirdOrgUnitFullName;
    }


    public List<WeLinkIsvDepartmentInfo> getDepartmentList() {
        WeLinkIsvDepartmentInfo rootDept = departmentInfo.stream().filter(d -> "-1".equals(d.getFatherCode())).findFirst().get();
        List<String> sortIdList = getSortIdList(rootDept, departmentInfo);
        Map<String, WeLinkIsvDepartmentInfo> deptMap = departmentInfo.stream().collect(Collectors.toMap(WeLinkIsvDepartmentInfo::getDeptCode, d -> d));
        List<WeLinkIsvDepartmentInfo> sortDeptList = Lists.newArrayList();
        sortIdList.forEach(id -> sortDeptList.add(deptMap.get(id)));
        return sortDeptList;
    }


    /**
     * 自动创建根节点
     * 无父节点的挂根节点
     * 因三方应用可能无根节点或父节点
     * @return
     */
    public List<WeLinkIsvDepartmentInfo> getIsvDepartmentList(String companyName) {
        //无根节点的添加一个根节点
        if (!departmentInfo.stream().filter(d -> "0".equals(d.getDeptCode())).findFirst().isPresent()) {
            WeLinkIsvDepartmentInfo wechatDepartment = new WeLinkIsvDepartmentInfo();
            wechatDepartment.setDeptCode("0");
            wechatDepartment.setDeptNameCn(companyName);
            wechatDepartment.setFatherCode("-1");
            wechatDepartment.setOrderNo(Integer.MAX_VALUE);
            departmentInfo.add(wechatDepartment);
        }
        //父节点找不到的挂根节点
        List<String> departmentIds = departmentInfo.stream().map(WeLinkIsvDepartmentInfo::getDeptCode).collect(Collectors.toList());
        for (WeLinkIsvDepartmentInfo department: departmentInfo) {
            if (!"-1".equals(department.getFatherCode()) && !departmentIds.contains(department.getFatherCode())) {
                department.setFatherCode("-1");
            }
        }
        return getDepartmentList();
    }

    private List<String> getSortIdList(WeLinkIsvDepartmentInfo dept, List<WeLinkIsvDepartmentInfo> departmentList) {
        List<String> sortIdList = Lists.newArrayList();
        sortIdList.add(dept.getDeptCode());
        List<WeLinkIsvDepartmentInfo> subDeptList = departmentList.stream().filter(d -> d.getFatherCode().equals(dept.getDeptCode())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(subDept -> {
                if(StringUtils.isEmpty(dept.getThirdOrgUnitFullName())){
                    dept.setThirdOrgUnitFullName(dept.getDeptNameCn());
                }
                subDept.setThirdOrgUnitFullName(dept.getThirdOrgUnitFullName() + "/" + subDept.getDeptNameCn());
                sortIdList.addAll(getSortIdList(subDept, departmentList));
            });
        }
        return sortIdList;
    }

    public static void main(String[] args) {
        String str = "{\"code\":\"0\",\"message\":\"OK\",\"offset\":100,\"limit\":25,\"totalCount\":327,\"departmentInfo\":[{\"deptCode\":\"2\",\"deptNameCn\":\"产品销售部\",\"deptNameEn\":\"Sales Dept\",\"fatherCode\":\"1\",\"deptLevel\":\"2\",\"orderNo\":1},{\"deptCode\":\"3\",\"deptNameCn\":\"产品研发部\",\"deptNameEn\":\"Products Dept\",\"fatherCode\":\"1\",\"deptLevel\":\"2\",\"orderNo\":2},{\"deptCode\":\"4\",\"deptNameCn\":\"研品部\",\"deptNameEn\":\"Products Dept\",\"fatherCode\":\"3\",\"deptLevel\":\"2\",\"orderNo\":2},{\"deptCode\":\"5\",\"deptNameCn\":\"开放平台\",\"deptNameEn\":\"Products Dept\",\"fatherCode\":\"4\",\"deptLevel\":\"2\",\"orderNo\":2}]}";
        WeLinkIsvDepartmentsListRespDTO weLinkIsvDepartmentsListRespDTO = JsonUtils.toObj(str, WeLinkIsvDepartmentsListRespDTO.class);
        List<WeLinkIsvDepartmentInfo> isvDepartmentList = weLinkIsvDepartmentsListRespDTO.getIsvDepartmentList("xxx公司");
        System.out.println(JsonUtils.toJson(isvDepartmentList));
    }
}