package com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: SipaiTripApplyDTO</p>
 * <p>Description: 思派出差申请单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/22 4:17 PM
 */
@Data
public class SipaiTripApplyDTO {

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
     * 直接上级
     */
    private String zjsj;

    /**
     * 单据编号
     */
    private String djbh;

    /**
     * 申请人
     */
    private String sqr;

    /**
     * 所属部门
     */
    private String szbm;

    /**
     * 申请日期
     */
    private String sqrq;

    /**
     * 是否公司购买机票 0:是;1:否
     */
    private String sfgsgmjp;

    /**
     * 交通工具 0:飞机 1:高铁
     */
    private String jtgj;

    /**
     * 出差类型
     */
    private String cclx;

    /**
     * 出差事由
     */
    private String ccsy;

    private String manager;

    /**
     * 项目负责人
     */
    private String xmfzr;

    private String zje;

    private String xmmc;

    private String xmbh;

    private String cdztlx;

    private String fybxlx;

    private String ysxx;

    private String fyxm;

}
