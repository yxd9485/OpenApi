package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: WawjDeptInfoResDTO</p>
 * <p>Description: 我爱我家部门信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/11/29 2:44 PM
 */
@Data
public class WawjDeptInfoResDTO {

    private String request_id;

    private Integer code;

    private Integer type;

    private String msg;

    private WawjDeptInfoResult data;

    @Data
    public static class WawjDeptInfoResult{

        private String fullName;

        private String orgUnitFullName;

        private String id;

        private String company_id;

        private String company_name;

        private String name;

        private String parent_org_unit_id;

        private Integer rank;

        private Integer employee_count;

        private String third_org_id;

        private List<ParentDept> parent_dept_list;

        private List<String> expand_list;

        private String orgUnitCode;

    }

    @Data
    public static class ParentDept{

        private String name;

        private String third_org_id;

        private String parent_org_unit_id;

        private String id;
    }
}
