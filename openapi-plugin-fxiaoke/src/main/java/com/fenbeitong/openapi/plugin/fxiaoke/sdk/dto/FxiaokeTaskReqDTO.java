package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by hanshuqi on 2020/07/01.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeTaskReqDTO {
    /**
     * 钉钉企业id
     */
    @NotBlank(message = "钉钉企业id[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 数据ID
     */
    @NotBlank(message = "数据ID[data_id]不可为空")
    @JsonProperty("data_id")
    private String dataId;
    /**
     * 任务类型
     */
    @NotBlank(message = "任务类型[task_type]不可为空")
    @JsonProperty("task_type")
    private String taskType;
    /**
     * 数据类型
     */
    @JsonProperty("data_type")
    private Integer dataType;
    /**
     * 事件发生时间
     */
    @JsonProperty("event_time")
    private Long eventTime;
    /**
     * 最大执行次数
     */
    @NotNull(message = "最大执行次数[execute_max]不可为空")
    @JsonProperty("execute_max")
    private Integer executeMax;
    /**
     * 已执行次数
     */
    @NotNull(message = "已执行次数[execute_num]不可为空")
    @JsonProperty("execute_num")
    private Integer executeNum;
    /**
     * 数据备注
     */
    @JsonProperty("data_remark")
    private String dataRemark;
    /**
     * 优先级(0~100)
     */
    @NotNull(message = "优先级(0~100)[priority]不可为空")
    @JsonProperty("priority")
    private Integer priority;
    /**
     * 执行开始时间
     */
    @JsonProperty("execute_begin")
    private Date executeBegin;
    /**
     * 执行结束时间
     */
    @JsonProperty("execute_end")
    private Date executeEnd;
    /**
     * 下次执行时间
     */
    @JsonProperty("next_execute")
    private Date nextExecute;
    /**
     * 执行结果
     */
    @JsonProperty("execute_result")
    private String executeResult;
    /**
     * 状态
     */
    @NotNull(message = "状态[state]不可为空")
    @JsonProperty("state")
    private Integer state;
}
