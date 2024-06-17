package com.fenbeitong.openapi.plugin.yiduijie.dto;

import lombok.Data;

/**
 * <p>Title: YiDuiJieClient</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 6:08 PM
 */
@Data
public class YiDuiJieClient {

    private String accessToken;

    private String clientId;

    private String name;

    private Boolean cloud;

    private String creator;

    private Long lastUpdated;

}
