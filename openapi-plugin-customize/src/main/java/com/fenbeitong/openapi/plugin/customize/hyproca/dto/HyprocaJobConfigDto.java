package com.fenbeitong.openapi.plugin.customize.hyproca.dto;

import lombok.Data;

/**
 * <p>Title: HyprocaJobConfigDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-03 18:44
 */

@Data
public class HyprocaJobConfigDto {

    String companyId;


    String corpId;

    /**
     * 请求数据的URL
     */
    String url;

    /**
     * 开始时间
     */
    String startDate;

    /**
     * 结束时间
     */
    String endDate;

    /**
     * 同步订单执行频率  单位分钟
     */
    Integer frequency;
}
