package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.Data;

/**
 * <p>Title: YiDuiJieAddUserResp</p>
 * <p>Description: 易对接添加账号请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 5:34 PM
 */
@Data
public class YiDuiJieAddUserResp {

    private String userId;

    private Integer status;

    private String message;

    public boolean success() {
        return status != null && status == 0;
    }
}
