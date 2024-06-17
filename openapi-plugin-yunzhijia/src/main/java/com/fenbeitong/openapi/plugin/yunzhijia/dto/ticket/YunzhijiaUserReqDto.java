package com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther zhang.peng
 * @Date 2021/4/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YunzhijiaUserReqDto {

    private String companyId;

    private String userId;

}
