package com.fenbeitong.openapi.plugin.feishu.isv.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvApprovalService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvTripApprovalService;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 *  审批单撤销
 *
 * @author lizhen
 */
@Component
@Slf4j
public class FeiShuIsvApprovalCancelHandler implements ITaskHandler {
    @Autowired
    private CommonApplyServiceImpl commonApplyService;
    @Autowired
    private FeiShuIsvApprovalService feiShuIsvApprovalService;
    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private FeiShuIsvTripApprovalService feiShuIsvTripApprovalService;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Override
    public TaskType getTaskType() {
        return TaskType.FEISHU_ISV_APPROVAL_REVERTED;
    }

    @Autowired
    private ExceptionRemind exceptionRemind;


    @Override
    public TaskResult execute(Task task) throws Exception {
        //1.解析task
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        //2.检查企业是否注册
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
        if (feishuIsvCompany == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskResult.EXPIRED;
        }
        //审批单详情信息
        FeiShuApprovalRespDTO feiShuApprovalDetail = feiShuIsvApprovalService.getFeiShuApprovalDetail(corpId, dataId);
        FeiShuApprovalRespDTO.ApprovalData approvalData = feiShuApprovalDetail.getData();
        //根据code查询是差旅审批还是用车审批
        String approvalCode = approvalData.getApprovalCode();
        ThirdApplyDefinition thirdApplyConfigByProcessCode = thirdApplyDefinitionDao.getThirdApplyConfigByProcessCode(approvalCode);
        if (ObjectUtils.isEmpty(thirdApplyConfigByProcessCode)) {
            return TaskResult.ABORT;
        }
        String userId = approvalData.getOpenId();
        String ucToken = userCenterService.getUcEmployeeToken(feishuIsvCompany.getCompanyId(), userId);
        TripApproveChangeApply tripApproveChangeApply = new TripApproveChangeApply();
        tripApproveChangeApply.setCompanyId(feishuIsvCompany.getCompanyId());
        TripApproveChangeReqDTO build = TripApproveChangeReqDTO.builder()
                .applyId(dataId)
                .thirdType(2)
                .apply(tripApproveChangeApply)
                .build();
        boolean bool = feiShuIsvTripApprovalService.cancelTripApprove(ucToken,build);
        if (!bool) {
            throw new OpenApiPluginSupportException(SupportRespCode.REVERT_APPLY_FAILED);
        }
        Integer processType = thirdApplyConfigByProcessCode.getProcessType();
        //差旅审批删除生成的用车数据
        if (processType == SaasApplyType.ChaiLv.getValue()) {
            // 是否生成用车
            String isUseCarStr = openSysConfigService.getOpenSysConfigByTypeCode(OpenSysConfigType.IS_USE_CAR.getType(), feishuIsvCompany.getCompanyId());
            if (!com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(isUseCarStr)) {
                //取出行程
                FeiShuApprovalFormDTO.Value feiShuApprovalFormDTO = null;
                String form = approvalData.getForm();
                String jsonForm = form.replaceAll("\\\\", "");
                List<Map> list = JsonUtils.toObj(jsonForm, new TypeReference<List<Map>>() {
                });
                for (Map formMap : list) {
                    Object formName = formMap.get("name");
                    Object value = formMap.get("value");
                    if (FeiShuConstant.APPROVAL_FORM_TRIP_GROUP.equals(formName)) {
                        feiShuApprovalFormDTO = JsonUtils.toObj(JsonUtils.toJson(value), FeiShuApprovalFormDTO.Value.class);
                    }
                }
                // 转换出用车审批单
                List<CarApproveCreateReqDTO> carApproveCreateReqDTOList = Lists.newArrayList();
                try {
                    CommonApplyReqDTO commonApplyReqDTO = feiShuIsvApprovalService.parseFeiShuTripApprovalForm(feishuIsvCompany.getCompanyId(), corpId, dataId, feiShuApprovalFormDTO);
                    carApproveCreateReqDTOList = commonApplyService.tripApplyGenerateCarApply(commonApplyReqDTO);
                    // 取消用车审批
                    if (!ObjectUtils.isEmpty(carApproveCreateReqDTOList)) {
                        for (CarApproveCreateReqDTO carApproveCreateReqDTO : carApproveCreateReqDTOList) {
                            String thirdId = carApproveCreateReqDTO.getApply().getThirdId();
                            build = TripApproveChangeReqDTO.builder()
                                    .applyId(thirdId)
                                    .thirdType(2)
                                    .apply(tripApproveChangeApply)
                                    .build();
                            feiShuIsvTripApprovalService.cancelTripApprove(ucToken, build);
                        }
                    }
                } catch (Exception e) {
                    log.error("飞书差旅审批单取消，差旅生成用车审批失败, 用车dto:" + JsonUtils.toJson(carApproveCreateReqDTOList), e);
                    exceptionRemind.taskRemindDingTalk("飞书差旅审批单取消，差旅生成用车审批失败, 用车dto:" + JsonUtils.toJson(carApproveCreateReqDTOList));
                }
            }
        }
        return TaskResult.SUCCESS;
    }

}
