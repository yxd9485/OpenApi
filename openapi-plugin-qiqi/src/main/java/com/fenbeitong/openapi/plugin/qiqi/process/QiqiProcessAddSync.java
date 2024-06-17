package com.fenbeitong.openapi.plugin.qiqi.process;

import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import org.springframework.beans.factory.InitializingBean;


/**
 * @ClassName QiqiAddSyncService
 * @Description 企企增量数据同步接口
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/19
 **/
public interface QiqiProcessAddSync extends InitializingBean {
    /**
     * @param operation 操作类型
     * @param corpId 三方企业id
     */
    TaskType addSync(String operation, String corpId) throws Exception;
}
