package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QiqiFinhubTaskDto
 * @Description 保存finhub_task表的参数封装
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiFinhubTaskDTO {
    /**
     * 数据id
     */
    @JsonProperty("objectId")
    private String objectId;
    /**
     * 三方企业id
     */
    @JsonProperty("corpId")
    private String corpId;
    /**
     * 创建人
     */
    @JsonProperty("createId")
    private String createId;
    /**
     * 业务类型枚举
     */
    @JsonProperty("taskType")
    private TaskType taskType;
    /**
     * 消息内容
     */
    @JsonProperty("dataMap")
    private QiqiMessageBodyDTO dataMap;
}
