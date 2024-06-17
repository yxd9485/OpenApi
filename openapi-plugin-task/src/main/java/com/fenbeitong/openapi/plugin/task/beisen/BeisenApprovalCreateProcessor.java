package com.fenbeitong.openapi.plugin.task.beisen;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenApplyListDTO;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenApprovalService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenCarApprovalService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenTripApprovalService;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.ExceptionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 *  审批单创建
 *
 * @author xiaowei
 */
@Component
@Slf4j
public class BeisenApprovalCreateProcessor extends AbstractTaskProcessor {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private BeisenApprovalService beisenApprovalService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private BeisenTripApprovalService beisenTripApprovalService;

    @Autowired
    private BeisenCarApprovalService beisenCarApprovalService;

    @Autowired
    private RestHttpUtils httpUtils;

    @Value("${host.appgate}")
    private String appgateUrl;


    @Override
    public Integer getTaskType() {
        return TaskType.BEISEN_APPROVAL_EVENT_CREATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("企业不存在 expired success");
        }
        //审批单详情信息
        BeisenApplyListDTO.BusinessList data = JsonUtils.toObj(task.getDataContent(), BeisenApplyListDTO.BusinessList.class);
        List<BeisenApplyListDTO.BusinessDetailsSync> businessDetailsSync = data.getBusinessDetailsSync();
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), data.getStaffId());
        List<CommonApplyReqDTO> commonApplyReqDTOList = beisenApprovalService.parseBeisenApprovalForm(data, businessDetailsSync, ucToken, pluginCorpDefinition.getAppId());
        commonApplyReqDTOList.forEach(e -> {
            CreateApplyRespDTO tripApproveRespDTO = null;
            CreateApplyRespDTO beisenCarApprove = null;
            try {
                if (e.getApply() != null && e.getApply().getType() == SaasApplyType.ChaiLv.getValue()) {
                    TripApproveCreateReqDTO tripApproveCreateReqDTO = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, e);
                    tripApproveRespDTO = beisenTripApprovalService.createBeisenTripApprove(ucToken, tripApproveCreateReqDTO);
                    if (ObjectUtils.isEmpty(tripApproveRespDTO) || StringUtils.isBlank(tripApproveRespDTO.getId())) {
                        throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "北森创建审行程批单失败");
                    }
                    log.info("create trip apply companyId: {}  applyId : {} , result: {} ", pluginCorpDefinition.getAppId(), data.getObjectId(), tripApproveRespDTO != null ? tripApproveRespDTO.getId() : null);
                } else if (e.getApply() != null && e.getApply().getType() == SaasApplyType.ApplyTaxi.getValue()) {
                    CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, e);
                    beisenCarApprove = beisenCarApprovalService.createBeisenCarApprove(ucToken, carApproveCreateReqDTO);
                    if (ObjectUtils.isEmpty(beisenCarApprove) || StringUtils.isBlank(beisenCarApprove.getId())) {
                        throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "北森创建用车批单失败");
                    }
                    log.info("create car apply companyId: {}  applyId : {} , result: {} ", pluginCorpDefinition.getAppId(), data.getObjectId(), beisenCarApprove != null ? beisenCarApprove.getId() : null);
                } else {
                    throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "获取北森审批单类型无效");
                }
            } catch (Exception ex) {
                throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, ExceptionUtils.getStackTraceAsString(ex));
            }
            log.info("taskId: {} create success applyId: {} ", task.getId(), tripApproveRespDTO != null ? tripApproveRespDTO.getId() : beisenCarApprove != null ? beisenCarApprove.getId() : null);
        });
        return TaskProcessResult.success("success");
    }


}
