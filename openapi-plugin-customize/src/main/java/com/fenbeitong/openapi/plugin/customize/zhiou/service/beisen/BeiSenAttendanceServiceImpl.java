package com.fenbeitong.openapi.plugin.customize.zhiou.service.beisen;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.zhiou.constant.ApplyStateEnum;
import com.fenbeitong.openapi.plugin.customize.zhiou.constant.BeisenResponseCodeEnum;
import com.fenbeitong.openapi.plugin.customize.zhiou.constant.ZhiouConstant;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.*;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.CustomizeBeisenCorp;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.ZhiouNonTravelApplyService;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.impl.ZhiouOpenApiAuthServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BeiSenAttendanceServiceImpl
 * @Description 北森考勤实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/30
 **/
@Component
@Slf4j
@Validated
public class BeiSenAttendanceServiceImpl {
    @Value("${beisen.host}")
    public String beisenBaseUrl;
    @Value("${beisen.token-url}")
    public String beisenTokenUrl;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ZhiouNonTravelApplyService zhiouNonTravelApplyService;
    @Autowired
    private ZhiouOpenApiAuthServiceImpl openApiAuthService;


    /**
     * 推送北森考勤
     *
     * @param applyRequestDTO 审批单参数
     * @param applyDetail     审批单详情
     * @param beisenCorp      北森配置表
     * @return resultMap      返回信息map
     */
    public Map<String, Object> pushAttendance(ApplyRequestDTO applyRequestDTO, Map<String, Object> applyDetail, CustomizeBeisenCorp beisenCorp) {
        Map<String, Object> resultMap = Maps.newHashMap();
        //重试北森考勤推送标识为否时直接返回成功，不需要再调北森接口
        if (!ObjectUtils.isEmpty(applyRequestDTO.isPushBeisenSuccess()) && applyRequestDTO.isPushBeisenSuccess()) {
            resultMap.put("code", BeisenResponseCodeEnum.BEISEN_STATE_SUCCESS.getCode());
        } else {
            Integer applyState = applyRequestDTO.getApplyState();
            //调用北森接口
            if (ApplyStateEnum.APPLY_PASS.getCode().equals(applyState)) {
                ApplyDetailDTO apply = JsonUtils.toObj(JsonUtils.toJson(applyDetail.get("apply")), ApplyDetailDTO.class);
                //判断是否已变更
                if (!StringUtils.isBlank(apply.getParentId()) && !apply.getParentId().equals(applyRequestDTO.getApplyId())) {
                    //已变更
                    try {
                        //组装查询原审批单的参数
                        ApplyRequestDTO oldApplyRequestDTO = new ApplyRequestDTO();
                        BeanUtils.copyProperties(applyRequestDTO, oldApplyRequestDTO);
                        oldApplyRequestDTO.setApplyId(apply.getParentId());
                        //生成openapi鉴权参数
                        MultiValueMap oldApplyParams = openApiAuthService.genApiAuthParams(oldApplyRequestDTO, beisenCorp.getCompanyId());
                        //查询原审批单详情
                        Map<String, Object> oldApplyDetail = zhiouNonTravelApplyService.getApplyDetail(oldApplyParams);
                        if (ObjectUtils.isEmpty(oldApplyDetail)) {
                            log.info("查询非行程审批详情失败,公司:{},审批单号:{}", beisenCorp.getCompanyId(), apply.getParentId());
                            throw new OpenApiCustomizeException(-9999, "查询非行程审批详情失败,公司:{},审批单号:{}", beisenCorp.getCompanyId(), apply.getParentId());
                        }
                        //删除原考勤单
                        Map<String,Object> result = removeBusiness(oldApplyDetail, beisenCorp);
                        boolean isSuccess = (!ObjectUtils.isEmpty(result.get("code")) && BeisenResponseCodeEnum.BEISEN_STATE_SUCCESS.getCode().equals(result.get("code")));
                        //删除成功新增变更后的考勤
                        if (isSuccess) {
                            resultMap = pushBusiness(applyDetail, beisenCorp);
                        } else {
                            resultMap = result;
                        }
                    } catch (OpenApiCustomizeException e) {
                        log.info("", e);
                    }
                } else {
                    //审批通过
                    resultMap = pushBusiness(applyDetail, beisenCorp);
                }
            } else if (ApplyStateEnum.APPLY_INVALID.getCode().equals(applyState)) {
                //已作废
                resultMap = removeBusiness(applyDetail, beisenCorp);
            } else {
                resultMap.put("msg", "状态为" + applyState + "的审批单无需推送");
            }
        }
        return resultMap;
    }

    /**
     * 获取token的接口
     */
    public String getAccessToken(CustomizeBeisenCorp beisenCorp, BeisenParamConfig config) {
        final String beisenTokenKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_CUSTOMIZE_BEISEN_REDIS_KEY, config.getCompanyId());
        String accessToken = (String) redisTemplate.opsForValue().get(beisenTokenKey);
        if (!StringUtils.isBlank(accessToken)) {
            return accessToken;
        }
        String result;
        String getTokenUrl;
        if (config.getTokenUrlIsNew()) {
            getTokenUrl = beisenCorp.getHttpHost() + ZhiouConstant.BEISEN_TOKEN_URLSUFFIX;
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("app_secret", config.getSecret());
            requestMap.put("app_key", config.getKey());
            requestMap.put("grant_type", config.getGrantType());
            result = RestHttpUtils.postFormUrlEncodeForStr(getTokenUrl, null, requestMap);
        } else {
            getTokenUrl = beisenBaseUrl.concat(beisenTokenUrl);
            MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
            requestMap.add("app_id", config.getAppId());
            requestMap.add("secret", config.getSecret());
            requestMap.add("tenant_id", config.getTenantId());
            requestMap.add("grant_type", config.getGrantType());
            result = RestHttpUtils.postForm(getTokenUrl, requestMap);
        }
        return token(result, beisenTokenKey);
    }

    private String token(String result, String beisenTokenKey) {
        int count = 1;
        while (count <= ZhiouConstant.BEISEN_TRY_COUNT) {
            try {
                if (!StringUtils.isBlank(result)) {
                    Map<String, Object> resutlMap = JsonUtils.toObj(result, Map.class);
                    if (resutlMap.get("access_token") != null) {
                        redisTemplate.opsForValue().set(beisenTokenKey, String.valueOf(resutlMap.get("access_token")), Long.valueOf(String.valueOf(resutlMap.get("expires_in"))) - 60, TimeUnit.SECONDS);
                        return String.valueOf(resutlMap.get("access_token"));
                    } else {
                        count++;
                    }
                }
            } catch (Exception e) {
                log.error("get beisen accessToken error", e);
                count++;
            }
        }
        return null;
    }

    /**
     * 北森post接口封装
     *
     * @param beisenCorp 北森配置表
     * @param urlSuffix  北森url后缀
     * @param param      参数
     * @return map       返回信息map
     */
    public Map<String, Object> beisenPost(CustomizeBeisenCorp beisenCorp, String urlSuffix, String param) {
        BeisenParamConfig beisenParamConfig = BeisenParamConfig.builder().companyId(beisenCorp.getCompanyId()).tokenUrlIsNew(true).key(beisenCorp.getAppKey()).secret(beisenCorp.getAppSecret()).grantType(ZhiouConstant.BEISEN_GRANT_TYPE).build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenCorp, beisenParamConfig));
        ResponseEntity<String> res = RestHttpUtils.postEntity((beisenCorp.getHttpHost().concat(urlSuffix)), httpHeaders, param);
        String body = res.getBody();
        Map<String, Object> map = JsonUtils.toObj(body, Map.class);
        List<String> requestId = res.getHeaders().get(ZhiouConstant.X_PAAS_REQUEST_ID);
        if (!ObjectUtils.isEmpty(requestId)) {
            map.put(ZhiouConstant.X_PAAS_REQUEST_ID, requestId.get(0));
        }
        return map == null ? Maps.newHashMap() : map;
    }

    /**
     * 北森get接口封装
     *
     * @param beisenCorp 北森配置表
     * @param urlSuffix  北森参数url后缀
     * @param paramMap   参数map
     * @return map       返回信息map
     */
    public Map<String, Object> beisenGet(CustomizeBeisenCorp beisenCorp, String urlSuffix, Map<String, Object> paramMap) {
        BeisenParamConfig beisenParamConfig = BeisenParamConfig.builder().companyId(beisenCorp.getCompanyId()).tokenUrlIsNew(true).key(beisenCorp.getAppKey()).secret(beisenCorp.getAppSecret()).grantType(ZhiouConstant.BEISEN_GRANT_TYPE).build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + getAccessToken(beisenCorp, beisenParamConfig));
        String res = RestHttpUtils.get((beisenCorp.getHttpHost().concat(urlSuffix)), httpHeaders, paramMap);
        Map<String, Object> map = JsonUtils.toObj(res, Map.class);
        return map == null ? Maps.newHashMap() : map;
    }

    /**
     * 北森接收出差数据接口参数转换
     *
     * @param applyDetail 审批单详情
     * @param beisenCorp  北森配置
     * @return Map<String, Object> 北森返回信息
     */
    public Map<String, Object> pushBusiness(Map<String, Object> applyDetail, CustomizeBeisenCorp beisenCorp) {
        List<Map<String, Object>> tripList = (List<Map<String, Object>>) applyDetail.get("trip_list");
        ApplyTripDTO tripDTO = JsonUtils.toObj(JsonUtils.toJson(tripList.get(0)), ApplyTripDTO.class);
        List<Map<String, Object>> guestList = (List<Map<String, Object>>) applyDetail.get("guest_list");
        Map<String, Object> guestMap = guestList.get(0);
        String[] thirdEmployeeIds;
        //出行人第一个作为主出差人传给北森，余下的作为同行人列表传过去
        if (!ObjectUtils.isEmpty(guestList) && guestList.size() > 1) {
            List<String> thirdEmployeeIdList = Lists.newArrayList();
            for (Map<String, Object> guest : guestList) {
                thirdEmployeeIdList.add((String) guest.get("third_employee_id"));
            }
            thirdEmployeeIdList.removeIf(s -> (s.equals(guestMap.get("third_employee_id"))));
            thirdEmployeeIds = thirdEmployeeIdList.toArray(new String[thirdEmployeeIdList.size()]);
        } else {
            thirdEmployeeIds = new String[0];
        }
        String startTime = dateToDateTime(tripDTO.getStartTime());
        String endTime = dateToDateTime(tripDTO.getEndTime());
        List<BeisenAttendancePushDTO> bussinessList = Lists.newArrayList();
        BeisenAttendancePushDTO pushDTO = BeisenAttendancePushDTO.builder().staffName((String)guestMap.get("name")).staffId((String)guestMap.get("third_employee_id"))
            .startDateTime(startTime).stopDateTime(endTime).departurePlace(tripDTO.getStartCityName().replaceAll("市","")).destination(tripDTO.getStartCityName().replaceAll("市","")).togetherStaffIds(thirdEmployeeIds).build();
        bussinessList.add(pushDTO);
        BeisenParamDTO beisenParamDTO = BeisenParamDTO.builder().businessList(bussinessList).identityType(1).errorEmail("").build();
        log.info("接收出差数据接口参数,beisenParamDTO:{}", beisenParamDTO);
        Map<String, Object> pushResMap = beisenPost(beisenCorp, ZhiouConstant.BEISEN_PUSH_ATTENDANCE_V1, JsonUtils.toJson(beisenParamDTO));
        return buildBeisenResult(beisenCorp, pushResMap);
    }

    /**
     * 北森删除推送错误的出差数据接口参数转换
     *
     * @param applyDetail 审批单明细
     * @param beisenCorp  北森配置
     * @return Map<String, Object> 北森返回信息
     */
    public Map<String, Object> removeBusiness(Map<String, Object> applyDetail, CustomizeBeisenCorp beisenCorp) {
        List<Map<String, Object>> tripList = (List<Map<String, Object>>) applyDetail.get("trip_list");
        ApplyTripDTO tripDTO = JsonUtils.toObj(JsonUtils.toJson(tripList.get(0)), ApplyTripDTO.class);
        List<Map<String, Object>> guestList = (List<Map<String, Object>>) applyDetail.get("guest_list");
        List<BeisenAttendancePushDTO> bussinessList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(guestList)) {
            for (Map<String, Object> guest : guestList) {
                String startTime = dateToDateTime(tripDTO.getStartTime());
                String endTime = dateToDateTime(tripDTO.getEndTime());
                BeisenAttendancePushDTO pushDTO = BeisenAttendancePushDTO.builder().staffName((String) guest.get("name")).staffId((String) guest.get("third_employee_id"))
                    .startDateTime(startTime).stopDateTime(endTime).departurePlace(tripDTO.getStartCityName().replaceAll("市","")).destination(tripDTO.getStartCityName().replaceAll("市","")).build();
                bussinessList.add(pushDTO);
            }
        } else {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("msg", "出行联系人信息为空，无法操作考勤");
        }
        BeisenParamDTO beisenParamDTO = BeisenParamDTO.builder().businessList(bussinessList).identityType(1).errorEmail("").build();
        log.info("删除推送错误的出差数据接口参数,beisenParamDTO:{}", beisenParamDTO);
        Map<String, Object> removeResMap = beisenPost(beisenCorp, ZhiouConstant.BEISEN_REMOVE_ATTENDANCE_V1, JsonUtils.toJson(beisenParamDTO));
        return buildBeisenResult(beisenCorp, removeResMap);
    }

    /**
     * 封装返回参数
     *
     * @param beisenCorp 北森配置表
     * @param resMap     返回参数map
     * @return resultMap 返回信息map
     */
    public Map<String, Object> buildBeisenResult(CustomizeBeisenCorp beisenCorp, Map<String, Object> resMap) {
        Map<String, Object> resultMap = Maps.newHashMap();
        Integer code = (Integer) resMap.get("Code");
        if (200 == code) {
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("requestId", resMap.get(ZhiouConstant.X_PAAS_REQUEST_ID));
            int count = 1;
            //如果状态为执行中，轮询去查询结果
            while (count <= ZhiouConstant.BEISEN_TRY_COUNT) {
                try {
                    Thread.sleep(1000);
                    Map<String, Object> result = beisenGet(beisenCorp, ZhiouConstant.BEISEN_GET_STATE, paramMap);
                    String resCode = (String) result.get("code");
                    if (!ObjectUtils.isEmpty(resCode) && "200".equals(resCode)) {
                        Map<String, Object> dataMap = (Map<String, Object>) result.get("data");
                        String state = (String) dataMap.get("state");
                        if (!ObjectUtils.isEmpty(state) && BeisenResponseCodeEnum.BEISEN_STATE_SUCCESS.getCode().equals(state)) {
                            resultMap.put("code", state);
                            return resultMap;
                        }
                        if (!ObjectUtils.isEmpty(state) && BeisenResponseCodeEnum.BEISEN_STATE_RUNNING.getCode().equals(state)) {
                            count++;
                        } else {
                            return result;
                        }
                    } else {
                        return result;
                    }
                } catch (InterruptedException e) {
                    log.info("线程睡眠异常");
                }
            }
        } else {
            resultMap.put("code", resMap.get("Code"));
            resultMap.put("msg", resMap.get("Message"));
        }
        return resultMap;
    }

    /**
     * 判断日期字符串是否为yyyy-MM-dd HH:mm:ss，如果不是就转化为yyyy-MM-dd HH:mm:ss
     *
     * @param dateString 日期字符串
     */
    public String dateToDateTime(String dateString) {
        if (isDateVail(dateString)) {
            return dateString;
        }
        LocalDate parse = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DateUtils.FORMAT_DATE_PATTERN));
        long timestamp = parse.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.FORMAT_DATE_TIME_PATTERN);
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * 校验时间格式是否为 yyyy-MM-dd HH:mm:ss
     *
     * @param dateString 日期字符串
     */
    private Boolean isDateVail(String dateString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DateUtils.FORMAT_DATE_TIME_PATTERN);
        boolean flag = true;
        try {
            LocalDateTime.parse(dateString, dtf);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

}
