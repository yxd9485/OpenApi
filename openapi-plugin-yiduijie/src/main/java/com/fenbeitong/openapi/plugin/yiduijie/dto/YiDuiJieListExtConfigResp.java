package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.Data;

/**
 * <p>Title: YiDuiJieListExtConfigResp</p>
 * <p>Description: 易对接查询其他配置响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 10:51 AM
 */
@Data
public class YiDuiJieListExtConfigResp {

    private String body;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }
}
