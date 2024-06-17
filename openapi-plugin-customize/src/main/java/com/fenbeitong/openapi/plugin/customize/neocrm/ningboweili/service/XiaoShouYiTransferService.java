package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service;

import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLiCommonDetailsDto;

/**
 * 销售易转换服务
 * @Auther zhang.peng
 * @Date 2021/5/19
 */
public interface XiaoShouYiTransferService {

    NingBoWeiLiCommonDetailsDto fetchDtoInfo(String sql,String token);

}
