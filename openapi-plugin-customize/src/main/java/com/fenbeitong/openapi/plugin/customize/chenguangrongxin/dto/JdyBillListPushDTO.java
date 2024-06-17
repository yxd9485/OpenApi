package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName JdyBillListPushDTO
 * @Description 简道云账单推送列表
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/19 下午4:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdyBillListPushDTO {
    @JsonProperty("is_start_workflow")
    private Boolean isStartWorkflow;
    @JsonProperty("data_list")
    private List<BillDataDTO> dataList;
    @JsonProperty("transaction_id")
    private String transactionId;

    public Boolean getIsStartWorkflow() {
        return isStartWorkflow;
    }
    public void setIsStartWorkflow(Boolean isStartWorkflow) {
        isStartWorkflow = isStartWorkflow;
    }
}
