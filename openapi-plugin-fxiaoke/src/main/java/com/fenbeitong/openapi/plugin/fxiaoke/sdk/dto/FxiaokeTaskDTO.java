package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * Created by hanshuqi on 2020/07/01.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeTaskDTO {
    /**
     * 钉钉企业id
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 数据ID
     */
    @JsonProperty("data_id")
    private String dataId;
    /**
     * 任务类型
     */
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
    @JsonProperty("execute_max")
    private Integer executeMax;
    /**
     * 已执行次数
     */
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
    @JsonProperty("state")
    private Integer state;
}
