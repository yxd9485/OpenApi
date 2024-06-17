package com.fenbeitong.openapi.plugin.customize.shankun.dto;

import lombok.Data;

/**
 * <p>Title: BillConfig51TalkSyncReqDTO</p>
 * <p>Description: 51talk账单配置参数请求</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/22 2:38 PM
 */
@Data
public class BillConfig51TalkSyncReqDTO {

    private String url;

    private String username;

    private String secret;
}
