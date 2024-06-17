package com.fenbeitong.openapi.plugin.beisen.common.dto;

import lombok.Data;

import java.util.List;

/**
 * 北森参数配置类
 *
 * @author xiaowei
 * @date 2020/07/28
 */
@Data
public class BeisenParamConfig {

    /**
     * 公司Id
     */
    private String companyId;

    /**
     * 权限顶级Id
     */
    private String parentId;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * app_id
     */
    private String appId;

    /**
     * 密钥
     */
    private String secret;

    /**
     * key
     */
    private String key;
    /**
     * 权限类型
     */
    private String grantType;

    /**
     * 要接入的审批单类型
     */
    private List<String> typeList;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * day
     */
    private Integer day;

    /**
     * hour
     */
    private Integer hour;

    /**
     * mine
     */
    private Integer mine;

    /**
     * 起始页
     */
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    private Integer pageSize;

    /**
     * 机票火车是否要生成往返数据 1 生成 0 不生成
     */
    private String tripType;

    /**
     * 公出单扩展字段的key
     */
    private String cityId;

    /**
     * 是否执行强删的操作
     */
    private Boolean forceDeleteFlag;

    /**
     * 是否执行强删的操作
     */
    private Boolean startCityCarFlag;

    /**
     * 是否用新的获取秘钥
     */
    private Boolean tokenUrlIsNew = false;

    /**
     * 是否用新的获取秘钥
     */
    private String companyName;

    /**
     * 是否用新的获取秘钥
     */
    private int delTopLevel;


}
