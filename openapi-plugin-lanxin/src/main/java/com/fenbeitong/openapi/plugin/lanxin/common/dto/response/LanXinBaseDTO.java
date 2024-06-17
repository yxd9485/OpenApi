package com.fenbeitong.openapi.plugin.lanxin.common.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Title: BaseDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 2:44 下午
 */
@Data
public class LanXinBaseDTO<T> implements Serializable {
    String errCode;
    String errMsg;
    T data;
}
