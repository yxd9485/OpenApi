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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunTokenRespDTO extends DaoYiYunBaseRespDTO {

    private String data;

}
