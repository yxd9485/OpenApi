package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BeisenRankParamConfig
 * @Description 北森职级参数配置类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/17
 **/
@Data
public class BeisenRankParamConfig {
    /**
     * 公司Id
     */
    @JsonProperty("companyId")
    private String companyId;

    /**
     * 权限顶级Id
     */
    @JsonProperty("parentId")
    private String parentId;

    /**
     * 租户Id
     */
    @JsonProperty("tenantId")
    private String tenantId;

    /**
     * app_id
     */
    @JsonProperty("appId")
    private String appId;

    /**
     * 密钥
     */
    @JsonProperty("secret")
    private String secret;

    /**
     * key
     */
    @JsonProperty("key")
    private String key;

    /**
     * 权限类型
     */
    @JsonProperty("grantType")
    private String grantType;

    /**
     * 要接入的审批单类型
     */
    @JsonProperty("typeList")
    private List<String> typeList;

    /**
     * 开始时间
     */
    @JsonProperty("startDate")
    private String startDate;

    /**
     * 结束时间
     */
    @JsonProperty("endDate")
    private String endDate;

    /**
     * day
     */
    @JsonProperty("day")
    private Integer day;

    /**
     * hour
     */
    @JsonProperty("hour")
    private Integer hour;

    /**
     * mine
     */
    @JsonProperty("mine")
    private Integer mine;

    /**
     * 起始页
     */
    @JsonProperty("pageIndex")
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /**
     * 机票火车是否要生成往返数据 1 生成 0 不生成
     */
    @JsonProperty("tripType")
    private String tripType;

    /**
     * 公出单扩展字段的key
     */
    @JsonProperty("cityId")
    private String cityId;

    /**
     * 是否执行强删的操作
     */
    @JsonProperty("forceDeleteFlag")
    private Boolean forceDeleteFlag;

    /**
     * 是否执行强删的操作
     */
    @JsonProperty("startCityCarFlag")
    private Boolean startCityCarFlag;

    /**
     * 是否用新的获取秘钥
     */
    @JsonProperty("tokenUrlIsNew")
    private Boolean tokenUrlIsNew = false;

    /**
     * 是否用新的获取秘钥
     */
    @JsonProperty("companyName")
    private String companyName;

    /**
     * 是否用新的获取秘钥
     */
    @JsonProperty("delTopLevel")
    private int delTopLevel;
}
