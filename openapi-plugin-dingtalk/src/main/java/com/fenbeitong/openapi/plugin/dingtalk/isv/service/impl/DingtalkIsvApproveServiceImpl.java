package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkBizDataDto;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdatamedium.DingtalkFormDataTransferDataHandler;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenTripApplyService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
public class DingtalkIsvApproveServiceImpl extends AbstractDingtalkIsvApplyService {

    @Autowired
    private DingtalkFormDataTransferDataHandler dingtalkFormDataTransferDataHandler;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private IOpenTripApplyService openTripApplyService;

    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        String userId = processInstanceTopVo.getOriginatorUserid();
        String ucToken = userCenterService.getUcEmployeeToken(dingtalkIsvCompany.getCompanyId(), userId);
        //解析biz_data
        String bizData = task.getBizData();
        JSONObject.parse(bizData);
        DingtalkBizDataDto bizDataDto = JsonUtils.toObj(bizData, DingtalkBizDataDto.class);
        //转换钉钉表单
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        String companyId = dingtalkIsvCompany.getCompanyId();
        commonApplyReqDTO = dingtalkFormDataTransferDataHandler.convertDingtalkFormData(bizDataDto,companyId);
        //是否使用酒店为否
        TripApproveCreateReqDTO req = commonApplyService.buildTripApproveCreateReq(commonApplyReqDTO, ucToken, false);
        try {
            openTripApplyService.createTripApprove(ucToken, req);
        } catch (Exception e) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SYNC_APPLY_FAILED, e.getMessage());
        }
        //记录审批实例信息
        saveDingtalkProcessInstance(task, apply, processInstanceTopVo);
        return TaskResult.SUCCESS;
    }

}
