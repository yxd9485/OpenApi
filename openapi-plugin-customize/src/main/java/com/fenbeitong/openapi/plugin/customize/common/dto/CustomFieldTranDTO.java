package com.fenbeitong.openapi.plugin.customize.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ctl
 * @date 2021/10/29
 */
@Data
public class CustomFieldTranDTO implements Serializable {

    /**
     * 公司id
     */
    private String companyId;

    /**
     * oss 的 url
     */
    private String url;
}
