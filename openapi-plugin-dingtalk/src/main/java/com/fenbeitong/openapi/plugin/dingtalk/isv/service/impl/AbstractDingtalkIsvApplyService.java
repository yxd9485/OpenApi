package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dao.DingtalkProcessInstanceDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkProcessInstance;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvProcessApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Title: AbstractDingtalkApplyService</p>
 * <p>Description: 钉钉申请单服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020/8/24 8:15 PM
 */
@Component
public abstract class AbstractDingtalkIsvApplyService extends AbstractCarApplyService implements IDingtalkIsvProcessApplyService {

    @Autowired
    private DingtalkProcessInstanceDao dingtalkProcessInstanceDao;

    protected void saveDingtalkProcessInstance(OpenSyncBizDataMedium task, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        // 记录审批实例信息
        DingtalkProcessInstance instance = new DingtalkProcessInstance();
        instance.setCorpId(task.getCorpId());
        instance.setTitle(processInstanceTopVo.getTitle());
        instance.setBizAction(processInstanceTopVo.getBizAction());
        instance.setBusinessId(processInstanceTopVo.getBusinessId());
        instance.setInstanceId(task.getBizId());
        instance.setProcessCode(apply.getProcessCode());
        instance.setUserId(processInstanceTopVo.getOriginatorUserid());
        dingtalkProcessInstanceDao.saveSelective(instance);
    }
}
