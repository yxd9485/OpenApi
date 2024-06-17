package com.fenbeitong.openapi.plugin.customize.zhiou.service.landray;

import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.zhiou.constant.ApplyStateEnum;
import com.fenbeitong.openapi.plugin.customize.zhiou.constant.ZhiouConstant;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.ApplyDetailDTO;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.ApplyRequestDTO;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.ApplyTripDTO;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.OpenLandrayEkpConfig;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.ZhiouNonTravelApplyService;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.impl.ZhiouOpenApiAuthServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LandrayApplyServiceImpl
 * @Description 推送蓝凌审批实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Component
@Slf4j
public class LandrayApplyServiceImpl {

    @Autowired
    private ZhiouNonTravelApplyService zhiouNonTravelApplyService;
    @Autowired
    private ZhiouOpenApiAuthServiceImpl openApiAuthService;

    /**
     * 推送审批单详情
     *
     * @param applyRequestDTO  审批单参数
     * @param applyDetail      审批单详情
     * @param landrayEkpConfig 蓝凌配置表
     * @return resultMap 返回信息map
     */
    public Map<String, Object> pushApplyDetails(ApplyRequestDTO applyRequestDTO, Map<String, Object> applyDetail, OpenLandrayEkpConfig landrayEkpConfig) {
        Map<String, Object> resultMap = Maps.newHashMap();
        //重试蓝凌审批推送标识为否时直接返回成功，不需要再调蓝凌接口
        if (!ObjectUtils.isEmpty(applyRequestDTO.isPushLandraySuccess()) && applyRequestDTO.isPushLandraySuccess()) {
            resultMap.put("success", true);
        } else {
            Integer applyState = applyRequestDTO.getApplyState();
            ApplyDetailDTO apply = JsonUtils.toObj(JsonUtils.toJson(applyDetail.get("apply")), ApplyDetailDTO.class);
            //将行程列表中的出发城市信息放到目的城市中
            List<Map<String, Object>> tripList = (List<Map<String, Object>>) applyDetail.get("trip_list");
            ApplyTripDTO tripDTO = JsonUtils.toObj(JsonUtils.toJson(tripList.get(0)), ApplyTripDTO.class);
            tripDTO.setArrivalCityId(tripDTO.getStartCityId());
            tripDTO.setArrivalCityName(tripDTO.getStartCityName());
            Map<String,Object> trip = JsonUtils.toObj(JsonUtils.toJson(tripDTO),Map.class);
            tripList = Lists.newArrayList(trip);
            applyDetail.put("trip_list",tripList);
            //调用蓝凌接口
            if (ApplyStateEnum.APPLY_PASS.getCode().equals(applyState)) {
                //判断是否已变更
                if (!StringUtils.isEmpty(apply.getParentId()) && !apply.getParentId().equals(applyRequestDTO.getApplyId())) {
                    //已变更
                    try {
                        //组装查询原审批单的参数
                        ApplyRequestDTO oldApplyRequestDTO = new ApplyRequestDTO();
                        BeanUtils.copyProperties(applyRequestDTO, oldApplyRequestDTO);
                        oldApplyRequestDTO.setApplyId(apply.getParentId());
                        //生成openapi鉴权参数
                        MultiValueMap oldApplyParams = openApiAuthService.genApiAuthParams(oldApplyRequestDTO, landrayEkpConfig.getCompanyId());
                        //查询原审批单详情
                        Map<String, Object> oldApplyDetail = zhiouNonTravelApplyService.getApplyDetail(oldApplyParams);
                        if (ObjectUtils.isEmpty(oldApplyDetail)) {
                            log.info("查询非行程审批详情失败,公司:{},审批单号:{}", landrayEkpConfig.getCompanyId(), apply.getParentId());
                            throw new OpenApiCustomizeException(-9999, "查询非行程审批详情失败,公司:{},审批单号:{}", landrayEkpConfig.getCompanyId(), apply.getParentId());
                        }
                        ApplyDetailDTO oldApply = JsonUtils.toObj(JsonUtils.toJson(oldApplyDetail.get("apply")), ApplyDetailDTO.class);
                        //已变更（更改原单状态）
                        Map<String, Object> jsonMap = Maps.newHashMap();
                        jsonMap.put("applyId", apply.getParentId());
                        jsonMap.put("state", oldApply.getState());
                        Map<String,Object> result = pushLandray(landrayEkpConfig, ZhiouConstant.LANDRAY_APPLY_UPDATE_STATE_SUFFIX, JsonUtils.toJson(jsonMap));
                        boolean isSuccess = !ObjectUtils.isEmpty(result.get("success")) && (boolean) result.get("success");
                        //变更原审批单成功时新增变更后的审批单
                        if (isSuccess) {
                            resultMap = pushLandray(landrayEkpConfig, ZhiouConstant.LANDRAY_APPLY_PUSH_SUFFIX, JsonUtils.toJson(applyDetail));
                        } else {
                            resultMap = result;
                        }
                    } catch (OpenApiCustomizeException e) {
                        log.info("", e);
                    }
                } else {
                    //审批通过
                    resultMap = pushLandray(landrayEkpConfig, ZhiouConstant.LANDRAY_APPLY_PUSH_SUFFIX, JsonUtils.toJson(applyDetail));
                }
            } else if (ApplyStateEnum.APPLY_INVALID.getCode().equals(applyState)) {
                //已作废
                Map<String, Object> jsonMap = Maps.newHashMap();
                jsonMap.put("applyId", applyRequestDTO.getApplyId());
                jsonMap.put("state", apply.getState());
                resultMap = pushLandray(landrayEkpConfig, ZhiouConstant.LANDRAY_APPLY_UPDATE_STATE_SUFFIX, JsonUtils.toJson(jsonMap));
            } else {
                resultMap.put("success", false);
                resultMap.put("msg", "状态为" + applyState + "的审批单无需推送");
            }
        }
        return resultMap == null ? Maps.newHashMap() : resultMap;
    }

    /**
     * 蓝凌接口封装
     *
     * @param landrayEkpConfig 蓝凌配置表
     * @param urlSuffix        蓝凌审批单url后缀
     * @param param            参数
     * @return map             返回结果map
     */
    public Map<String, Object> pushLandray(OpenLandrayEkpConfig landrayEkpConfig, String urlSuffix, String param) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((landrayEkpConfig.getUserName() + ":" + landrayEkpConfig.getPassword()).getBytes()));
        String res = RestHttpUtils.postJson((landrayEkpConfig.getHttpUrl().concat(urlSuffix)), httpHeaders, param);
        Map<String, Object> map = JsonUtils.toObj(res, Map.class);
        return map == null ? Maps.newHashMap() : map;
    }
}
