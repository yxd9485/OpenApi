package com.fenbeitong.openapi.plugin.feishu.common.service.common;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuServiceTypeConstant;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.FeiShuFormDataBuildService;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.impl.DinnerFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.feishu.common.service.form.impl.MallFormDataServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
@ServiceAspect
@Service
public class FeiShuNoticeFactoryService {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    public Map<String, String> getNoticeResult(String serviceType, Map map){
        if (FeiShuServiceTypeConstant.MALL.equals(serviceType)){
            return commonApplyService.parseFbtMallApplyNotice(map);
        }
        if (FeiShuServiceTypeConstant.DINNER.equals(serviceType)){
            return commonApplyService.parseFbtDinnerApplyNotice(map);
        }
        return null;
    }
}
