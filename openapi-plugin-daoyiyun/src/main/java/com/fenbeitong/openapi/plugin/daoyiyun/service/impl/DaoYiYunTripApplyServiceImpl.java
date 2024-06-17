package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;

import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderThirdRpcService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackBodyDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunProcessApplyService;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunTripApplyServiceImpl implements DaoYiYunProcessApplyService {

    @Autowired
    private OpenTripApplyServiceImpl openTripApplyService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @DubboReference(check = false)
    private IApplyOrderThirdRpcService iApplyOrderThirdRpcService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeServiceImpl;

    @Autowired
    private DaoYiYunParseFormApplyService daoYiYunParseFormApplyService;

    @Override
    public TaskProcessResult processApply(FinhubTask task,
        DaoYiYunCallbackBodyDTO daoYiYunCallbackBodyDTO, String companyId) throws Exception {
        CommonApplyReqDTO commonApplyReqDTO = daoYiYunParseFormApplyService.parseTripApprovalForm(daoYiYunCallbackBodyDTO, companyId);
        if (commonApplyReqDTO == null) {
            return TaskProcessResult.success("无有效行程");
        }
        log.info("创建审批单信息：{}" , JsonUtils.toJson( commonApplyReqDTO ));
        commonApplyReqDTO.getApply().setCompanyId(companyId);
        createApply(  companyId ,  commonApplyReqDTO );
        return TaskProcessResult.success("success");
    }

    /**
     * 创建审批单
     * @param companyId
     * @param commonApplyReqDTO
     * @throws Exception
     */
    private void createApply( String companyId , CommonApplyReqDTO commonApplyReqDTO) throws Exception{
        LoginResVO loginResVO = openEmployeeServiceImpl.loginAuthInit(companyId, commonApplyReqDTO.getApply().getEmployeeId(), IdTypeEnums.THIRD_ID.getValue());
        String ucToken = loginResVO.getLogin_info().getToken();
        TripApproveCreateReqDTO req = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
        openTripApplyService.createTripApprove(ucToken, req);
    }

}
