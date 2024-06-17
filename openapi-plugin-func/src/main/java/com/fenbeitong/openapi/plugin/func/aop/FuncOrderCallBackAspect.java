package com.fenbeitong.openapi.plugin.func.aop;

import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.callback.MongoOrderEventService;
import com.fenbeitong.openapi.plugin.func.callback.OrderEventRockMqService;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: FuncOrderCallBackAspect</p>
 * <p>Description: 订单回调拦截器</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/1 7:37 PM
 */
@SuppressWarnings("all")
@Component
@Aspect
@Slf4j
public class FuncOrderCallBackAspect {

    @Autowired
    private MongoOrderEventService mongoOrderEventService;

    @Autowired
    private OrderEventRockMqService orderEventRockMqService;

    @Autowired
    private ThirdCallbackConfDao callbackConfDao;

    @Pointcut("@annotation(com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack)")
    public void pointCut() {

    }

    @Around(value = "pointCut()")
    public Object aroundOrderEvent(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        Map eventMap = JsonUtils.toObj(JsonUtils.toJson(args[0]), Map.class);
        Object[] extArgs = args.length >= 2 ? (Object[]) args[1] : null;
        boolean retry = extArgs == null ? false : extArgs.length > 0 ? Boolean.valueOf(extArgs[0].toString()) : false;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        FuncOrderCallBack annotation = method.getAnnotation(FuncOrderCallBack.class);
        Integer status = (Integer) MapUtils.getValueByExpress(eventMap, annotation.status());
        int[] statusList = annotation.statusList();
        //状态为空或者
        if (status == null || !Arrays.contains(statusList, status)) {
            log.info("当前事件消息为 {} , 状态为 {}",JsonUtils.toJson(eventMap),status);
            return true;
        }
        String companyId = (String) MapUtils.getValueByExpress(eventMap, annotation.companyId());
        //新接入的消息为accountType，旧消息为orderType
        Integer orderType = MapUtils.getValueByExpress(eventMap, "orderType") == null ? null : Integer.parseInt(MapUtils.getValueByExpress(eventMap, "orderType").toString());

        ThirdCallbackConf thirdCallbackConf = companyId == null ? null : callbackConfDao.queryByCompanyIdAndCallBackType(companyId, annotation.callbackType());
        Integer callbackRange = null;
        if (thirdCallbackConf != null) {
            String version = annotation.version();
            String jsonParam = thirdCallbackConf.getJsonParam();
            Map jsonMap = JsonUtils.toObj(jsonParam, Map.class);
            String configVersion = jsonMap == null ? null : (String) jsonMap.get(annotation.type() + "_version");
            //取出因公因私配置
            callbackRange = jsonMap == null ? null : NumericUtils.obj2int(jsonMap.get("callback_range"), 1);
            //版本不一致不进行推送
            if (!ObjectUtils.isEmpty(configVersion) && !version.equals(configVersion)) {
                log.info("当前事件消息为 {}, 版本不一致不进行推送 {}",JsonUtils.toJson(eventMap),jsonParam);
                return true;
            }
        }
        //消息因公时放行，拦截的情况：1、表和配置不一致，2、消息因私+表无
        if (!ObjectUtils.isEmpty(orderType)) {
            //查表取出配置  消息因私+表无，拦截
            if (ObjectUtils.isEmpty(callbackRange) && Integer.valueOf(2).equals(orderType)) {
                log.info("订单过滤,订单类型表配置：{},事件通知:{}", callbackRange, orderType);
                return true;
            }
            //表和配置不一致，拦截
            if (!Integer.valueOf(3).equals(callbackRange) && !ObjectUtils.isEmpty(callbackRange) && !orderType.equals(callbackRange)) {
                log.info("订单过滤,订单类型表配置：{},事件通知:{}", callbackRange, orderType);
                return true;
            }
        }
        try {
            return thirdCallbackConf == null ? true : process(pjp, args[0], eventMap, method, annotation, retry);
        } catch (Throwable throwable) {
            log.warn("订单事件存盘失败", throwable);
        }
        return false;
    }

    private Object process(ProceedingJoinPoint pjp, Object event, Map eventMap, Method method, FuncOrderCallBack annotation, boolean retry) throws Throwable {
        boolean ignore = isIgnore(eventMap, annotation);
        // 如果根据表达式结果判断是需要检查 再走下面的逻辑
        if (!ignore) {
            String[] logicKeys = annotation.logicKeys();
            //重试 不走消息落库
            if (!retry && !ObjectUtils.isEmpty(logicKeys)) {
                List<String> logicKeyValues = Lists.newArrayList();
                for (String logicKey : logicKeys) {
                    logicKeyValues.add(StringUtils.obj2str(eventMap.get(logicKey)));
                }
                String orderId = (String) eventMap.get(annotation.orderId());
                Integer status = NumericUtils.obj2int(eventMap.get(annotation.status()));
                String collectionName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, event.getClass().getSimpleName());
                String id = mongoOrderEventService.saveEvent(event, collectionName, logicKeyValues, method.getDeclaringClass().getName(), method.getName(), orderId, status);
                orderEventRockMqService.sendCheckMsg(collectionName, id, 0, NumericUtils.obj2int(eventMap.get(annotation.status())));
            }
        }

        return pjp.proceed();
    }

    /**
     * 判断是否要忽略
     *
     * @param eventMap
     * @param annotation
     * @return
     */
    private boolean isIgnore(Map eventMap, FuncOrderCallBack annotation) {
        boolean ignore = false;
        String express = annotation.ignoreExpress();
        if (StringUtils.isBlank(express)) {
            // 如果是空 不忽略
            ignore = false;
        } else {
            // 不为空 通过表达式进行判断 是否要忽略
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext();
            Map<String, Object> dataMap = Maps.newHashMap();
            context.setVariable("data", eventMap);
            Boolean value = parser.parseExpression(express).getValue(context, Boolean.class);
            ignore = value != null && value;
        }
        return ignore;
    }
}
