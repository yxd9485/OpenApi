package com.fenbeitong.openapi.plugin.customize.zhiou.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName ApplyDetailDTO
 * @Description 审批单详情
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/6
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDetailDTO {

    /**
     * 员工姓名
     */
    @JsonProperty("employee_name")
    private String employeeName;

    /**
     * 员工三方id
     */
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;

    /**
     * 申请单状态
     */
    @JsonProperty("state")
    private Integer state;

    /**
     * 上一张申请单id
     */
    @JsonProperty("parent_id")
    private String parentId;

}
