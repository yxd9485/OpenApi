package com.fenbeitong.openapi.plugin.feishu.common.service.common;

import com.fenbeitong.openapi.plugin.feishu.common.service.form.ParseApplyFormService;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/6/29 下午12:02
 */
public class ApplyFormFactory {

    private static Map<String , ParseApplyFormService> applyFormHandlerMap = new HashMap<>();

    public static ParseApplyFormService getStrategyMap(String applyType){
        return applyFormHandlerMap.get( applyType );
    }

    public static void registerHandler(String applyType , ParseApplyFormService handler){
        applyFormHandlerMap.put( applyType , handler );
    }



}
