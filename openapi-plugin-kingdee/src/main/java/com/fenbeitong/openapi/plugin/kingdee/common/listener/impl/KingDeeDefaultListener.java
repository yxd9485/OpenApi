package com.fenbeitong.openapi.plugin.kingdee.common.listener.impl;

import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.kingdee.common.constant.KingdeeConstant;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeekBudgetDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekListener;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description 可配置化监听
 * @Author duhui
 * @Date 2020-11-26
 **/

@Primary
@Service
public class KingDeeDefaultListener implements KingDeekListener {

    private static final String KINGDEE_REDIS_KEY = "kingdee_redis_key:{0}";
    private static final String KINGDEE_BILL_REDIS_KEY = "kingdee_bill_redis_key:{0}";
    @Autowired
    OpenSysConfigDao openSysConfigDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public String saveParse(String data, String companyId, Object... params) {
        OpenThirdScriptConfig openThirdScriptConfig = getOpenThirdScriptConfig(companyId, KingdeeConstant.TtlRelationType.SAVE_PARSE);
        if (!ObjectUtils.isEmpty(openThirdScriptConfig)) {
            return kingDeeFilter(openThirdScriptConfig, null, data, companyId, null, params);
        } else {
            KingDeekBudgetDTO kingDeekBudgetDTO = (KingDeekBudgetDTO) params[0];
            String budget = String.format(data,
                    kingDeekBudgetDTO.getFAdjustDate(),
                    kingDeekBudgetDTO.getFPeriod(),
                    kingDeekBudgetDTO.getOrgName(),
                    kingDeekBudgetDTO.getMoney().doubleValue(),
                    kingDeekBudgetDTO.getOrgCode());
            return budget;
        }

    }

    @Override
    public String commitParse(String data, String number) {
        String budget = String.format(data, number);
        return budget;
    }

    @Override
    public String auditParse(String data, String number) {
        String budget = String.format(data, number);
        return budget;
    }

    @Override
    public void setList(String dataKey, String dataValue, String companyId, StringBuffer strData, Object... objects) {
        OpenThirdScriptConfig openThirdScriptConfig = getOpenThirdScriptConfig(companyId, KingdeeConstant.TtlRelationType.SET_LIST);
        if (!ObjectUtils.isEmpty(openThirdScriptConfig)) {
            strData = kingDeeFilter(openThirdScriptConfig, dataKey, dataValue, companyId, strData, objects);
        } else {
            strData = strData.append("\"").append(dataKey).append("\"").append(":[").append(dataValue).append("]");
        }
    }

    @Override
    public void setMap(String dataKey, String dataValue, String companyId, StringBuffer strData, Object... objects) {
        OpenThirdScriptConfig openThirdScriptConfig = getOpenThirdScriptConfig(companyId, KingdeeConstant.TtlRelationType.SET_MAP);
        if (!org.springframework.util.StringUtils.isEmpty(openThirdScriptConfig)) {
            strData = kingDeeFilter(openThirdScriptConfig, dataKey, dataValue, companyId, strData, objects);
        } else {
            dataValue = dataValue.trim();
            if (StringUtils.isBlank(dataKey)) {
                strData = strData.insert(0, dataValue.substring(0, dataValue.length() - 1) + ",").append("}");
            } else if (StringUtils.isBlank(strData.toString())) {
                strData = strData.append("\"").append(dataKey).append("\"").append(":").append(dataValue);
            } else {
                strData.insert(0, "\"" + dataKey + "\":" + dataValue.substring(0, dataValue.length() - 1) + ",").append("}");
            }
        }
    }

    public String getOpenSysConfig(String type, String companyId) {
        final String tokenKey = MessageFormat.format(KINGDEE_REDIS_KEY, companyId + type);
        String data = (String) redisTemplate.opsForValue().get(tokenKey);
        if (!StringUtils.isBlank(data)) {
            return data;
        } else {
            OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfigByTypeCode(type, companyId);
            if (!ObjectUtils.isEmpty(openSysConfig)) {
                redisTemplate.opsForValue().set(KINGDEE_REDIS_KEY, openSysConfig.getValue(), 7, TimeUnit.DAYS);
                return openSysConfig.getValue();
            } else {
                return null;
            }
        }
    }

    private OpenThirdScriptConfig getOpenThirdScriptConfig(String companyId, Integer relationType) {
        final String tokenKey = MessageFormat.format(KINGDEE_BILL_REDIS_KEY, companyId + EtlScriptType.BILL_PUSH.getType() + relationType);
        String data = (String) redisTemplate.opsForValue().get(tokenKey);
        if (!StringUtils.isBlank(data)) {
            return JsonUtils.toObj(data, OpenThirdScriptConfig.class);
        } else {
            OpenThirdScriptConfig openThirdScriptConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.BILL_PUSH, relationType);
            if (!ObjectUtils.isEmpty(openThirdScriptConfig)) {
                redisTemplate.opsForValue().set(tokenKey, JsonUtils.toJson(openThirdScriptConfig), 1, TimeUnit.HOURS);
                return openThirdScriptConfig;
            } else {
                return null;
            }
        }
    }

    private <T> T kingDeeFilter(OpenThirdScriptConfig openThirdScriptConfig, String dataKey, String dataValue, String companyId, StringBuffer strData, Object... objects) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("dataKey", dataKey);
            put("dataValue", dataValue);
            put("companyId", companyId);
            put("strData", strData);
            put("objects", objects);
        }};
        return (T) EtlUtils.etlFilter(openThirdScriptConfig, params);
    }

}
