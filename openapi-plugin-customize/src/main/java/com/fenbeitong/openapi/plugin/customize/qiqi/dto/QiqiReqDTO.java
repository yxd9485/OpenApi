package com.fenbeitong.openapi.plugin.customize.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName QiqiReqDto
 * @Description 企企接口参数封装
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/8
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiReqDTO {
    /**
     * 公司id
     */
    @JsonProperty("companyId")
    String companyId;
    /**
     * 目标对象
     */
    @JsonProperty("objectType")
    String objectType;
    /**
     * 查询条件
     */
    @JsonProperty("queryConditions")
    String queryConditions;
    /**
     * 嵌套参数集合
     */
    @JsonProperty("commonReqDetailList")
    List<QiqiCommonReqDetailDTO> commonReqDetailList;
}
