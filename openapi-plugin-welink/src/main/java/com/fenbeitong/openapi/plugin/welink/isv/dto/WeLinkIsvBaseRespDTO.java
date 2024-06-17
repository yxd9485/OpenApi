package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-14 20:49:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvBaseRespDTO {

    private String code;

    private String message;

}