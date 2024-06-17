package com.fenbeitong.openapi.plugin.daoyiyun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2022-06-03 14:23:13
 * @author lizhen
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunBaseRespDTO {

    private String msg;

    private String traceId;

    private int code;


}
