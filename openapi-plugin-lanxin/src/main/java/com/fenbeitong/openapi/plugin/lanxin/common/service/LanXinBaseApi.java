package com.fenbeitong.openapi.plugin.lanxin.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.lanxin.common.constant.LanXinConstant;
import com.fenbeitong.openapi.plugin.lanxin.common.constant.LanXinRedisKeyConstant;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.request.LanXinMsgDTO;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.response.*;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: LanXinBaseApi</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 2:34 下午
 */
@Slf4j
public class LanXinBaseApi extends LanXinMsgService {
    @Autowired
    AuthDefinitionDao authDefinitionDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @Description 获取人员访问TOKEN
     * @Author duhui
     * @Date 2021/12/6
     **/
    public LanXinBaseDTO<LanXinUserTokenDTO> getUserToken(String baseUrl, Map<String, Object> reqMap, String companyId) {
        String url = baseUrl.concat(LanXinConstant.USER_TOKEN_URL);
        try {
            String str = RestHttpUtils.get(url, null, reqMap);
            log.info("companyId:{},蓝信获取人员访问TOKEN:{}", companyId, str);
            LanXinBaseDTO<LanXinUserTokenDTO> lanXinUserTokenDTO;
            if (!StringUtils.isBlank(str)) {
                lanXinUserTokenDTO = JsonUtils.toObj(str, new TypeReference<LanXinBaseDTO<LanXinUserTokenDTO>>() {
                });
            } else {
                throw new FinhubException(500, "蓝信获取人员访问TOKEN接口数据异常");
            }
            if (ObjectUtils.isEmpty(lanXinUserTokenDTO) || !LanXinConstant.SUCCESS.equals(lanXinUserTokenDTO.getErrCode())) {
                throw new FinhubException(500, "蓝信获取人员访问TOKEN接口数据异常");
            }
            return lanXinUserTokenDTO;
        } catch (Exception e) {
            sendMsg(companyId, url, reqMap, e);
        }
        return null;
    }


    /**
     * @Description 获取应用访问TOKEN
     * @Author duhui
     * @Date 2021/12/6
     **/
    public LanXinBaseDTO<LanXinApplyTokenDTO> getApplyToken(String baseUrl, Map<String, Object> reqMap, String companyId) {
        final String applyTokenRedisKey = MessageFormat.format(LanXinRedisKeyConstant.OPEN_PLUGIN_LAN_XIN_APPLY_TOKEN_KEY, companyId);
        String data = (String) redisTemplate.opsForValue().get(applyTokenRedisKey);
        if (!StringUtils.isBlank(data)) {
            return JsonUtils.toObj(data, new TypeReference<LanXinBaseDTO<LanXinApplyTokenDTO>>() {
            });
        }
        String url = baseUrl.concat(LanXinConstant.APPLY_TOKEN_URL);
        try {
            String str = RestHttpUtils.get(url, null, reqMap);
            log.info("companyId:{},蓝信获取应用访问TOKEN:{}", companyId, str);
            LanXinBaseDTO<LanXinApplyTokenDTO> lanXinApplyTokenDTO;
            if (!StringUtils.isBlank(str)) {
                lanXinApplyTokenDTO = JsonUtils.toObj(str, new TypeReference<LanXinBaseDTO<LanXinApplyTokenDTO>>() {
                });
            } else {
                throw new FinhubException(500, "蓝信获取应用访问TOKEN接口数据异常");
            }
            if (ObjectUtils.isEmpty(lanXinApplyTokenDTO) || !LanXinConstant.SUCCESS.equals(lanXinApplyTokenDTO.getErrCode())) {
                throw new FinhubException(500, "蓝信获取应用访问TOKEN接口数据异常");
            }
            redisTemplate.opsForValue().set(applyTokenRedisKey, str, 7000, TimeUnit.SECONDS);
            return lanXinApplyTokenDTO;
        } catch (Exception e) {
            sendMsg(companyId, url, reqMap, e);
        }
        return null;
    }

    /**
     * @Description 获取人员基本信息
     * @Author duhui
     * @Date 2021/12/6
     **/
    public LanXinBaseDTO<LanXinUserInfoDTO> getUserInfo(String baseUrl, Map<String, Object> reqMap, String companyId) {
        String url = baseUrl.concat(LanXinConstant.USER_INFO_URL);
        try {
            String str = RestHttpUtils.get(url, null, reqMap);
            log.info("companyId:{},蓝信获取人员信息:{}", companyId, str);
            LanXinBaseDTO<LanXinUserInfoDTO> lanXinUserInfoDTO;
            if (!StringUtils.isBlank(str)) {
                lanXinUserInfoDTO = JsonUtils.toObj(str, new TypeReference<LanXinBaseDTO<LanXinUserInfoDTO>>() {
                });
            } else {
                throw new FinhubException(500, "蓝信获取人员信息接口数据异常");
            }
            if (ObjectUtils.isEmpty(lanXinUserInfoDTO) || !LanXinConstant.SUCCESS.equals(lanXinUserInfoDTO.getErrCode())) {
                throw new FinhubException(500, "蓝信获取人员信息接口数据异常");
            }
            return lanXinUserInfoDTO;
        } catch (Exception e) {
            sendMsg(companyId, url, reqMap, e);
        }
        return null;
    }

    /**
     * @Description 发送应用消息(应用号通道)
     * @Author duhui
     * @Date 2021/12/6
     **/
    public LanXinBaseDTO sendMsg(String baseUrl, LanXinMsgDTO lanXinMsgDTO, String companyId, String appToken) {
        String url = baseUrl.concat(LanXinConstant.SEND_MSG_URL).concat("?app_token=").concat(appToken);
        try {
            String str = RestHttpUtils.postJson(url, JsonUtils.toJson(lanXinMsgDTO));
            log.info("companyId:{},蓝信发送消息返回信息:{}", companyId, str);
            if (StringUtils.isBlank(str)) {
                throw new FinhubException(500, "蓝信发送消息接口异常");
            }
            return JsonUtils.toObj(str, LanXinBaseDTO.class);
        } catch (Exception e) {
            sendMsg(companyId, url, lanXinMsgDTO, e);
        }
        return null;
    }

    /**
     * @Description 通过唯一标识获取人员ID
     * @Author duhui
     * @Date 2021/12/6
     **/
    public LanXinBaseDTO getUserStaffId(String baseUrl, String companyId, Map<String, Object> reqMap) {
        String url = baseUrl.concat(LanXinConstant.GET_STAFFID_BY_PHONE);
        try {
            String str = RestHttpUtils.get(url, null, reqMap);
            log.info("companyId:{},蓝信获取人员StaffId:{}", companyId, str);
            LanXinBaseDTO<LanXinUserStaffIdDTO> lanXinUserInfoDTO;
            if (!StringUtils.isBlank(str)) {
                lanXinUserInfoDTO = JsonUtils.toObj(str, new TypeReference<LanXinBaseDTO<LanXinUserStaffIdDTO>>() {
                });
            } else {
                throw new FinhubException(500, "蓝信获取人员StaffId信息接口数据异常");
            }
            if (ObjectUtils.isEmpty(lanXinUserInfoDTO) || !LanXinConstant.SUCCESS.equals(lanXinUserInfoDTO.getErrCode())) {
                throw new FinhubException(500, ObjectUtils.isEmpty(lanXinUserInfoDTO) ? "蓝信获取人员StaffId信息接口数据异常" : JsonUtils.toJson(lanXinUserInfoDTO));
            }
            return lanXinUserInfoDTO;
        } catch (Exception e) {
            sendMsg(companyId, url, reqMap, e);
        }
        return null;
    }

}
