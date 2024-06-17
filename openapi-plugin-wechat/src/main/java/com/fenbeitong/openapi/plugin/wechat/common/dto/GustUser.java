package com.fenbeitong.openapi.plugin.wechat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dave.hansins on 19/12/25.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GustUser {

    private String id;
    private String phoneNum;
    private String name;
    private Boolean isEmployee;
}
