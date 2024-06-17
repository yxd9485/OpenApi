package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkCarApplyFormParserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;


/**
 * <p>Title: DingtalkCarApplyServiceImpl</p>
 * <p>Description: 用车订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020/8/24 8:11 PM
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkIsvCarApplyServiceImpl extends AbstractDingtalkIsvApplyService {


    @Autowired
    private DingtalkCarApplyFormParserServiceImpl formParser;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        String ucToken =  userCenterService.getUcEmployeeToken(dingtalkIsvCompany.getCompanyId(), processInstanceTopVo.getOriginatorUserid());
        String bizData = task.getBizData();
        CommonApplyReqDTO commonApplyReqDTO = formParser.parseDingtalkIsvCarForm(bizData, task.getBizId());
        CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
        carApproveCreateReqDTO.getApply().setCompanyId(dingtalkIsvCompany.getCompanyId());
        CreateApplyRespDTO feiShuCarApprove = super.createCarApprove(ucToken, carApproveCreateReqDTO);
        if (ObjectUtils.isEmpty(feiShuCarApprove) || com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(feiShuCarApprove.getId())) {
            throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR);
        }
        return TaskResult.SUCCESS;
    }

}

