package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;

import com.fenbeitong.finhub.common.constant.ApplyStatus;
import com.fenbeitong.openapi.plugin.support.apply.constant.ApplyTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.saasplus.api.model.dto.apply.ApplyOrderContract;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderThirdRpcService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackBodyDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunProcessApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author xiaohai
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunTripUpdateApplyServiceImpl implements DaoYiYunProcessApplyService {

    @Autowired
    private OpenTripApplyServiceImpl openTripApplyService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @DubboReference(check = false)
    private IApplyOrderThirdRpcService iApplyOrderThirdRpcService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeServiceImpl;

    @Autowired
    private DaoYiYunParseFormApplyService daoYiYunParseFormApplyService;

    @Override
    public TaskProcessResult processApply(FinhubTask task,
                                          DaoYiYunCallbackBodyDTO daoYiYunCallbackBodyDTO, String companyId) throws Exception {
        String mainApplicationIdKey = daoYiYunCallbackBodyDTO.getMainApplicationIdKey();
        CommonApplyReqDTO commonApplyReqDTO = daoYiYunParseFormApplyService.parseTripApprovalForm(daoYiYunCallbackBodyDTO, companyId);
        if (commonApplyReqDTO == null) {
            return TaskProcessResult.success("无有效行程");
        }
        log.info("变更审批单信息：{}" , JsonUtils.toJson( commonApplyReqDTO ));
        //查询原单状态
        Map<String, Object> variables = daoYiYunCallbackBodyDTO.getVariables();
        String mainApplicationId = StringUtils.obj2str(variables.get(mainApplicationIdKey));
        commonApplyReqDTO.getApply().setCompanyId(companyId);
        changeApply(  companyId ,  mainApplicationId ,  commonApplyReqDTO);
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
        commonApplyReqDTO.getApply().setCompanyId(companyId);
        TripApproveCreateReqDTO req = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
        openTripApplyService.createTripApprove(ucToken, req);
    }

    /**
     * 审批单变更
     * @param companyId
     * @param commonApplyReqDTO
     */
    private void changeApply( String companyId , String mainApplicationId , CommonApplyReqDTO commonApplyReqDTO) throws Exception{
        //查询原单状态
        List<ApplyOrderContract> applyOrderContracts = iApplyOrderThirdRpcService.thirdQueryApplyByThirdId(companyId, mainApplicationId);
        if(CollectionUtils.isNotBlank(applyOrderContracts)){
            Integer state = applyOrderContracts.get(0).getState();
            if( ApplyStatus.Cancelled.getValue() == state ){
                //原单是作废状态，新增一条单据
                createApply(  companyId ,  commonApplyReqDTO );
            }else if( ApplyStatus.Approved.getValue() == state ){
                //原单是审核通过，变更原单
                LoginResVO loginResVO = openEmployeeServiceImpl.loginAuthInit(companyId, commonApplyReqDTO.getApply().getEmployeeId(), IdTypeEnums.THIRD_ID.getValue());
                String ucToken = loginResVO.getLogin_info().getToken();
                TripApproveCreateReqDTO req = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
                TripApproveChangeReqDTO changReq = new TripApproveChangeReqDTO();
                TripApproveApply reqApply = req.getApply();
                BeanUtils.copyProperties( req , changReq );
                TripApproveChangeApply changeApply = new TripApproveChangeApply();
                BeanUtils.copyProperties( reqApply , changeApply );
                changReq.setThirdType(ApplyTypeConstant.APPLY_TYPE_THIRD);
                //原审批单id
                changReq.setApplyId( mainApplicationId );
                changReq.setApply(changeApply);
                openTripApplyService.changeTripApprove(ucToken, changReq);
            }else{
                log.info("原单状态存在异常 ，审批状态：{}" , state);
            }
        }else {
            log.info("未查询到原单数据 ，三方审批单id：{}" , mainApplicationId);
        }
    }


}
