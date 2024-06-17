package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;

/**
 * 用户抄送列表
 *
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuncCompanyApplyListReqDTO {

    /**
     * 申请人
     */
    @JsonProperty("proposer")
    private String proposer;
    /**
     * 申请单号
     */
    @JsonProperty("apply_id")
    private String applyOrderId;
    /**
     * 申请单号
     */
    @JsonProperty("third_id")
    private String thirdId;
    /**
     * 提交开始时间
     */
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String startTime;
    /**
     * 提交结束时间
     */
    @JsonProperty("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String endTime;

    /**
     * 审批单类型 -1.全部 1.差旅行程申请 2.临时用车申请 4.采购申请 5.超规用餐申请 6.国内机票订单申请 7.国际机票订单申请 8.酒店订单申请 9.火车票订单申请 10.分贝券申请 11.用餐申请 12.退订申请 13.改期申请 14 外卖申请 15个人虚拟卡 16.核销申请 17.商务核销单 18.付款申请 19.备用金 21.里程 22.滴滴企业级用车 24.自定义表单 25.汽车票订单申请
     */
    private Integer type = -1;

    /**
     * 审批状态 -1.全部 2.待审核 4.已同意 16.已拒绝 8.已作废 128.已过期 1024.变更中 2048.已变更 512.已完成
     */
    private Integer state = -1;
    /**
     * 页码
     */
    @JsonProperty("page_index")
    private Integer pageIndex = 1;

    /**
     * 条数
     */
    @JsonProperty("page_size")
    @Max(500)
    private Integer pageSize = 20;

    public String getStartTime() {
        if (!StringUtils.isBlank(startTime)) {
            return startTime + " 00:00:00";
        }
        return null;
    }

    public String getEndTime() {
        if (!StringUtils.isBlank(endTime)) {
            return endTime + " 23:59:59";
        }
        return null;
    }
}
