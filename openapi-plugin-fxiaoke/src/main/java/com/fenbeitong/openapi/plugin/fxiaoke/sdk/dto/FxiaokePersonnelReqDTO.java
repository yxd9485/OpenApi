package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: FxiaokeDepartmentSimpleListRespDTO</p>
 * <p>Description: 部门</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-01 16:52
 */
@Data
public class FxiaokePersonnelReqDTO {

    // 企业应用访问公司合法性凭证
    @JsonProperty("corpAccessToken")
    private String corpAccessToken;

    // 开放平台公司帐号
    @JsonProperty("corpId")
    private String corpId;

    // 每页个数，选填，可为空，为空时默认20，最大为1000，必须为大于0整数
    @JsonProperty("pageSize")
    private Integer pageSize;

    // 页码，选填，可为空，为空时默认1,必须为大于0整数
    @JsonProperty("pageNumber")
    private Integer pageNumber;

    // 开始时间戳，选填，可为空，为空时不作为条件 ,以毫秒为单位
    @JsonProperty("startTime")
    private Integer startTime;

    // 结束时间戳，选填，可为空，为空时不作为条件 ,以毫秒为单位（endTime必须大于startTime）
    @JsonProperty("endTime")
    private Integer endTime;

    @JsonProperty("showDepartmentIdsDetail")
    private Boolean showDepartmentIdsDetail;

}
