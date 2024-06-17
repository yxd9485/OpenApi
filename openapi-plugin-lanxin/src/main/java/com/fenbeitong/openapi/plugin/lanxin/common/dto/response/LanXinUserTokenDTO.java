package com.fenbeitong.openapi.plugin.lanxin.common.dto.response;

import lombok.Data;

/**
 * <p>Title: BaseDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 2:44 下午
 */
@Data
public class LanXinUserTokenDTO {
    String userToken;
    String expiresIn;
    String scope;
    String state;
    String staffId;
}
