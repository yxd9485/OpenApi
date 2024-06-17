package com.fenbeitong.openapi.plugin.demo.service;

import com.fenbeitong.openapi.plugin.core.entity.KvEntity;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存实现，如果希望使用一下缓存功能需要配合启动类添加注解@EnableCaching
 * <p>
 * Created by log.chang on 2019/11/26.
 */
@Slf4j
@ServiceAspect
@Service

@CacheConfig(cacheNames = "kvEntity"/*, keyGenerator= */)
public class CacheDemoService implements Serializable{
    /*
        CacheConfig参数说明：
        属性	            类型	        功能
        cacheNames	    String[]	缓存的名称和value功能一样
        keyGenerator	String	    缓存key的生成器
        cacheManager	String	    配置使用那个缓存管理器、和cacheResolver排斥
        cacheResolver	String	    定义使用那个拦截器、和cacheManager互斥
    */

    /*
        Cacheable获取缓存，如果有缓存直接返回，参数说明：
        属性	            类型	        功能
        value	        String[]	缓存的名称 和cacheNames功能一样
        cacheNames	    String[]	缓存的名称和value功能一样
        key	            String	    缓存key的值、默认是以所有的参数作为key、也可以直接配置keyGenerator
        keyGenerator	String	    缓存key的生成器
        cacheManager	String	    配置使用那个缓存管理器、和cacheResolver排斥
        cacheResolver	String	    定义使用那个拦截器、和cacheManager互斥
        condition	    String	    根据spel表达式来可以配置什么条件下进行缓存 默认全部缓存
        unless	        String	    和condition相反
        sync	        boolean	    是否开启同步功能、默认不开启
     */

    /*
        CachePut执行并且更新缓存相关 不管如何 肯定会执行方法 然后返回 这样可以更新缓存的内容，参数说明：
        属性	            类型	        功能
        value	        String[]	缓存的名称 和cacheNames功能一样
        cacheNames	    String[]	缓存的名称和value功能一样
        key	            String	    缓存key的值、默认是以所有的参数作为key、也可以直接配置keyGenerator
        keyGenerator	String	    缓存key的生成器
        cacheManager	String	    配置使用那个缓存管理器、和cacheResolver排斥
        cacheResolver	String	    定义使用那个拦截器、和cacheManager互斥
        condition	    String	    根据spel表达式来可以配置什么条件下进行缓存 默认全部缓存
        unless	        String	    和condition相反
     */

    /*
        CacheEvict删除缓存，参数说明：
        属性	                类型	        功能
        value	            String[]	缓存的名称 和cacheNames功能一样
        cacheNames	        String[]	缓存的名称和value功能一样
        key	                String	    缓存key的值、默认是以所有的参数作为key、也可以直接配置keyGenerator
        keyGenerator	    String	    缓存key的生成器
        cacheManager	    String	    配置使用那个缓存管理器、和cacheResolver排斥
        cacheResolver	    String	    定义使用那个拦截器、和cacheManager互斥
        condition	        String	    根据spel表达式来可以配置什么条件下进行缓存 默认全部缓存
        allEntries	        boolean	    是否删除所有键的缓存 默认不删除
        beforeInvocation	boolean	    是否在调用此方法前 删除缓存
     */

    private List<KvEntity> kvEntityList = new ArrayList<KvEntity>() {{
        add(new KvEntity("k1", "v1"));
        add(new KvEntity("k2", "v2"));
        add(new KvEntity("k3", "v3"));
        add(new KvEntity("k4", "v4"));
        add(new KvEntity("k5", "v5"));
    }};

    /**
     * 缓存全部，如果结果为空则不缓存
     */
    @Cacheable(key = "'list'", unless = "#result == null") // 结果为空不缓存
    public List<KvEntity> listKvEntity() {
        ThreadUtils.sleep(1000);
        return kvEntityList;
    }

    /**
     * 缓存key，如果结果为空则不缓存
     */
    @Cacheable(key = "#key", unless = "#result == null")
    public KvEntity getKvEntity(String key) {
        ThreadUtils.sleep(1000);
        return kvEntityList.stream().filter(kv -> kv.getKey().equals(key)).findFirst().orElse(null);
    }

    /**
     * 添加key缓存
     */
    @CachePut(key = "#key")
    @CacheEvict(key = "'list'")
    public KvEntity addKvEntity(String key, String value) {
        KvEntity kvEntity = new KvEntity(key, value);
        kvEntityList.add(kvEntity);
        return kvEntity;
    }

    /**
     * 更新key缓存
     */
    @CachePut(key = "#key")
    @CacheEvict(key = "'list'")
    public void updateKvEntity(String key, String value) {
        KvEntity kvEntity = getKvEntity(key);
        kvEntityList.remove(kvEntity);
        if (kvEntity.getValue().equals(value))
            return;
        kvEntity.setValue(value);
        kvEntityList.add(kvEntity);
    }

    /**
     * 1. 删除key缓存
     * 2. 同时需要删除list缓存并
     * 3. 重新加载list缓存（此步骤可不操作）
     */
    @Caching(evict = {@CacheEvict(key = "#key"), @CacheEvict(key = "'all'")}, put = @CachePut(key = "'list'"))
    public List<KvEntity> delKvEntity(String key) {
        KvEntity kvEntity = kvEntityList.stream().filter(kv -> kv.getKey().equals(key)).findFirst().orElse(null);
        kvEntityList.remove(kvEntityList.indexOf(kvEntity));
        return kvEntityList;
    }


}
