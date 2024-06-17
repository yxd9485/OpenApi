package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;
/**
 * <p>Title: YiDaRespDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 8:45 下午
 */

import lombok.Data;

@Data
public class YiDaRespDTO {

    private boolean success;

    private Object result;

    private String RequestId;

    private String Message;

    private Integer errorCode;

    private String HostId;

    private String Code;

    private String errorMsg;

    private String errorLevel;
}
