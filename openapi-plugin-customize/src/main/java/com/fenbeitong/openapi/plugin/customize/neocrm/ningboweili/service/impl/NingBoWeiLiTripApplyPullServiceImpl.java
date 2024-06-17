package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.customize.common.service.impl.PrimaryCommonImpl;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLIJobConfigDto;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLiTripApplyDetailsDto;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.NingBoWeiLiTripApplyPullService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: HyprocaTripApplyServiceImpl</p>
 * <p>Description: 宁波伟立差旅审批拉取</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-03 15:12
 */
@ServiceAspect
@Service
@Slf4j
public class NingBoWeiLiTripApplyPullServiceImpl extends PrimaryCommonImpl implements NingBoWeiLiTripApplyPullService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    CommonServiceImpl commonService;

    @Autowired
    AuthDefinitionDao authDefinitionDao;

    @Autowired
    private ITaskService taskService;

    private static final String NINGBOWEILI_APPLY_PRE = "ningboweili_apply_id_";


    @Override
    public String tripApplyPull(NingBoWeiLIJobConfigDto ningBoWeiLIJobConfigDto) {
        // 获取数据
        String data = getData(ningBoWeiLIJobConfigDto.getUrl(), ningBoWeiLIJobConfigDto.getCompanyId());

        NingBoWeiLiTripApplyDetailsDto ningBoWeiLiTripApplyDetailsDto = JSONObject.parseObject(data, NingBoWeiLiTripApplyDetailsDto.class);

        if (!ObjectUtils.isEmpty(ningBoWeiLiTripApplyDetailsDto) && "200".equals(ningBoWeiLiTripApplyDetailsDto.getCode().toString())) {
            // 数据过滤
            List<NingBoWeiLiTripApplyDetailsDto.Records> list = dataFileter(ningBoWeiLiTripApplyDetailsDto, ningBoWeiLIJobConfigDto);
            list.forEach(t -> {
                // 订单
                final String hyprocaApplyKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, NINGBOWEILI_APPLY_PRE + t.getCustomItem7__c());
                // 查询数据
                String applyIdKey = (String) redisTemplate.opsForValue().get(hyprocaApplyKey);
                if (StringUtils.isBlank(applyIdKey)) {
                    insertTask(t, ningBoWeiLIJobConfigDto.getCorpId());
                    redisTemplate.opsForValue().set(hyprocaApplyKey, t.getCustomItem7__c(), 30, TimeUnit.MINUTES);
                }
            });
        }

        return "success";
    }


    /**
     * 数据过滤
     */

    public List<NingBoWeiLiTripApplyDetailsDto.Records> dataFileter(NingBoWeiLiTripApplyDetailsDto ningBoWeiLiTripApplyDetailsDto, NingBoWeiLIJobConfigDto ningBoWeiLIJobConfigDto) {
        log.info("ningBoWeiLiTripApplyDetailsDto:{}", JsonUtils.toJson(ningBoWeiLiTripApplyDetailsDto));
        List<NingBoWeiLiTripApplyDetailsDto.Records> list = new ArrayList<>();
        ningBoWeiLiTripApplyDetailsDto.getResult().getRecords().forEach(records -> {
            // 获取详情信息
            List<Integer> integerList = records.getCustomItem6__c();
            // 2:汽车 3:火车 4:国内机票 5:国外机票
            List<Integer> typeList = new ArrayList<Integer>() {{
                add(2);
                add(3);
                add(4);
                add(5);
            }};
            typeList.retainAll(integerList);
            if (typeList.size() > 0) {
                list.add(records);
            }
        });

        return list;

    }

    /**
     * 获取数据
     */
    public String getData(String url, String companyId) {
        // 获取token
        String token = commonService.getToken(companyId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        url = url + " and updatedAt between \"" + DateUtils.beforeMineToNowTime(5) + "\" and \"" + System.currentTimeMillis() + "\"";
        // 获取审批单数据
        String data = RestHttpUtils.get(url, httpHeaders, null);
        log.info("宁波伟立审批单数据:{}", data);
        return data;
    }

    /**
     * 添加task执行任务
     */
    public void insertTask(NingBoWeiLiTripApplyDetailsDto.Records Records, String corpId) {
        Map<String, Object> eventMsg = Maps.newHashMap();
        eventMsg.put("EventType", TaskType.NINGBOWEILI_TRIP_APPLY_CREATE.getKey());
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", System.currentTimeMillis());
        eventMsg.put("DataId", Records.getCustomItem7__c());
        eventMsg.put("DataContent", JsonUtils.toJson(Records));
        List<String> taskList = new ArrayList<>();
        taskList.add(TaskType.NINGBOWEILI_TRIP_APPLY_CREATE.getKey());
        taskService.genTask(eventMsg, taskList);
    }


}
