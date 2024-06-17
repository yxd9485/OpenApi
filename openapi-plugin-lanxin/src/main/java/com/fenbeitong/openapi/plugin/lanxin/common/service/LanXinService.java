package com.fenbeitong.openapi.plugin.lanxin.common.service;

import com.fenbeitong.openapi.plugin.lanxin.common.dto.request.LanXinMsgDTO;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.response.*;
import com.fenbeitong.openapi.plugin.lanxin.common.entity.LanxinCorp;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: LanXinServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 9:03 下午
 */


@Slf4j
@Service
@ServiceAspect
public class LanXinService extends LanXinBaseApi {

    /**
     * 获取用户信息
     */
    public LanXinBaseDTO<LanXinUserInfoDTO> getUserInfo(LanxinCorp lanxinCorp, String code) {
        LanXinBaseDTO<LanXinApplyTokenDTO> lanXinApplyTokenDTO = getApplyToken(lanxinCorp);
        LanXinBaseDTO<LanXinUserTokenDTO> lanXinUserTokenDTO = new LanXinBaseDTO<>();
        if (!ObjectUtils.isEmpty(lanXinApplyTokenDTO)) {
            lanXinUserTokenDTO = getUserToken(lanxinCorp, lanXinApplyTokenDTO.getData().getAppToken(), code);
        }
        if (!ObjectUtils.isEmpty(lanXinUserTokenDTO)) {
            Map<String, Object> map = new HashMap<>(4);
            map.put("app_token", lanXinApplyTokenDTO.getData().getAppToken());
            map.put("user_token", lanXinUserTokenDTO.getData().getUserToken());
            return super.getUserInfo(lanxinCorp.getGatewayUrl(), map, lanxinCorp.getCompanyId());
        }
        return null;
    }

    /**
     * 获取应用token
     */
    public LanXinBaseDTO<LanXinApplyTokenDTO> getApplyToken(LanxinCorp lanxinCorp) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("grant_type", "client_credential");
        map.put("appid", lanxinCorp.getAppId());
        map.put("secret", lanxinCorp.getAppSecret());
        return super.getApplyToken(lanxinCorp.getGatewayUrl(), map, lanxinCorp.getCompanyId());
    }

    /**
     * 获取人员token
     */
    public LanXinBaseDTO<LanXinUserTokenDTO> getUserToken(LanxinCorp lanxinCorp, String appToken, String code) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("app_token", appToken);
        map.put("grant_type", "authorization_code");
        map.put("code", code);
        return super.getUserToken(lanxinCorp.getGatewayUrl(), map, lanxinCorp.getCompanyId());
    }

    /**
     * 发送卡片消息
     */
    public void sendMsg(LanxinCorp lanxinCorp, LanXinMsgDTO lanXinMsgDTO) {
        super.sendMsg(lanxinCorp.getGatewayUrl(), lanXinMsgDTO, lanxinCorp.getCompanyId(), getApplyToken(lanxinCorp).getData().getAppToken());
    }

    /**
     * 通过手机号获取人员ID
     */
    public LanXinBaseDTO<LanXinUserStaffIdDTO> getStaffidByPhone(LanxinCorp lanxinCorp, String phone) {
        LanXinBaseDTO<LanXinApplyTokenDTO> lanXinApplyTokenDTO = getApplyToken(lanxinCorp);
        if (!ObjectUtils.isEmpty(lanXinApplyTokenDTO)) {
            Map<String, Object> map = new HashMap<>(8);
            map.put("app_token", lanXinApplyTokenDTO.getData().getAppToken());
            map.put("org_id", lanxinCorp.getOrganizationId());
            map.put("id_type", "mobile");
            map.put("id_value", phone);
            return super.getUserStaffId(lanxinCorp.getGatewayUrl(), lanxinCorp.getCompanyId(), map);
        }
        return null;
    }

}
