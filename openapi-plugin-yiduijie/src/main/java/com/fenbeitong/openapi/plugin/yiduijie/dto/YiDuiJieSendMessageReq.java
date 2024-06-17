package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: YiDuiJieSendMessageReq</p>
 * <p>Description: 易对接推送消息请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 6:32 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiDuiJieSendMessageReq {

    private String operator;

    private String appId;

    private List data;
}
