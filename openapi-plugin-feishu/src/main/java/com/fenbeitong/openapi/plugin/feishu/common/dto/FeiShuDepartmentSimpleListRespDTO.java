package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
 * @date 2020/6/2
 */
@Data
public class FeiShuDepartmentSimpleListRespDTO {

    private Integer code;

    private String msg;

    private DepartmentSimpleList data;

    @Data
    public static class DepartmentSimpleList {

        @JsonProperty("has_more")
        private boolean hasMore;

        private List<DepartmentInfo> items;

        @JsonProperty("page_token")
        private String pageToken;
//
//        @JsonProperty("department_infos")
//        private List<DepartmentInfo> departmentInfos;


    }

    @Data
    public static class DepartmentInfo {

        @JsonProperty("chat_id")
        private String chatId;

        @JsonProperty("department_id")
        private String id;

        @JsonProperty("leader_user_id")
        private String leaderUserId;

        @JsonProperty("member_count")
        private String memberCount;

        private String name;

        @JsonProperty("open_department_id")
        private String openDepartmentId;

        private String order;

        @JsonProperty("parent_department_id")
        private String parentId;

        private Status status;

        private String thirdOrgUnitFullName;

        @JsonProperty("unit_ids")
        private List<String> unitIds;
    }

    @Data
    public static class Status {

        @JsonProperty("is_deleted")
        private boolean isDeleted;

    }

    @Data
    public static class IName {

        @JsonProperty("en_us")
        private String enUs;

        @JsonProperty("ja_jp")
        private String jaJp;

        @JsonProperty("zh_cn")
        private String zhCn;

    }

//    @Data
//    public static class DepartmentInfo {
//
//        private String id;
//
//        private String name;
//
//        @JsonProperty("parent_id")
//        private String parentId;
//
//        private String thirdOrgUnitFullName;
//
//        @JsonProperty("leader_employee_id")
//        private String leaderEmployeeId;
//
//        @JsonProperty("leader_open_id")
//        private String leaderOpenId;
//    }


    private List<String> getSortIdList(DepartmentInfo dept, List<DepartmentInfo> departmentList) {
        List<String> sortIdList = Lists.newArrayList();
        sortIdList.add(dept.getId());
        List<DepartmentInfo> subDeptList = departmentList.stream().filter(depart -> !StringUtils.isEmpty(depart.getParentId())).filter(d -> d.getParentId().equals(dept.getId())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(subDept -> {
                if (StringUtils.isEmpty(dept.getThirdOrgUnitFullName())) {
                    dept.setThirdOrgUnitFullName(dept.getName());
                }
                subDept.setThirdOrgUnitFullName(dept.getThirdOrgUnitFullName() + "/" + subDept.getName());
                sortIdList.addAll(getSortIdList(subDept, departmentList));
            });
        }
        return sortIdList;
    }

    public List<DepartmentInfo> getDepartmentList() {
        DepartmentInfo rootDept = data.getItems().stream().filter(d -> "0".equals(d.getId())).findFirst().get();
        List<String> sortIdList = getSortIdList(rootDept, data.getItems());
        Map<String, DepartmentInfo> deptMap = data.getItems().stream().collect(Collectors.toMap(DepartmentInfo::getId, d -> d));
        List<DepartmentInfo> sortDeptList = Lists.newArrayList();
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
    public List<DepartmentInfo> getIsvDepartmentList(String companyName) {
        if (data.getItems() != null) {
            //无根节点的添加一个根节点
            if (!data.getItems().stream().filter(d -> "0".equals(d.getId())).findFirst().isPresent()) {
                DepartmentInfo departmentInfo = new DepartmentInfo();
                departmentInfo.setId("0");
                departmentInfo.setName(companyName);
                departmentInfo.setParentId("");
                data.getItems().add(departmentInfo);
            }
            //父节点找不到的挂根节点
            List<String> departmentIds = data.getItems().stream().map(DepartmentInfo::getId).collect(Collectors.toList());
            for (DepartmentInfo department : data.getItems()) {
                if (!"0".equals(department.getId()) && !departmentIds.contains(department.getParentId())) {
                    department.setParentId("0");
                }
            }
            return getDepartmentList();
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "{\"code\":0,\"msg\":\"success\",\"data\":{\"has_more\":true,\"page_token\":\"763bd1e74d05e95e\",\"department_infos\":[]}}";
        FeiShuDepartmentSimpleListRespDTO feiShuDepartmentSimpleListRespDTO = JsonUtils.toObj(str, FeiShuDepartmentSimpleListRespDTO.class);
        List<DepartmentInfo> isvDepartmentList = feiShuDepartmentSimpleListRespDTO.getIsvDepartmentList("xxx公司");
        System.out.println(JsonUtils.toJson(isvDepartmentList));
    }
}
