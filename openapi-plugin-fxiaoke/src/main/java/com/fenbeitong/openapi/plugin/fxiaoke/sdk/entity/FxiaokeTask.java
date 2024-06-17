package com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity;

import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/07/01.
 */
@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
@Table(name = "fxiaoke_task")
public class FxiaokeTask extends Task{

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 钉钉企业id
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 数据ID
     */
    @Column(name = "DATA_ID")
    private String dataId;

    /**
     * 任务类型
     */
    @Column(name = "task_type")
    private String taskType;

    /**
     * 数据类型
     */
    @Column(name = "DATA_TYPE")
    private Integer dataType;

    /**
     * 事件发生时间
     */
    @Column(name = "EVENT_TIME")
    private Long eventTime;

    /**
     * 最大执行次数
     */
    @Column(name = "EXECUTE_MAX")
    private Integer executeMax;

    /**
     * 已执行次数
     */
    @Column(name = "EXECUTE_NUM")
    private Integer executeNum;

    /**
     * 数据备注
     */
    @Column(name = "DATA_REMARK")
    private String dataRemark;

    /**
     * 优先级(0~100)
     */
    @Column(name = "PRIORITY")
    private Integer priority;

    /**
     * 执行开始时间
     */
    @Column(name = "EXECUTE_BEGIN")
    private Date executeBegin;

    /**
     * 执行结束时间
     */
    @Column(name = "EXECUTE_END")
    private Date executeEnd;

    /**
     * 下次执行时间
     */
    @Column(name = "NEXT_EXECUTE")
    private Date nextExecute;

    /**
     * 执行结果
     */
    @Column(name = "EXECUTE_RESULT")
    private String executeResult;

    /**
     * 状态
     */
    @Column(name = "STATE")
    private Integer state;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
