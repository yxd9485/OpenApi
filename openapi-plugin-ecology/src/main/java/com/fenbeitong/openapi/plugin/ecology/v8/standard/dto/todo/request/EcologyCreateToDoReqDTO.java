package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 创建泛微待办请求DTO
 * @Auther zhang.peng
 * @Date 2021/12/7
 */
@Data
@Builder
public class EcologyCreateToDoReqDTO {

    /**
     * 异构系统标识
     */
    @JsonProperty("syscode")
    private String sysCode ;

    /**
     * 流程实例id
     */
    @JsonProperty("flowid")
    private String flowId;

    /**
     * 标题
     */
    @JsonProperty("requestname")
    private String requestName;

    /**
     * 流程类型名称
     */
    @JsonProperty("workflowname")
    private String workflowName;

    /**
     * 步骤名称（节点名称）
     */
    @JsonProperty("nodename")
    private String nodeName;

    /**
     * PC地址
     */
    @JsonProperty("pcurl")
    private String pcUrl;

    /**
     * APP地址
     */
    @JsonProperty("appurl")
    private String appUrl;

    /**
     * 流程处理状态 // 0：待办 2：已办 4：办结 8：抄送（待阅）
     */
    @JsonProperty("isremark")
    private String isRemark;

    /**
     * 流程查看状态 // 0：未读 1：已读;
     */
    @JsonProperty("viewtype")
    private String viewType;

    /**
     * 创建人（原值）
     */
    @JsonProperty("creator")
    private String creator;

    /**
     * 创建日期时间
     */
    @JsonProperty("createdatetime")
    private String createDateTime;

    /**
     * 接收人（原值）
     */
    @JsonProperty("receiver")
    private String receiver;

    /**
     * 接收日期时间
     */
    @JsonProperty("receivedatetime")
    private String receiveDateTime;

    /**
     * 时间戳字段，客户端使用线程调用接口的时候，根据此字段判断是否需要更新数据，防止后发的请求数据被之前的覆盖 例如"1602817491990"(毫秒级时间戳)
     */
    @JsonProperty("receivets")
    private String receivets;
}
