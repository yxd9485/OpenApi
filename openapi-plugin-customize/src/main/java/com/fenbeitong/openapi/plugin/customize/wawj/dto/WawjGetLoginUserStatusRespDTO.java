package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-10-24 19:12:37
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WawjGetLoginUserStatusRespDTO {

    private Integer status;

    private String msg;

    private String emplid;


}