package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: YiDuiJieUpdateUserReq</p>
 * <p>Description: 易对接修改帐号请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 5:03 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiDuiJieUpdateUserReq {

    private String email;

    private String password;

    private String companyName;

    private Boolean enable;
}
