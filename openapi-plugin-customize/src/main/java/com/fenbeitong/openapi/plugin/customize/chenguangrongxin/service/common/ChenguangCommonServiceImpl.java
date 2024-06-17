package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.common;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.constant.ChenguangrongxinConstant;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.entity.CustomizeJiandaoyunCorp;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ChenguangCommonServiceImpl
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/21
 **/
@Component
@Slf4j
public class ChenguangCommonServiceImpl<T> {

    @Value("${jiandaoyun.host}")
    private String jiandaoyunHost;

    /**
     * 推送简道云接口
     *
     * @param jiandaoyunUrl  简道云url
     * @param transactionId  事务id：用于防止因重试而导致重复创建同一批数据，也用于绑定一批文件，建议使用 UUID 以免重复
     * @param pushData       参数
     * @param jiandaoyunCorp 简道云配置表
     */
    public void push(String jiandaoyunUrl, String transactionId, T pushData, CustomizeJiandaoyunCorp jiandaoyunCorp) {
        List<T> list = Lists.newArrayList(pushData);
        Map<String, Object> map = new HashMap<>(4);
        map.put("transaction_id", transactionId);
        map.put("data_list", list);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jiandaoyunCorp.getAppKey());
        String result = RestHttpUtils.postJson(jiandaoyunHost + jiandaoyunUrl, httpHeaders, JSONObject.toJSONString(map));
        Map<String, Object> resultMap = JsonUtils.toObj(result, Map.class);
        if (ObjectUtils.isEmpty(resultMap)) {
            log.info("辰光融信推送简道云返回信息为空,companyId:{},transactionId:{}", jiandaoyunCorp.getCompanyId(), transactionId);
            throw new OpenApiCustomizeException(-9999, "辰光融信推送简道云返回信息为空");
        }
        String status = (String) resultMap.get("status");
        if (!StringUtils.equals(status, ChenguangrongxinConstant.SUCCESS)) {
            log.info("辰光融信推送简道云返回失败,companyId:{},transactionId:{},resultMap:{}", jiandaoyunCorp.getCompanyId(), transactionId, resultMap);
            throw new OpenApiCustomizeException(-9999, "辰光融信报销单推送简道云返回失败");
        }
    }
}
