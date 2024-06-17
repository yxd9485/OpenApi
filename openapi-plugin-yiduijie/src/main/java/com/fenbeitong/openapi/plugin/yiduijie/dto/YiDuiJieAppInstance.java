package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.Data;

/**
 * <p>Title: YiDuiJieApp</p>
 * <p>Description: 易对接应用实例</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 6:41 PM
 */
@Data
public class YiDuiJieAppInstance {

    private String id;

    private String description;

    private Long beginDate;

    private Long endDate;

    private String status;

    private Boolean stopped;

}
