package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeReqDataDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.callback.constant.ResultEnum;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.util.KingdeeBaseUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取金蝶接口信息
 *
 * @Auther zhang.peng
 * @Date 2021/6/3
 */

@Slf4j
@ServiceAspect
@Service
public class KingDeeServiceImpl implements KingDeeService  {

    @Autowired
    private KingdeeService kingdeeService;
    @Autowired
    OpenSysConfigDao openSysConfigDao;
    @Autowired
    KingdeeConfig kingdeeConfig;
    @Autowired
    OpenKingdeeReqDataDao openKingdeeReqDataDao;

    @Override
    public String getToken(KingDeeConfigDTO jinDieConfigDTo) {
        Map<String, Object> map = new HashMap<>();
        map.put("appId", jinDieConfigDTo.getAppId());
        map.put("appSecret", jinDieConfigDTo.getAppSecret());
        String reqData = JsonUtils.toJsonSnake(map);
        Map<String, Map<String, Object>> resultMap = JsonUtils.toObj(RestHttpUtils.postJson(jinDieConfigDTo.getTokenUrl(), reqData), Map.class);
        return resultMap.get("data") == null ? "" : resultMap.get("data").get("token") == null ? "" : resultMap.get("data").get("token").toString();
    }

    /**
     * 获取部门或人员数据
     */
    @Override
    public List<List> getData(ViewReqDTO viewReqDTO, OpenKingdeeUrlConfig kingDeeUrlConfig, String cookie) {
        List<List> data = new ArrayList<>();
        getListRecursion(viewReqDTO, cookie, data, 0, kingDeeUrlConfig);
        // 查询数据
        return data;
    }

    /**
     * 获取cookie
     */
    @Override
    public String getCookie(OpenKingdeeUrlConfig kingDeeUrlConfig) {
        //  登录
        MultiValueMap loginParam = KingdeeBaseUtils.buildLogin(kingDeeUrlConfig.getAcctId(), kingDeeUrlConfig.getUserName(), kingDeeUrlConfig.getPassword(), Long.parseLong(kingDeeUrlConfig.getLcid()));
        ResultVo login = kingdeeService.login(kingDeeUrlConfig.getUrl() + kingdeeConfig.getLogin(), loginParam);
        if (login.getCode() != ResultEnum.SUCCESS.getCode()) {
            log.warn("【登录金蝶系统失败】：{}", login.getMsg());
            return null;
        }
        // 获取cookie
        Map<String, Object> map2 = (Map<String, Object>) login.getData();
        String cookie = map2.get("cookie").toString();
        return cookie;
    }

    /**
     * 递归查询数据
     */
    public void getListRecursion(ViewReqDTO viewReqDTO, String cookie, List<List> data, int count, OpenKingdeeUrlConfig kingDeeUrlConfig) {
        if (count >= 100) {
            throw new FinhubException(0, "程序异常");
        }
        String respData = kingdeeService.view(kingDeeUrlConfig.getUrl().concat(kingdeeConfig.getBillQury()), cookie, JsonUtils.toJson(viewReqDTO));
        if (!respData.contains("ErrorCode") && !respData.contains("Errors")) {
            if (!"".equals(respData) && respData != null && !"[]".equals(respData)) {
                JSONArray jsonArray = JSONObject.parseArray(respData);
                List<List> dataList = jsonArray.toJavaList(List.class);
                data.addAll(dataList);
                viewReqDTO.getData().setStartRow(viewReqDTO.getData().getStartRow() + 2000);
                viewReqDTO.getData().setLimit(2000);
                getListRecursion(viewReqDTO, cookie, data, ++count, kingDeeUrlConfig);
            }
        } else {
            throw new FinhubException(0, "数据异常");
        }
    }

    /**
     * 获取配置
     */
    public KingDeeK3CloudConfigDTO getConfig(String companyId) {
        // 获取配置信息
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.JINDIE_THIRD_PROJECT_SYS_CONFIG.getType());
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO = JsonUtils.toObj(openSysConfig.getValue(), KingDeeK3CloudConfigDTO.class);
        return kingDee3KCloudConfigDTO;
    }

}
