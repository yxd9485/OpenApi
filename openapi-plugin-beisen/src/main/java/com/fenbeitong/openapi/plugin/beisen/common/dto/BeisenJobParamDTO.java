package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: BeisenJobParamDTO<p>
 * <p>Description: 北森定时任务body参数<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/9/13 21:50
 */
@Data
public class BeisenJobParamDTO {
    /**
     * 公司id
     */
    @JsonProperty("companyId")
    private String companyId;
    /**
     * 权限类型
     */
    private String grantType;
    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;
}
