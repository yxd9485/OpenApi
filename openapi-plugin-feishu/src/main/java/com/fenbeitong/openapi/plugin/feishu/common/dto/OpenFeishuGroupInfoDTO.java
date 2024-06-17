package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by zhangpeng on 2021/09/25.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenFeishuGroupInfoDTO {
    /**
     * 考勤组ID
     */
    @JsonProperty("group_id")
    private String groupId;
    /**
     * 考勤组状态；1 可用；0 废弃
     */
    @JsonProperty("status")
    private String status;
    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;
}
