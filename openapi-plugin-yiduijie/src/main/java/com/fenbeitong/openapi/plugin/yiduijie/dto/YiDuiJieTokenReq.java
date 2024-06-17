package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: YiDuiJieTokenReq</p>
 * <p>Description: 易对接token参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:12 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiDuiJieTokenReq {

    /**
     * 服务商账号合法的客户端编号
     */
    private String username;

    /**
     * 服务商账号合法的客户端访问码
     */
    private String password;
}
