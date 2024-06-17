package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.yiduijie.model.department.Department;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: YiDuiJieListDepartmentResp</p>
 * <p>Description: 易对接财务部门</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 6:48 PM
 */
@Data
public class YiDuiJieListDepartmentResp {

    @JsonProperty("body")
    private List<Department> departmentList;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }
}
