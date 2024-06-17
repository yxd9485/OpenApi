package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit.DingtalkTakeawayKitApplyFormParserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenTakeawayApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Title: DingtalkIsvTakeawaykitApplyServiceImpl</p>
 * <p>Description: 外卖审批套件审批单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaohai
 * @date 2021/12/31 10:57 PM
 */
@Slf4j
@Service
public class DingtalkIsvTakeawaykitApplyServiceImpl extends AbstractDingtalkIsvApplyService {


    @Autowired
    private DingtalkTakeawayKitApplyFormParserServiceImpl formParser;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    OpenTakeawayApplyServiceImpl takeawayApplyServiceImpl;

    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        LoginResVO loginResVO = openEmployeeService.loginAuthInit(dingtalkIsvCompany.getCompanyId() , processInstanceTopVo.getOriginatorUserid(), "1");
        String ucToken = loginResVO.getLogin_info().getToken();
        //获取部门名称
        String orgName = loginResVO.getCompany_info().getOrg_unit().getName();
        //部门id
        String orgId = "";
        String bizData = task.getBizData();
        TakeawayApproveCreateReqDTO commonApplyReqDTO = formParser.parserTakeawayForm(bizData, task.getBizId(), orgId, orgName);
        commonApplyReqDTO.getApply().setCompanyId( dingtalkIsvCompany.getCompanyId() );
        try {
            takeawayApplyServiceImpl.createTakeawayApprove(ucToken , commonApplyReqDTO);
        } catch (Exception e) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SYNC_APPLY_FAILED, e.getMessage());
        }
        //记录审批实例信息
        saveDingtalkProcessInstance(task, apply, processInstanceTopVo);
        return TaskResult.SUCCESS;
    }


}

