package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: WechatDepartmentListRespDTO</p>
 * <p>Description: 企业微信部门列表响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 11:39 AM
 */
@Data
public class WechatDepartmentListRespDTO {

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    @JsonProperty("department")
    private List<WechatDepartment> departmentList;

    public List<WechatDepartment> getDepartmentList(String companyName) {
        WechatDepartment rootDept = departmentList.stream().filter(d -> d.getParentId() == 0).findFirst().get();
        rootDept.setName(companyName);
        rootDept.setThirdOrgUnitFullName(companyName);
        List<Long> sortIdList = getSortIdList(rootDept, departmentList);
        Map<Long, WechatDepartment> deptMap = departmentList.stream().collect(Collectors.toMap(WechatDepartment::getId, d -> d));
        List<WechatDepartment> sortDeptList = Lists.newArrayList();
        sortIdList.forEach(id -> sortDeptList.add(deptMap.get(id)));
        return sortDeptList;
    }

    /**
     * 自动创建根节点
     * 无父节点的挂根节点
     * 因三方应用可能无根节点或父节点
     * @return
     */
    public List<WechatDepartment> getIsvDepartmentList(String companyName) {
        //无根节点的添加一个根节点
        if (!departmentList.stream().filter(d -> d.getParentId() == 0).findFirst().isPresent()) {
            WechatDepartment wechatDepartment = new WechatDepartment();
            wechatDepartment.setId(1L);
            wechatDepartment.setName("1");
            wechatDepartment.setParentId(0L);
            wechatDepartment.setOrder(Long.MAX_VALUE);
            departmentList.add(wechatDepartment);
        }
        //父节点找不到的挂根节点
        List<Long> departmentIds = departmentList.stream().map(WechatDepartment::getId).collect(Collectors.toList());
        for (WechatDepartment wechatDepartment: departmentList) {
            if (wechatDepartment.getParentId() !=0 && !departmentIds.contains(wechatDepartment.getParentId())) {
                wechatDepartment.setParentId(1L);
            }
        }
        return getDepartmentList(companyName);
    }

    /**
     * 返回原始的departmentList
     * @return
     */
    public List<WechatDepartment> getDepartmentListOriginal() {
        return departmentList;
    }
    private List<Long> getSortIdList(WechatDepartment dept, List<WechatDepartment> departmentList) {
        List<Long> sortIdList = Lists.newArrayList();
        sortIdList.add(dept.getId());
        List<WechatDepartment> subDeptList = departmentList.stream().filter(d -> d.getParentId().equals(dept.getId())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(subDept -> {
                subDept.setThirdOrgUnitFullName(dept.getThirdOrgUnitFullName() + "/" + subDept.getName());
                sortIdList.addAll(getSortIdList(subDept, departmentList));
            });
        }
        return sortIdList;
    }

    @Data
    public static class WechatDepartment {

        private Long id;

        private String name;

        @JsonProperty("parentid")
        private Long parentId;

        private Long order;

        private String thirdOrgUnitFullName;
    }
}
