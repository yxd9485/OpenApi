package com.fenbeitong.openapi.plugin.wechat.eia.service.process;

import com.fenbeitong.openapi.plugin.wechat.eia.dao.ProcessInstanceDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.ProcessInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;

/**
 * Created by dave.hansins on 19/12/16.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaProcessInstanceService {
    @Autowired
    ProcessInstanceDao processInstanceDao;

    public int saveProcessInstance(ProcessInstance processInstance) {
        processInstance.setCreateTime(new Date());
        int result = processInstanceDao.save(processInstance);
        return result;
    }
}
