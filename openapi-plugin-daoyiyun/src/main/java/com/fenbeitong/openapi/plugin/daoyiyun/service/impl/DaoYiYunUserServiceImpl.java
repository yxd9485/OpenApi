package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;


import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.daoyiyun.constant.DaoYiYunConstant;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunUserService;
import com.fenbeitong.openapi.plugin.daoyiyun.util.DaoYiYunHttpUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunUserInfoRespDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工信息
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunUserServiceImpl implements DaoYiYunUserService {

    @Autowired
    private DaoYiYunHttpUtil httpUtil;

    @Override
    public String getUserAccount(String userId, String applicationId) {
        DaoYiYunUserInfoRespDTO.UserInfo userInfo = getUserInfo(userId, applicationId);
        if (userInfo == null) {
            throw new OpenApiArgumentException("未在道一云查询到用户信息");
        }
        return userInfo.getAccount();
    }


    @Override
    public DaoYiYunUserInfoRespDTO.UserInfo getUserInfo(String userId, String applicationId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        String url = DaoYiYunConstant.DAO_YI_YUN_HOST + MessageFormat.format(DaoYiYunConstant.URL_USER_INFO, userId);
        String result = httpUtil.get(url, null, applicationId);
        DaoYiYunUserInfoRespDTO userInfoRespDTO = JsonUtils.toObj(result, DaoYiYunUserInfoRespDTO.class);
        return userInfoRespDTO.getData();
    }


    @Override
    public String getUserId(String account, String applicationId) {
        DaoYiYunUserInfoRespDTO.UserInfo userInfo = getUserInfoByAccount(account, applicationId);
        if (userInfo == null) {
            throw new OpenApiArgumentException("未在道一云查询到用户信息");
        }
        return userInfo.getId();
    }

    @Override
    public DaoYiYunUserInfoRespDTO.UserInfo getUserInfoByAccount(String account, String applicationId) {
        if (StringUtils.isBlank(account)) {
            return null;
        }
        String url = DaoYiYunConstant.DAO_YI_YUN_HOST + DaoYiYunConstant.URL_SUER_INFO_ACCOUNT;
        Map<String, Object> param = new HashMap<>();
        param.put("account", account);
        String result = httpUtil.get(url, param, applicationId);
        DaoYiYunUserInfoRespDTO userInfoRespDTO = JsonUtils.toObj(result, DaoYiYunUserInfoRespDTO.class);
        return userInfoRespDTO.getData();

    }

}
