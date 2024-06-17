package com.fenbeitong.openapi.plugin.dingtalk.isv.service.job;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.OpenSyncBizDataDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IOpenSyncBizDataService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdata.IOpenSyncBizDataTaskHandler;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.ExceptionUtils;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * 钉钉云高优先级事件处理
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class OpenSyncBizDataJobService implements ApplicationContextAware {

    private static final int TASK_POOL = 16;

    private static Map<Integer, IOpenSyncBizDataTaskHandler> handlerMap;
    private static ThreadFactory taskThreadFactory = new ThreadFactoryBuilder().setNameFormat("callback-task-pool-%d").build();
    private static ExecutorService executor = new ThreadPoolExecutor(TASK_POOL, TASK_POOL, 0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), taskThreadFactory, new ThreadPoolExecutor.AbortPolicy());


    @Autowired
    private IOpenSyncBizDataService openSyncBizDataService;

    @Autowired
    private OpenSyncBizDataDao openSyncBizDataDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initTaskHandler(applicationContext);
    }

    public Map<Integer, IOpenSyncBizDataTaskHandler>  initTaskHandler(ApplicationContext applicationContext) {
        Map<String, IOpenSyncBizDataTaskHandler> taskHandlerMap = applicationContext.getBeansOfType(IOpenSyncBizDataTaskHandler.class);
        log.info("初始化任务处理器开始");
        handlerMap = Maps.newHashMap();
        if (taskHandlerMap == null || taskHandlerMap.isEmpty()) {
            log.info("无任务处理器实现");
            return null;
        }
        for (IOpenSyncBizDataTaskHandler handler : taskHandlerMap.values()) {
            if (handler.getTaskType() == null) {
                log.info("任务处理器[{}]的任务类型不能为空", handler.getClass().getName());
            } else {
                log.info("发现任务类型为[{}]的处理器[{}]", handler.getTaskType().getKey(), handler.getClass().getName());
                handlerMap.put(handler.getTaskType().getKey(), handler);
            }
        }
        log.info("[TaskDispatcher] 初始化任务处理器结束");
        return handlerMap;
    }

    public void start() {
        Map<String, Object> condition = new HashMap<>();
        List<OpenSyncBizData> openSyncBizData = openSyncBizDataService.listOpenSyncBizData(condition);
        if (CollectionUtils.isEmpty(openSyncBizData)) {
            log.info("没有等待处理的任务");
            return;
        }
        log.info("开始处理回调任务，待执行任务量:{}", openSyncBizData.size());
        // 按照corpId分组，创建线程执行，同一企业下的任务需要按照顺序执行
        Map<String, List<OpenSyncBizData>> taskListMap = openSyncBizData.stream().collect(groupingBy(OpenSyncBizData::getCorpId));
        Set<String> keySet = taskListMap.keySet();
        for (String corp : keySet) {
            List<OpenSyncBizData> executeList = taskListMap.get(corp);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (OpenSyncBizData task : executeList) {
                    IOpenSyncBizDataTaskHandler handler = this.getHandler(task.getBizType());
                    if (handler == null) {
                        log.info("没有找到对应的任务处理器，taskType: {}", task.getBizType());
                        continue;
                    }
                    // 加锁
                    String lockKey = MessageFormat.format(RedisKeyConstant.TASK_REDIS_KEY, task.getId());
                    Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
                    if (lockTime > 0) {
                        try {
                            log.info("任务[{}]开始执行， 任务类型: {}", task.getId(), task.getBizType());
                            task = openSyncBizDataDao.getById(task.getId());
                            if (task == null) {
                                log.info("任务[{}]已被处理", task.getId());
                                continue;
                            }
                            TaskResult taskResult = handler.execute(task);
                            openSyncBizDataService.saveTask(task.getId(), taskResult.getResult());
                        } catch (Exception e) {
                            log.info("任务[{}]执行失败, result: {}", task.getId(), e);
                            openSyncBizDataService.updateTask(task.getId(), ExceptionUtils.getStackTraceAsString(e));
                        } finally {
                            RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
                        }
                    } else {
                        log.info("未获取到锁，taskId: {}", task.getId());
                        continue;
                    }
                }
            }, executor);
        }
        log.info("任务执行完毕，数量: {} ", openSyncBizData.size());
    }

    private IOpenSyncBizDataTaskHandler getHandler(Integer bizType) {
        return handlerMap.get(bizType);
    }

}
