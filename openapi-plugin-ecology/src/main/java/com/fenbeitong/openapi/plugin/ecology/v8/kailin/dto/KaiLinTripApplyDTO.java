package com.fenbeitong.openapi.plugin.ecology.v8.kailin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 开林出差申请单
 *
 * @author lizhen
 */
@Data
public class KaiLinTripApplyDTO {

    /**
     * 申请名称
     */
    @JsonProperty("requestname")
    private String requestName;

    /**
     * 申请级次
     */
    @JsonProperty("requestlevel")
    private String requestLevel;

    /**
     * 短信提醒 0不短信提醒;1离线短信提醒;2在线短信提醒
     */
    @JsonProperty("messageType")
    private String messageType;

    /**
     * 申请人
     */
    private String sqr;

    /**
     * 申请编号
     */
    private String sqbh;

    /**
     * 出差事由
     */
    private String ccsy;

    /**
     * 分公司
     */
    private String fgs;

    /**
     * 部门
     */
    private String bm;

    /**
     * 出差天数
     */
    private String ccts;

    /**
     * 出差备注
     */
    private String ccbz;

    /**
     * 同行人
     */
    private String thr;

    /**
     * 员工编号
     */
    private String ygbh;

    /**
     * 图片
     */
    private String tp;

    /**
     * 职级
     */
    private String zj;

}
