package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 删除泛微待办请求DTO
 * @Auther zhang.peng
 * @Date 2021/12/7
 */
@Data
public class EcologyDeleteToDoReqDTO {

    @JsonProperty("syscode")
    private String sysCode = "FBT";

    @JsonProperty("flowid")
    private String flowId;

    @JsonProperty("userid")
    private String userId;

}
