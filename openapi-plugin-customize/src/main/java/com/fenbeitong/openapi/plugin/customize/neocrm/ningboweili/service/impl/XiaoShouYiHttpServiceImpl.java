package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.constant.NingBoWeiLiConstant;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.XiaoShouYiHttpService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/5/19
 */
@Slf4j
@ServiceAspect
@Service
public class XiaoShouYiHttpServiceImpl implements XiaoShouYiHttpService {

    @Override
    public String query(String sql,String token) {
        Map<String, Object> param = new HashMap<>();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        param.put("q",sql);
        return RestHttpUtils.get(NingBoWeiLiConstant.QUERY_URL,httpHeaders, param);
    }

}
