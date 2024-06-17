package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: WawjKaoQinDTO</p>
 * <p>Description: 考勤请求数据</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 5:49 PM
 */
@Data
public class WawjKaoQinDTO {

    @JsonProperty("shift_type")
    private Integer shiftType;

    @JsonProperty("shift_type_desc")
    private String shiftTypeDesc;

    private String name;

    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;

    @JsonProperty("work_date")
    private String workDate;

    @JsonProperty("date_type")
    private Integer dateType;

    @JsonProperty("check_in_time")
    private String checkInTime;

    @JsonProperty("check_out_time")
    private String checkoutTime;

    @JsonProperty("work_hours")
    private BigDecimal workHours;

    @JsonProperty("attr_1")
    private String attr1;

    @JsonProperty("attr_2")
    private String attr2;

    @JsonProperty("attr_3")
    private String attr3;

    @JsonProperty("attr_4")
    private String attr4;

    @JsonProperty("attr_5")
    private String attr5;

    @JsonProperty("type")
    private Integer type;
}
