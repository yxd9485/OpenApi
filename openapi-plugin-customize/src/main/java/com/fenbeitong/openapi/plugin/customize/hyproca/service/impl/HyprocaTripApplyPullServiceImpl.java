package com.fenbeitong.openapi.plugin.customize.hyproca.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCustonmConstant;
import com.fenbeitong.openapi.plugin.customize.common.service.impl.PrimaryCommonImpl;
import com.fenbeitong.openapi.plugin.customize.hyproca.dto.HyprocaJobConfigDto;
import com.fenbeitong.openapi.plugin.customize.hyproca.dto.HyprocaTripApplyDto;
import com.fenbeitong.openapi.plugin.customize.hyproca.service.HyprocaTripApplyPullService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: HyprocaTripApplyServiceImpl</p>
 * <p>Description: 海普诺凯差旅审批拉取</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-03 15:12
 */
@ServiceAspect
@Service
@Slf4j
public class HyprocaTripApplyPullServiceImpl extends PrimaryCommonImpl implements HyprocaTripApplyPullService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    AuthDefinitionDao authDefinitionDao;

    @Autowired
    private ITaskService taskService;

    private static final String HYPROCA_APPLY_PRE = "hyproca_apply_id_";


    @Override
    public String tripApplyPull(HyprocaJobConfigDto hyprocaJobConfigDto) {
        if (ObjectUtils.isEmpty(hyprocaJobConfigDto)) {
            throw new FinhubException(1, "hyprocaJobConfigDto不能为空");
        }
        String data = getData(hyprocaJobConfigDto);
        JSONArray jsonArray = JSONObject.parseArray(data);
        List<HyprocaTripApplyDto> thipApplyDtoList = jsonArray.toJavaList(HyprocaTripApplyDto.class);
        if (!ObjectUtils.isEmpty(thipApplyDtoList)) {
            thipApplyDtoList.forEach(t -> {
                // 订单
                final String hyprocaApplyKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, HYPROCA_APPLY_PRE.concat(t.getTripId()));
                // 查询数据
                String applyIdKey = (String) redisTemplate.opsForValue().get(hyprocaApplyKey);
                if (StringUtils.isBlank(applyIdKey)) {
                    insertTask(t, hyprocaJobConfigDto.getCorpId());
                    redisTemplate.opsForValue().set(hyprocaApplyKey, t.getTripId(), 7, TimeUnit.DAYS);
                }
            });
        }

        return "success";
    }


    /**
     * 获取数据
     */
    public String getData(HyprocaJobConfigDto hyprocaJobConfigDto) {
        // 获取审批单数据
        Map<String, String> reqJson = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(hyprocaJobConfigDto.getStartDate()) && !ObjectUtils.isEmpty(hyprocaJobConfigDto.getEndDate())) {
            reqJson.put("date1", hyprocaJobConfigDto.getStartDate());
            reqJson.put("date2", hyprocaJobConfigDto.getEndDate());
        } else {
            reqJson.put("date1", DateUtils.beforeMineToNowDate(hyprocaJobConfigDto.getFrequency()));
            reqJson.put("date2", DateUtils.toSimpleStr(new Date()));
        }

        String data = getData(hyprocaJobConfigDto.getUrl(), reqJson, OpenCustonmConstant.reqType.POST, null,null,hyprocaJobConfigDto.getCompanyId());
        log.info("海普诺凯审批单数据:{}", data);
        return data;
    }

    /**
     * 添加task执行任务
     */
    public void insertTask(HyprocaTripApplyDto thipApplyDto, String corpId) {
        Map<String, Object> eventMsg = Maps.newHashMap();
        eventMsg.put("EventType", TaskType.HYPROCA_TRIP_APPLY_CREATE.getKey());
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", System.currentTimeMillis());
        //三方审批单id修改为tripId
        String dataId = StringUtils.isBlank(thipApplyDto.getTripId())?thipApplyDto.getWfinstanceId():thipApplyDto.getTripId();
        eventMsg.put("DataId", dataId);
        eventMsg.put("DataContent", JsonUtils.toJson(thipApplyDto));
        List<String> taskList = new ArrayList<>();
        taskList.add(TaskType.HYPROCA_TRIP_APPLY_CREATE.getKey());
        taskService.genTask(eventMsg, taskList);
    }


}
