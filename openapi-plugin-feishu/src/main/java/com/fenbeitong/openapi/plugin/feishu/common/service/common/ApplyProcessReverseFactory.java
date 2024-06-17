package com.fenbeitong.openapi.plugin.feishu.common.service.common;

import com.fenbeitong.openapi.plugin.feishu.common.service.apply.FeishuProcessReverseApplyService;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/6/29 下午12:02
 */
public class ApplyProcessReverseFactory {

    private static Map<Integer , FeishuProcessReverseApplyService> applyFormHandlerMap = new HashMap<>();

    public static FeishuProcessReverseApplyService getStrategyMap(Integer applyType){
        return applyFormHandlerMap.get( applyType );
    }

    public static void registerHandler(Integer applyType , FeishuProcessReverseApplyService handler){
        applyFormHandlerMap.put( applyType , handler );
    }



}
