package com.fenbeitong.openapi.plugin.kingdee.customize.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ConnectorInfoDTO
 * @Description 连接器参数
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/7/26 下午7:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorInfoDTO {
    @JsonProperty("companyId")
    private String companyId;
    @JsonProperty("interactiveId")
    private String interactiveId;
    @JsonProperty("pageIndex")
    private Integer pageIndex;
    @JsonProperty("pageSize")
    private Integer pageSize;
}
