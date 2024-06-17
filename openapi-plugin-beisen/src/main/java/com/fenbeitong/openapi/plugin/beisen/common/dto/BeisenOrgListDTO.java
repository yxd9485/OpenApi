package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 北森的组织数据结构
 *
 * @author xiaowei
 * @date 2020/06/16
 */
@Data
public class BeisenOrgListDTO {

    @JsonProperty("Total")
    private Integer total;
    @JsonProperty("Data")
    private List<OrgDto> data;
    @JsonProperty("Code")
    private int code;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Extra")
    private String extra;

    @Data
    public static class OrgDto {

        @JsonProperty("OId")
        private String oId;
        @JsonProperty("Name")
        private String name;
        @JsonProperty("ShortName")
        private String shortName;
        @JsonProperty("POIdOrgAdmin")
        private String poIdOrgAdmin;
        @JsonProperty("Status")
        private int status;
        @JsonProperty("StdIsDeleted")
        private boolean stdIsDeleted;
        @JsonProperty("CreatedTime")
        private String createTime;
        @JsonProperty("ModifiedTime")
        private String modifiedTime;
        @JsonProperty("PersonInCharge")
        private String personInCharge;
        @JsonProperty("extend_1")
        private String extend_1;
        @JsonProperty("extend_2")
        private String extend_2;
        @JsonProperty("extend_3")
        private String extend_3;

    }

    public List<OrgDto> getDepartmentList(String companyName, String parentId) {
        List<OrgDto> sortDeptList = Lists.newArrayList();
        if (!StringUtils.isBlank(parentId) && data.size() > 0) {
            List<OrgDto> allOrgList = data.stream().filter(d -> d.getPoIdOrgAdmin().equals(parentId)).collect(Collectors.toList());
            allOrgList.stream().forEach(rootOrgDto -> {
                rootOrgDto.setShortName(rootOrgDto.getName());
                rootOrgDto.setName(companyName.concat("/").concat(rootOrgDto.name));
                List<String> sortIdList = getSortIdList(rootOrgDto, data);
                Map<String, OrgDto> deptMap = data.stream().collect(Collectors.toMap(BeisenOrgListDTO.OrgDto::getOId, d -> d));
                sortIdList.forEach(id -> sortDeptList.add(deptMap.get(id)));
            });
        }
        return sortDeptList;
    }

    private List<String> getSortIdList(OrgDto org, List<OrgDto> orgDtoList) {
        List<String> sortIdList = Lists.newArrayList();
        sortIdList.add(org.getOId());
        List<OrgDto> subDeptList = orgDtoList.stream().filter(d -> d.getPoIdOrgAdmin().equals(org.getOId())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(subDept -> {
                subDept.setShortName(subDept.getName());
                subDept.setName(org.getName() + "/" + subDept.getName());
                sortIdList.addAll(getSortIdList(subDept, orgDtoList));
            });
        }
        return sortIdList;
    }

}
