package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.Data;

/**
 * <p>Title: YiDuiJieApp</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 6:53 PM
 */
@Data
public class YiDuiJieApp {

    private String appId;

    private String catalog;

    private Long createdDate;

    private String fromType;

    private String status;

    private String title;

    private String toType;

    private Integer totalUsage;

    private String vendor;
}
