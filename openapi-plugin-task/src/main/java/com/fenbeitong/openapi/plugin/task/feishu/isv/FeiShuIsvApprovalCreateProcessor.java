package com.fenbeitong.openapi.plugin.task.feishu.isv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCallbackApprovalDTO;
import com.fenbeitong.openapi.plugin.feishu.common.listener.FeiShuApprovalDefaultListener;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.handler.FeishuIsvProcessApplyFactory;
import com.fenbeitong.openapi.plugin.feishu.isv.service.*;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeEnum;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.sdk.dto.approve.CreateCarApproveRespDTO;
import com.fenbeitong.openapi.sdk.dto.approve.CreateTripApproveRespDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 *  审批单创建
 *
 * @author lizhen
 */
@Component
@Slf4j
public class FeiShuIsvApprovalCreateProcessor extends AbstractTaskProcessor {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private FeiShuIsvApprovalService feiShuIsvApprovalService;

    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    @Autowired
    private FeiShuIsvCarApprovalService feiShuIsvCarApprovalService;

    @Autowired
    private FeiShuIsvTripApprovalService feiShuIsvTripApprovalService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Autowired
    private FeishuIsvProcessApplyFactory applyFactory;

    @Autowired
    private FeiShuApprovalDefaultListener feiShuApprovalDefaultListener;

    @Override
    public Integer getTaskType() {
        return TaskType.FEISHU_ISV_APPROVAL_CREATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("企业不存在，任务丢弃 success");
        }
        //审批单详情信息
        FeiShuApprovalRespDTO feiShuApprovalDetail = feiShuIsvApprovalService.getFeiShuApprovalDetail(corpId, dataId);
        FeiShuApprovalRespDTO.ApprovalData approvalData = feiShuApprovalDetail.getData();
        //根据code查询是差旅审批还是用车审批
        String approvalCode = approvalData.getApprovalCode();
        ThirdApplyDefinition thirdApplyConfigByProcessCode = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(approvalCode);
        if (ObjectUtils.isEmpty(thirdApplyConfigByProcessCode)) {
            return TaskProcessResult.success("success");
        }
        Integer processType = thirdApplyConfigByProcessCode.getProcessType();
        FeiShuCallbackApprovalDTO feiShuCallbackApprovalDTO = JsonUtils.toObj(task.getDataContent(), FeiShuCallbackApprovalDTO.class);
        String status = feiShuCallbackApprovalDTO.getEvent().getStatus();
        //判断是否是反向审批，不是反向审批丢弃掉拒绝的消息
        String processDirType = ProcessTypeEnum.valueOf(processType);
        if (FeiShuConstant.APPROVAL_INSTANCE_STATUS_REJECTED.equals(status) && "0".equals(processDirType)) {
            log.info("不是反向审批的，拒绝任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("不是反向审批的，拒绝任务丢弃 success");
        }
        //申请单提交人ID
        String userId = approvalData.getOpenId();
        String ucToken = userCenterService.getUcEmployeeToken(feishuIsvCompany.getCompanyId(), userId);
        String form = approvalData.getForm();
        String jsonForm = form.replaceAll("\\\\", "");
        List<Map> list = JsonUtils.toObj(jsonForm, new TypeReference<List<Map>>() {
        });
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        //差旅
        if (processType == SaasApplyType.ChaiLv.getValue()) {
            //将差旅套件和自定义字段分别取出
            boolean isUseHotel = false;
            FeiShuApprovalFormDTO.Value feiShuApprovalFormDTO = null;
            for (Map formMap : list) {
                Object formName = formMap.get("name");
                Object value = formMap.get("value");
                if (FeiShuConstant.APPROVAL_FORM_USE_HOTEL.equals(formName) && "是".equals(value)) {
                    isUseHotel = true;
                }
                if (FeiShuConstant.APPROVAL_FORM_TRIP_GROUP.equals(formName)) {
                    feiShuApprovalFormDTO = JsonUtils.toObj(JsonUtils.toJson(value), FeiShuApprovalFormDTO.Value.class);
                }
            }
            commonApplyReqDTO = feiShuIsvApprovalService.parseFeiShuTripApprovalForm(feishuIsvCompany.getCompanyId(), corpId, dataId, feiShuApprovalFormDTO);
            TripApproveCreateReqDTO tripApproveCreateReqDTO = commonApplyService.buildTripApproveCreateReq(commonApplyReqDTO, ucToken, isUseHotel);
            CreateApplyRespDTO tripApproveRespDTO = feiShuIsvTripApprovalService.createTripApprove(ucToken, tripApproveCreateReqDTO);
            if (ObjectUtils.isEmpty(tripApproveRespDTO) || StringUtils.isBlank(tripApproveRespDTO.getId())) {
                throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR);
            }
            // 是否用车配置
            String isUseCarStr = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigType.IS_USE_CAR.getType(), commonApplyReqDTO.getApply().getCompanyId());
            List<CarApproveCreateReqDTO> carApproveCreateReqDTOS = Lists.newArrayList();
            if (!StringUtils.isBlank(isUseCarStr)) {
                try {
                    carApproveCreateReqDTOS = commonApplyService.tripApplyGenerateCarApply(commonApplyReqDTO);
                    if (!ObjectUtils.isEmpty(carApproveCreateReqDTOS)) {
                        for (CarApproveCreateReqDTO carApproveCreateReqDTO : carApproveCreateReqDTOS) {
                            //创建用车时捕获异常，用车创建失败只发告警，保持差旅审批成功且不被重试造成重复
                            CreateApplyRespDTO feiShuCarApprove = feiShuIsvCarApprovalService.createCarApprove(ucToken, carApproveCreateReqDTO);
                            if (ObjectUtils.isEmpty(feiShuCarApprove) || StringUtils.isBlank(feiShuCarApprove.getId())) {
                                log.error("飞书差旅生成用车审批失败, 用车dto:" + JsonUtils.toJson(carApproveCreateReqDTO));
                                exceptionRemind.taskRemindDingTalk("飞书差旅生成用车失败, 用车dto:" + JsonUtils.toJson(carApproveCreateReqDTO));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("飞书差旅生成用车审批失败, 用车dto:" + JsonUtils.toJson(carApproveCreateReqDTOS), e);
                    exceptionRemind.taskRemindDingTalk("飞书差旅生成用车失败, 用车dto:" + JsonUtils.toJson(carApproveCreateReqDTOS));
                }
            }
        } else if (processType == SaasApplyType.ApplyOrder.getValue()) {
            commonApplyService.createFbtOrderApply(corpId, dataId, OpenType.FEISHU_ISV.getType());
        } else if (processType == SaasApplyType.ApplyTaxi.getValue()) {
            commonApplyReqDTO = feiShuApprovalDefaultListener.parseFeiShuCarForm(feishuIsvCompany.getCompanyId(),feishuIsvCompany.getCorpId(),dataId,jsonForm,userId);
            CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
            CreateApplyRespDTO feiShuCarApprove = feiShuIsvCarApprovalService.createCarApprove(ucToken, carApproveCreateReqDTO);
            if (ObjectUtils.isEmpty(feiShuCarApprove) || StringUtils.isBlank(feiShuCarApprove.getId())) {
                throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR);
            }
        } else {
            IFeishuIsvProcessApplyService processApply = applyFactory.getProcessApply(processType);
            TaskResult taskResult = processApply.processApply(FinhubTaskUtils.convert2Task(task), status);
            return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
        }
        return TaskProcessResult.success("success");
    }

}
