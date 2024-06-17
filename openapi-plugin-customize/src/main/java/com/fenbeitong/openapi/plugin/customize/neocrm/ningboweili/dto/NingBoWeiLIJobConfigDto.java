package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto;

import lombok.Data;

/**
 * <p>Title: HyprocaJobConfigDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-03 18:44
 */

@Data
public class NingBoWeiLIJobConfigDto {

    String companyId;
    String corpId;
    /**
     * 请求数据的URL
     */
    String url;

}
