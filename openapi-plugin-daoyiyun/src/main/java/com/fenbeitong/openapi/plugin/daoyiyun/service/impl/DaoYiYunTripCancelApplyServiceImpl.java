package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;

import com.fenbeitong.finhub.common.constant.ApplyStatus;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackBodyDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunProcessApplyService;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.saasplus.api.model.dto.apply.ApplyOrderContract;
import com.fenbeitong.saasplus.api.model.dto.apply.ThirdApplyCancelDTO;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderThirdRpcService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunTripCancelApplyServiceImpl implements DaoYiYunProcessApplyService {

    @DubboReference(check = false)
    private IApplyOrderThirdRpcService iApplyOrderThirdRpcService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeServiceImpl;

    @Autowired
    private DaoYiYunParseFormApplyService daoYiYunParseFormApplyService;

    @Override
    public TaskProcessResult processApply(FinhubTask task,
        DaoYiYunCallbackBodyDTO daoYiYunCallbackBodyDTO, String companyId) {
        CommonApplyReqDTO commonApplyReqDTO = daoYiYunParseFormApplyService.parseTripApprovalForm(daoYiYunCallbackBodyDTO, companyId);
        if (commonApplyReqDTO == null) {
            return TaskProcessResult.success("无有效行程");
        }
        log.info("撤销审批单信息：{}" , JsonUtils.toJson( commonApplyReqDTO ));
        commonApplyReqDTO.getApply().setCompanyId(companyId);
        cancelApply(  companyId ,  commonApplyReqDTO );
        return TaskProcessResult.success("success");
    }


    /**
     * 审批单撤销
     * @param companyId
     * @param commonApplyReqDTO
     */
    private void cancelApply( String companyId , CommonApplyReqDTO commonApplyReqDTO){
        LoginResVO loginResVO = openEmployeeServiceImpl.loginAuthInit(companyId, commonApplyReqDTO.getApply().getEmployeeId(), IdTypeEnums.THIRD_ID.getValue());
        String thirdId = commonApplyReqDTO.getApply().getThirdId();
        List<ApplyOrderContract> applyOrderContracts = iApplyOrderThirdRpcService.thirdQueryApplyByThirdId(companyId, thirdId);
        if(CollectionUtils.isNotBlank(applyOrderContracts)){
            //原单状态是已通过，过滤数据不处理
            Integer state = applyOrderContracts.get(0).getState();
            if( ApplyStatus.Approved.getValue() == state ){
                String applyId = applyOrderContracts.get(0).getId();
                String ucToken = loginResVO.getLogin_info().getToken();
                String userId = loginResVO.getUser_info().getId();
                ThirdApplyCancelDTO thirdApplyCancel = new ThirdApplyCancelDTO();
                thirdApplyCancel.setThird_id(commonApplyReqDTO.getApply().getThirdId());
                thirdApplyCancel.setApply_id(applyId);
                iApplyOrderThirdRpcService.thirdCancel(thirdApplyCancel, userId, companyId, ucToken);
            }
        }
    }

}
