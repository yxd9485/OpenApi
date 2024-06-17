package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.Data;

/**
 * <p>Title: YiDuiJieSendMessageResp</p>
 * <p>Description: 易对接发送消息响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 6:45 PM
 */
@Data
public class YiDuiJieSendMessageResp {

    /**
     * 0表示成功，非0数字为错误代码，具体含义参考错误代码对照
     */
    private Integer status;

    /**
     * 返回消息文本
     */
    private String message;

    /**
     * 客户端是否离线
     */
    private Boolean offline;

    public boolean success() {
        return status != null && status == 0;
    }
}
