package com.fenbeitong.openapi.plugin.func.deprecated.common.service.impl;

import com.fenbeitong.openapi.plugin.func.deprecated.common.service.OpenJavaDataService;
import com.fenbeitong.openapi.plugin.support.deprecat.dao.OpenOrderParamDao;
import com.fenbeitong.openapi.plugin.support.deprecat.entity.OpenOrderParam;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * module: 迁移open-java 数据查询模块<br/>
 * <p>
 * description: Table and Redis<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/9 22:31
 * @since 1.0
 */
@Service
@ServiceAspect
@Slf4j
public class OpenJavaDataServiceImpl implements OpenJavaDataService {

    @Autowired
    private OpenOrderParamDao openOrderParamDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String getOrderParam(String orderId) {
        OpenOrderParam openOrderParam = openOrderParamDao.getOpenOrderId(orderId);
        String param = "";
        if (ObjectUtils.isEmpty(openOrderParam)) {
            log.info("订单ID未同步到订单自定义表中的数据 {}", orderId);
           try {
               Object o = redisTemplate.opsForValue().get(orderId);
               if (ObjectUtils.isEmpty(o)) {
                   param = "[]";
               } else {
                   param = JsonUtils.toJson(o);
               }
           } catch (Exception e) {
               log.info("获取出未存储成功的订单ID关联的自定义字段，进行返回处理",e);
           }
        } else {
            param = openOrderParam.getParam();
        }
        return param;
    }
}
