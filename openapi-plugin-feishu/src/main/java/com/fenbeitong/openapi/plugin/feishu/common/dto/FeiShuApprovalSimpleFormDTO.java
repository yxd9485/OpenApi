package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 飞书简单表单，用于表单定义拉取，创建instance
 * @author lizhen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuApprovalSimpleFormDTO {
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
    private Object value;

    @JsonProperty("children")
    private List<FeiShuApprovalSimpleFormDTO> children;


}
