package com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto;

import lombok.Data;

/**
 * <p>Title: ConfigDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/4/26 11:56 上午
 */
@Data
public class YunTianJobConfigDto {
    private String companyId;
    private String companyName;
    private String rootId;
    private String url;
    private String secret;
    private boolean isForceUpdate;
    /**
     * 1不限2限制
     */
    private int usableRange = 1;

}
