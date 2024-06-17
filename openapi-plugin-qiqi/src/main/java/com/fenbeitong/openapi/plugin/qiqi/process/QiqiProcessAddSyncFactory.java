package com.fenbeitong.openapi.plugin.qiqi.process;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName QiqiAddSyncProcessFactory
 * @Description 企企数据增量同步工厂类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/19
 **/
@Component
public class QiqiProcessAddSyncFactory {
    private static Map<String, QiqiProcessAddSync> addSyncMap = new HashMap<String, QiqiProcessAddSync>();

    public static QiqiProcessAddSync getByObjectName(String objectName) {
        return addSyncMap.get(objectName);
    }

    public static void register(String objectName, QiqiProcessAddSync qiqiProcessAddSync) {
        addSyncMap.put(objectName, qiqiProcessAddSync);
    }
}
