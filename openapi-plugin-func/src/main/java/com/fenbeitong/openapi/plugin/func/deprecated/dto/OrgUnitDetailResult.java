package com.fenbeitong.openapi.plugin.func.deprecated.dto;

import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/18 11:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgUnitDetailResult {
    private String fullName;
    private String orgUnitFullName;
    private String id;
    private String company_id;
    private String company_name;
    private String name;
    private List<OrgUnitResult.DeptManagerBean> dept_managers_info;
    private String parent_org_unit_id;
    private Integer level;
    private Integer rank;
    private Integer employee_count;
    private String third_org_id;
    private List<Map<String, Object>> parent_dept_list;
    private String expand_json;
    private List<Map<String, Object>> expand_list = Collections.emptyList();
    private String org_unit_code;
}
