package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLiCommonDetailsDto;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.XiaoShouYiHttpService;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.XiaoShouYiTransferService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @Auther zhang.peng
 * @Date 2021/5/19
 */
@Slf4j
@ServiceAspect
@Service
public class XiaoShouYiTransferServiceImpl implements XiaoShouYiTransferService {

    @Autowired
    private XiaoShouYiHttpService xiaoShouYiHttpService;

    @Override
    public NingBoWeiLiCommonDetailsDto fetchDtoInfo(String sql,String token) {
        XiaoShouYiHttpService xiaoShouYiHttpService = new XiaoShouYiHttpServiceImpl();
        String result = xiaoShouYiHttpService.query(sql,token);
        return JsonUtils.toObj(result, NingBoWeiLiCommonDetailsDto.class);
    }

}
