package com.fenbeitong.openapi.plugin.wechat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatUserIdGetRespDTO {
    private int errcode;
    private String errmsg;
    private String userid;

}
