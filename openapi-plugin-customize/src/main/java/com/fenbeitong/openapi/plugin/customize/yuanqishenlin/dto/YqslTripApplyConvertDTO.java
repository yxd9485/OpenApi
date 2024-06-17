package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.core.entity.KvEntity;
import com.fenbeitong.openapi.plugin.support.apply.dto.MultiGuestListDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YqslTripApplyConvertDTO {
    //申请事由
    @JsonProperty("apply_reason")
    private String applyReason;
    //申请事由描述
    @JsonProperty("apply_reason_desc")
    private String applyReasonDesc;
    //分贝id
    @JsonProperty("employee_id")
    private String employeeId;
    //三方审批单id
    @JsonProperty("third_id")
    private String thirdId;
    //开始日期
    @JsonProperty("start_time")
    private Date startTime;
    //结束日期
    @JsonProperty("end_time")
    private Date endTime;
    //多城市
    @JsonProperty("multi_trip_city")
    private List<KvEntity> multiTripCity;
    //乘客姓名
    @JsonProperty("name")
    private String name;
    @JsonProperty("guest_list")
    private List<MultiGuestListDTO> guestList;

}
