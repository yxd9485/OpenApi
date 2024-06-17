package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushMessageFailDataDTO {
    /**
     * 三方用户ID
     */
    private String thirdEmployeeId;

    /**
     * 公司ID
     */
    private String companyId;

    /**
     * 开始时间
     */
    private String createTimeBegin;

    /**
     * 结束时间
     */
    private String createTimeEnd;
}
