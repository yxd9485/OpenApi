package com.fenbeitong.openapi.plugin.feishu.common.service.common;

import com.fenbeitong.openapi.plugin.feishu.common.service.apply.FeishuProcessApplyService;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/6/29 下午12:02
 */
public class ApplyProcessFactory {

    private static Map<Integer , FeishuProcessApplyService> applyFormHandlerMap = new HashMap<>();

    public static FeishuProcessApplyService getStrategyMap(Integer applyType){
        return applyFormHandlerMap.get( applyType );
    }

    public static void registerHandler(Integer applyType , FeishuProcessApplyService handler){
        applyFormHandlerMap.put( applyType , handler );
    }



}
