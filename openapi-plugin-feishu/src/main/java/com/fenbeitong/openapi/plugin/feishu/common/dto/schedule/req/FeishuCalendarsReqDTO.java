package com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 飞书创建日历请求数据
 * @Author: xiaohai
 * @Date: 2021/12/26 下午10:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeishuCalendarsReqDTO {

    private String summary;

    private String description;

    private String permissions;

    private int color;

    @JsonProperty("summary_alias")
    private String summaryAlias;

}
