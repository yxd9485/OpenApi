package com.fenbeitong.openapi.plugin.voucher.service.impl;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.voucher.dao.OpenExpressConfigDetailDao;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenExpressConfigDetail;
import com.fenbeitong.openapi.plugin.voucher.service.IExpressService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ql.util.express.*;
import com.ql.util.express.instruction.op.OperatorBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: ExpressServiceImpl</p>
 * <p>Description: 表达式服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/26 3:29 PM
 */
@Service
@Slf4j
public class ExpressServiceImpl implements IExpressService {

    @Autowired
    private OpenExpressConfigDetailDao openExpressConfigDetailDao;

    private static final ThreadPoolExecutor EXPRESS_EXECUTOR = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactory() {

        AtomicInteger poolNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ExpressServiceThreadPool-" + poolNumber.getAndIncrement());
        }
    });

    @SuppressWarnings("all")
    @Override
    public ExpressRunner getExpressRunner() {
        ExpressRunner runner = new ExpressRunner(true, false);
        try {
            runner.setShortCircuit(true);
            runner.addFunctionOfClassMethod("joinStr", StringUtils.class.getName(), "joinStr",
                    new Class[]{String.class, List.class}, null);
            runner.addFunction("contextPut", new OperatorBase() {
                @Override
                public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
                    Object value = list.get(1);
                    String key = list.get(0).toString();
                    parent.put(key, ((OperateData) value).getObjectInner(parent));
                    return null;
                }
            });
        } catch (Exception e) {
        }
        return runner;
    }

    @Override
    public Object executeScript(ExpressRunner runner, DefaultContext<String, Object> context, String script) {
        try {
            if (script == null) {
                return null;
            }
            return runner.execute(script, context, null, false, false);
        } catch (Exception e) {
            log.warn("执行脚本失败，脚本:" + script + "，源数据:" + JsonUtils.toJson(context) + "\n", e);
        }
        return null;
    }

    @Override
    public Map<String, Object> getValue(String expressId, ExpressRunner expressRunner, DefaultContext<String, Object> context, Map<String, Object> srcData, String beforeRowScript) {
        executeScript(expressRunner, context, beforeRowScript);
        Map<String, Object> condition = Maps.newHashMap();
        condition.put("mainId", expressId);
        List<Future<Map<String, Object>>> futureList = Lists.newArrayList();
        List<OpenExpressConfigDetail> detailList = openExpressConfigDetailDao.listOpenExpressConfigDetail(condition);
        for (OpenExpressConfigDetail detail : detailList) {
            Future<Map<String, Object>> future = EXPRESS_EXECUTOR.submit(() -> {
                String conditionExpress = detail.getConditionExpress();
                Object match = executeScript(expressRunner, context, conditionExpress);
                if (match != null && (Boolean) match) {
                    String value = detail.getMatchValue();
                    if (value != null) {
                        Map<String, Object> matchValue = JsonUtils.toObj(value, Map.class);
                        return matchValue;
                    }
                }
                return null;
            });
            futureList.add(future);
        }
        Map<String, Object> matchValue = futureList.stream().map(f -> {
            try {
                return f.get();
            } catch (Exception e) {
            }
            return null;
        }).filter(v -> !ObjectUtils.isEmpty(v)).findFirst().orElse(Maps.newHashMap());
        return matchValue == null ? Maps.newHashMap() : matchValue;
    }
}
