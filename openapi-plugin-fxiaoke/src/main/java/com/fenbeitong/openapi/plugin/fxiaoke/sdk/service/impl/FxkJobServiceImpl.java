package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeTask;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.support.util.RedisDistributedLock;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * 具体执行纷享销客任务
 */
@ServiceAspect
@Service
@Slf4j
public class FxkJobServiceImpl  implements ApplicationContextAware {
//extends CommonApplyServiceImpl

    private static final int TASK_LIMIT = 1000;
    private static final int TASK_POOL = 16;

    private static Map<String, ITaskHandler> handlerMap;
    private static ThreadFactory taskThreadFactory = new ThreadFactoryBuilder().setNameFormat("callback-task-pool-%d").build();
    private static ExecutorService executor = new ThreadPoolExecutor(TASK_POOL, TASK_POOL, 0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), taskThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    FxkTaskService fxkTaskService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ITaskHandler> taskHandlerMap = applicationContext.getBeansOfType(ITaskHandler.class);
        log.info("初始化纷享销客任务处理器开始");
        handlerMap = Maps.newHashMap();
        if (taskHandlerMap == null || taskHandlerMap.isEmpty()) {
            log.info("无纷享销客任务处理器实现");
            return;
        }
        for (ITaskHandler handler : taskHandlerMap.values()) {
            if (handler.getTaskType() == null) {
                log.info("纷享销客任务处理器[{}]的任务类型不能为空", handler.getClass().getName());
            } else {
                log.info("发现纷享销客任务类型为[{}]的处理器[{}]", handler.getTaskType().getKey(), handler.getClass().getName());
                handlerMap.put(handler.getTaskType().getKey(), handler);
            }
        }
        log.info("[TaskDispatcher] 初始化任务处理器结束");
    }


    public void start() {
        //可以不用处理，，开始执行前已经把状态进行更新
//        fxkTaskService.updateProcessingTask2Pending();
        int taskNums = fxkTaskService.countFxiaokeNeedProcessedTask();
        log.info("开始处理纷享销客任务，待执行任务量:{}, 批次大小:{}", taskNums, TASK_LIMIT);
        List<FxiaokeTask> taskList = fxkTaskService.getFxiaokeNeedProcessedTaskList(TASK_LIMIT);
        //转换为普通task
        if (CollectionUtils.isEmpty(taskList)) {
            log.info("没有等待处理的任务");
            return;
        }
        // 按照corpId分组，创建线程执行，同一企业下的任务需要按照顺序执行
        Map<String, List<Task>> taskListMap = taskList.stream().collect(groupingBy(Task::getCorpId));

        Set<String> keySet = taskListMap.keySet();
        List<CompletableFuture> futures = new ArrayList<>();
        for (String corp : keySet) {
            List<Task> executeList = taskListMap.get(corp);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Task task : executeList) {
                    ITaskHandler handler = this.getHandler(task.getTaskType());
                    if (handler == null) {
                        log.info("没有找到纷享销客对应的任务处理器，taskType: {}", task.getTaskType());
                        continue;
                    }
                    // 加锁
                    String lockKey = RedisDistributedLock.REDIS_LOCK_KEY_PREFIX_TASK + task.getId();
                    Long lockTime = RedisDistributionLock.lock(lockKey, redisTemplate);
                    if (lockTime > 0) {
                        try {
                            log.info("任务[{}]开始执行， 任务类型: {}", task.getId(), task.getTaskType());
                            fxkTaskService.beginTask(task.getId());
                            TaskResult taskResult = handler.execute(task);
                            fxkTaskService.saveTask(task.getId(), taskResult.getResult());
                        } catch (Exception e) {
                            log.info("任务[{}]执行失败, result: {}", task.getId(), e);
                            //失败后更新任务状态
                            fxkTaskService.updateTask(task.getId(), ExceptionUtils.getStackTraceAsString(e));
                        } finally {
                            RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
                        }
                    } else {
                        log.info("未获取到锁，taskId: {}", task.getId());
                        continue;
                    }
                }
            }, executor);
            futures.add(future);
        }

        // 所有任务处理完成后，执行下一轮
        for (CompletableFuture future : futures) {
            try {
                // 超时时间设置为每次执行最大任务数*5s
                future.get(TASK_LIMIT * 5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("线程执行中断", e);
                Thread.currentThread().interrupt();
            } catch (TimeoutException e) {
                log.error("线程执行超时", e);
            } catch (ExecutionException e) {
                log.error("线程执行出现异常", e);
            }
        }
        log.info("任务执行完毕，数量: {} ", taskList.size());
    }

    private ITaskHandler getHandler(String taskType) {
        return handlerMap.get(taskType);
    }

    /**
     * 查询数据
     *
     * @return
     */
    public String executFxkApply() {
        start();
        return null;
    }


}
