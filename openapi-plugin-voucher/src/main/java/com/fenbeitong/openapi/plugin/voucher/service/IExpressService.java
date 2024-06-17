package com.fenbeitong.openapi.plugin.voucher.service;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import java.util.Map;

/**
 * <p>Title: IExpressService</p>
 * <p>Description: 条件表达式服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/26 3:10 PM
 */
public interface IExpressService {

    /**
     * 获取执行器
     *
     * @return
     */
    ExpressRunner getExpressRunner();


    /**
     * 执行脚本
     *
     * @param runner
     * @param context
     * @return
     */
    Object executeScript(ExpressRunner runner, DefaultContext<String, Object> context, String script);

    /**
     * 获取匹配条件的值
     *
     * @param expressId
     * @param expressRunner
     * @param context
     * @param srcData
     * @param beforeRowScript
     * @return
     */
    Map<String, Object> getValue(String expressId, ExpressRunner expressRunner, DefaultContext<String, Object> context, Map<String, Object> srcData, String beforeRowScript);
}
