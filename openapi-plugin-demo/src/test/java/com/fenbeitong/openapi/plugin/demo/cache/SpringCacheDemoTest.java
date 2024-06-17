package com.fenbeitong.openapi.plugin.demo.cache;

import com.fenbeitong.openapi.plugin.core.entity.KvEntity;
import com.fenbeitong.openapi.plugin.demo.DemoTest;
import com.fenbeitong.openapi.plugin.demo.service.CacheDemoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * spring缓存测试类
 * Created by log.chang on 2019/11/26.
 */
@Slf4j
public class SpringCacheDemoTest extends DemoTest {

    @Autowired
    private CacheDemoService cacheDemoService;

    @Test
    public void cacheTest() {
        long start = System.currentTimeMillis();

        List<KvEntity> kvEntityList = cacheDemoService.listKvEntity();
        log.info("第一次获取全量kvEntitieList={} 耗时 {}ms", kvEntityList, System.currentTimeMillis() - start);

        // 第二次没有sleep说明已经走了缓存
        start = System.currentTimeMillis();
        kvEntityList = cacheDemoService.listKvEntity();
        log.info("第二次获取全量kvEntitieList={} 耗时 {}ms", kvEntityList, System.currentTimeMillis() - start);

        // 添加之后执行了sleep说明重新获取了全量
        start = System.currentTimeMillis();
        KvEntity addKvEntity = cacheDemoService.addKvEntity("key-new", "value-new");
        kvEntityList = cacheDemoService.listKvEntity();
        log.info("添加元素之后获取全量kvEntitieList={} 耗时 {}ms", kvEntityList, System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        KvEntity kvEntity = cacheDemoService.getKvEntity(addKvEntity.getKey());
        log.info("新添加的元素第一次获取对象KvEntity={} 耗时 {}ms", kvEntity, System.currentTimeMillis() - start);

        cacheDemoService.updateKvEntity(addKvEntity.getKey(),"value-new-2");

        start = System.currentTimeMillis();
        kvEntity = cacheDemoService.getKvEntity(addKvEntity.getKey());
        log.info("更新的元素第一次获取对象KvEntity={} 耗时 {}ms", kvEntity, System.currentTimeMillis() - start);

        cacheDemoService.delKvEntity(addKvEntity.getKey());

        start = System.currentTimeMillis();
        kvEntity = cacheDemoService.getKvEntity(addKvEntity.getKey());
        log.info("删除新添加的元素获取对象KvEntity={} 耗时 {}ms", kvEntity, System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        kvEntityList = cacheDemoService.listKvEntity();
        log.info("删除新添加的元素获取全量对象kvEntityList={} 耗时 {}ms", kvEntityList, System.currentTimeMillis() - start);

    }


}
