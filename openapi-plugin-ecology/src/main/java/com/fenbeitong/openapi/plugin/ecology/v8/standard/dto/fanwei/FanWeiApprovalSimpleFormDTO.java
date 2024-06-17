package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 泛微简单表单，用于表单定义拉取，创建instance
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanWeiApprovalSimpleFormDTO {
    //表单ID
    @JsonProperty("id")
    private String id;
    //表单ID
    @JsonProperty("name")
    private String name;
    //表单type
    @JsonProperty("type")
    private String type;
    //具体数据
    @JsonProperty("value")
    private String value;


}
