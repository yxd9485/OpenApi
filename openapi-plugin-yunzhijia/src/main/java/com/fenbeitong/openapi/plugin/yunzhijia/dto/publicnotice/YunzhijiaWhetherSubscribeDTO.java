package com.fenbeitong.openapi.plugin.yunzhijia.dto.publicnotice;

import lombok.Data;

/**
 * 企业是否订阅返回结果DTO
 * @Auther zhang.peng
 * @Date 2021/7/30
 */
@Data
public class YunzhijiaWhetherSubscribeDTO {

    private String ssb;  //1是订阅；0是没有订阅
}
