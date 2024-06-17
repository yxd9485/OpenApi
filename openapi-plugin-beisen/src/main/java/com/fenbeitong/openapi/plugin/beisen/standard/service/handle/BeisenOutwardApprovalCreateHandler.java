package com.fenbeitong.openapi.plugin.beisen.standard.service.handle;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenOutwardApplyListDTO;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenApprovalService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenCarApprovalService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.impl.BeisenTripApprovalService;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
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
 *  公出单创建用车审批单
 *
 * @author xiaowei
 */
@Component
@Slf4j
public class BeisenOutwardApprovalCreateHandler implements ITaskHandler {

    @Autowired
    UserCenterService userCenterService;
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
    public TaskType getTaskType() {
        return TaskType.BEISEN_OUTWARD_EVENT_CREATE;
    }

    @Override
    public TaskResult execute(Task task) {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskResult.EXPIRED;
        }
        //审批单详情信息
        BeisenOutwardApplyListDTO.OutwardInfo data = JsonUtils.toObj(task.getDataContent(), BeisenOutwardApplyListDTO.OutwardInfo.class);
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), data.getStaffId());
        List<CommonApplyReqDTO> commonApplyReqDTOList = beisenApprovalService.parseBeisenOutwardApprovalForm(data, ucToken);
        commonApplyReqDTOList.forEach(e -> {
            CreateApplyRespDTO beisenCarApprove = null;
            try {
                if (e.getApply() != null && e.getApply().getType() == SaasApplyType.ApplyTaxi.getValue()) {
                    CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, e);
                    beisenCarApprove = beisenCarApprovalService.createBeisenCarApprove(ucToken, carApproveCreateReqDTO);
                    if (ObjectUtils.isEmpty(beisenCarApprove) || StringUtils.isBlank(beisenCarApprove.getId())) {
                        throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "北森创建用车批单失败");
                    }
                    log.info("create car outward apply companyId: {}  applyId : {} , result: {} ", pluginCorpDefinition.getAppId(), data.getOId(), beisenCarApprove != null ? beisenCarApprove.getId() : null);
                }
            } catch (Exception ex) {
                throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, ExceptionUtils.getStackTraceAsString(ex));
            }
            log.info("taskId: {} create outward car apply success applyId: {} ", task.getId(), beisenCarApprove != null ? beisenCarApprove.getId() : null);
        });
        return TaskResult.SUCCESS;
    }


}
