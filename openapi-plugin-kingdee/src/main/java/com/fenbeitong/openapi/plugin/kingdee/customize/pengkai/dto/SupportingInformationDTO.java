package com.fenbeitong.openapi.plugin.kingdee.customize.pengkai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SupportingInformationDTO
 * @Description 金蝶辅助资料信息，连接器入参
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/7/7 下午2:48
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportingInformationDTO {
    @JsonProperty("companyId")
    private String companyId;
    @JsonProperty("interactiveId")
    private String interactiveId;
    @JsonProperty("pageIndex")
    private Integer pageIndex;
    @JsonProperty("pageSize")
    private Integer pageSize;

}
