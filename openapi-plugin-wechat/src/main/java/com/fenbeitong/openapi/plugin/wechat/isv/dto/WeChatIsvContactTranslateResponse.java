package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-22 16:50:12
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvContactTranslateResponse {

    private Integer errcode;

    private String errmsg;

    private String jobid;


}