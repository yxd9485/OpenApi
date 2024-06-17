package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenApplyListDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenOutwardApplyListDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenApplyPullDataService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiService;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.support.common.dao.BeisenOutwardCityConfigDao;
import com.fenbeitong.openapi.plugin.support.common.entity.BeisenOutwardCityConfig;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.ITaskService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 北森审批单拉取的服务
 *
 * @author xiaowei
 * @data 2020/07/28
 */
@ServiceAspect
@Service
@Slf4j
public class BeisenApplyPullDataServiceImpl implements BeisenApplyPullDataService {


    @Autowired
    private ITaskService taskService;

    @Autowired
    private BeisenApiService beisenApiService;
    @Autowired
    private BeisenOutwardCityConfigDao beisenOutwardCityConfigDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String BEISEN_APPLY_PRE = "beisen_apply_id_";
    private static final String BEISEN_OUTWARD_APPLY_PRE = "beisen_outward_apply_id_";


    @Override
    public boolean pullApplyData(BeisenParamConfig beisenParamConfig) {
        List<BeisenApplyListDTO.BusinessList> applyData = beisenApiService.getApplyData(beisenParamConfig);
        List<BeisenApplyListDTO.BusinessList> filterApplyDatas = applyData.stream().filter(e -> "通过".equals(e.getApproveStatus())).collect(Collectors.toList());
        if (filterApplyDatas != null && filterApplyDatas.size() > 0) {
            log.info("pull beisen apply data tenantId: {} , total size: {} approve size: {} ", beisenParamConfig.getTenantId(), applyData.size(), filterApplyDatas.size());
            filterApplyDatas.stream().forEach(a -> {
                final String beisenApplyKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, BEISEN_APPLY_PRE + a.getObjectId());
                String applyIdKey = (String) redisTemplate.opsForValue().get(beisenApplyKey);
                Map<String, String> carResult = new HashMap<>();
                if (StringUtils.isBlank(applyIdKey)) {
                    List<BeisenApplyListDTO.BusinessDetailsSync> businessDetailsSync = a.getBusinessDetailsSync();
                    List<BeisenApplyListDTO.BusinessDetailsSync> collects = businessDetailsSync.stream().filter(e -> beisenParamConfig.getTypeList().contains(e.getBusinessVehicle())).collect(Collectors.toList());
                    collects.sort((BeisenApplyListDTO.BusinessDetailsSync a1, BeisenApplyListDTO.BusinessDetailsSync a2) -> Integer.valueOf(DateUtils.toStr(a1.getStartDateTime(), "yyyyMMdd")) - Integer.valueOf(DateUtils.toStr(a2.getStartDateTime(), "yyyyMMdd")));
                    List<BeisenApplyListDTO.BusinessDetailsSync> resultList = new ArrayList<>();
                    for (int i = 0; i < collects.size(); i++) {
                        BeisenApplyListDTO.BusinessDetailsSync e = collects.get(i);
                        e.setTripType(beisenParamConfig.getTripType());
                        resultList.add(e);
                        if (beisenParamConfig.getTypeList().contains("5")) {//酒店
                            BeisenApplyListDTO.BusinessDetailsSync businessDetailsSyncN = new BeisenApplyListDTO.BusinessDetailsSync();
                            BeanUtils.copyProperties(e, businessDetailsSyncN);
                            businessDetailsSyncN.setBusinessVehicle("5");
                            resultList.add(businessDetailsSyncN);
                        }
                        if (beisenParamConfig.getTypeList().contains("6")) {
                            BeisenApplyListDTO.BusinessDetailsSync businessDetailsSyncDesCar = new BeisenApplyListDTO.BusinessDetailsSync();
                            BeanUtils.copyProperties(e, businessDetailsSyncDesCar);
                            businessDetailsSyncDesCar.setBusinessVehicle("6");
                            if (carResult.containsKey(businessDetailsSyncDesCar.getDestination()) &&
                                    carResult.get(businessDetailsSyncDesCar.getDestination()).equals(DateUtils.toStr(businessDetailsSyncDesCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncDesCar.getStopDateTime(), "yyyyMMdd")))) {
                            } else {
                                carResult.put(businessDetailsSyncDesCar.getDestination(), DateUtils.toStr(businessDetailsSyncDesCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncDesCar.getStopDateTime(), "yyyyMMdd")));
                                resultList.add(businessDetailsSyncDesCar);
                            }
                            if (beisenParamConfig.getStartCityCarFlag() != null && beisenParamConfig.getStartCityCarFlag()) {
                                BeisenApplyListDTO.BusinessDetailsSync businessDetailsSyncStartCar = new BeisenApplyListDTO.BusinessDetailsSync();
                                BeanUtils.copyProperties(e, businessDetailsSyncStartCar);
                                businessDetailsSyncStartCar.setDeparturePlace(e.getDestination());
                                businessDetailsSyncStartCar.setDestination(e.getDeparturePlace());
                                businessDetailsSyncStartCar.setBusinessVehicle("6");
                                if (carResult.containsKey(businessDetailsSyncStartCar.getDestination()) &&
                                        carResult.get(businessDetailsSyncStartCar.getDestination()).equals(DateUtils.toStr(businessDetailsSyncStartCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncStartCar.getStopDateTime(), "yyyyMMdd")))) {
                                } else {
                                    carResult.put(businessDetailsSyncStartCar.getDestination(), DateUtils.toStr(businessDetailsSyncStartCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncStartCar.getStopDateTime(), "yyyyMMdd")));
                                    resultList.add(businessDetailsSyncStartCar);
                                }
                            }
                        }
                    }
                    a.setBusinessDetailsSync(resultList);
                    Map<String, Object> eventMsg = new HashMap<>();
                    eventMsg.put("EventType", TaskType.BEISEN_APPROVAL_EVENT_CREATE.getKey());
                    eventMsg.put("CorpId", beisenParamConfig.getTenantId());
                    eventMsg.put("TimeStamp", System.currentTimeMillis());
                    eventMsg.put("DataId", a.getObjectId());
                    eventMsg.put("DataContent", JsonUtils.toJson(a));
                    List<String> taskList = new ArrayList<>();
                    taskList.add(TaskType.BEISEN_APPROVAL_EVENT_CREATE.getKey());
                    taskService.genTask(eventMsg, taskList);
                    redisTemplate.opsForValue().set(beisenApplyKey, a.getObjectId(), 480, TimeUnit.SECONDS);
                }
            });
        }
        return true;
    }

    @Override
    public boolean pullOutWardApplyData(BeisenParamConfig beisenParamConfig) {
        List<BeisenOutwardApplyListDTO.OutwardInfo> outWardApplyData = beisenApiService.getOutWardApplyData(beisenParamConfig);
        List<BeisenOutwardApplyListDTO.OutwardInfo> filterOutwardApplyDatas = outWardApplyData.stream().filter(e -> "1".equals(e.getApproveStatus())).collect(Collectors.toList());
        if (filterOutwardApplyDatas != null && filterOutwardApplyDatas.size() > 0) {
            log.info("pull beisen outward apply data tenantId: {} , total size: {} approve size: {} ", beisenParamConfig.getTenantId(), outWardApplyData.size(), filterOutwardApplyDatas.size());
            filterOutwardApplyDatas.stream().forEach(a -> {
                final String beisenOutwardApplyKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, BEISEN_OUTWARD_APPLY_PRE + a.getOId());
                String applyIdKey = (String) redisTemplate.opsForValue().get(beisenOutwardApplyKey);
                if (StringUtils.isBlank(applyIdKey)) {
                    a.setCityId(String.valueOf(a.getProperties().get(beisenParamConfig.getCityId())));
                    Map<String, Object> eventMsg = new HashMap<>();
                    eventMsg.put("EventType", TaskType.BEISEN_OUTWARD_EVENT_CREATE.getKey());
                    eventMsg.put("CorpId", beisenParamConfig.getTenantId());
                    eventMsg.put("TimeStamp", System.currentTimeMillis());
                    eventMsg.put("DataId", a.getOId());
                    eventMsg.put("DataContent", JsonUtils.toJson(a));
                    List<String> taskList = new ArrayList<>();
                    taskList.add(TaskType.BEISEN_OUTWARD_EVENT_CREATE.getKey());
                    taskService.genTask(eventMsg, taskList);
                    redisTemplate.opsForValue().set(beisenOutwardApplyKey, a.getOId(), 480, TimeUnit.SECONDS);
                }
            });
        }
        return true;
    }
}
