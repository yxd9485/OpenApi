package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: WawjFuLiShenQingDTO</p>
 * <p>Description: 我爱我家福利申请数据</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 6:14 PM
 */
@Data
public class WawjFuLiShenQingDTO {

    @JsonProperty("apply_id")
    private String applyId;

    private String name;

    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;

    private String phone;

    @JsonProperty("work_date")
    private String workDate;

    @JsonProperty("apply_time")
    private String applyTime;

    @JsonProperty("type")
    private Integer type;
}
