package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;
/**
 * <p>Title: YiDaTripApplyServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/13 4:45 下午
 */

import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaApplyDetailRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaProcessApplyService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenTripApplyService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class YiDaTripApplyServiceImpl implements IYiDaProcessApplyService {


    @Autowired
    private IYiDaApplyService yiDaApplyService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private IOpenTripApplyService openTripApplyService;

    @Override
    public TaskResult processApply(String companyId, YiDaApplyDetailRespDTO yiDaApplyDetailRespDTO) throws Exception {
        // 申请单提交人ID
        String userId = yiDaApplyDetailRespDTO.getOriginator().getUserId();
        String ucToken = userCenterService.getUcEmployeeToken(companyId, userId);
        if (!ObjectUtils.isEmpty(ucToken)) {
            CommonApplyReqDTO commonApplyReqDTO = yiDaApplyService.parseYiDaTripApprovalForm(companyId, yiDaApplyDetailRespDTO);
            TripApproveCreateReqDTO tripApproveCreateReqDTO = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
            CreateApplyRespDTO tripApprove = openTripApplyService.createTripApprove(ucToken, tripApproveCreateReqDTO);
            if (ObjectUtils.isEmpty(tripApprove) || ObjectUtils.isEmpty(tripApprove.getId())) {
                return TaskResult.FAIL;
            }
            return TaskResult.SUCCESS;
        }
        return TaskResult.FAIL;
    }
}

