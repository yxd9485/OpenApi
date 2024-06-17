package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowFormDataDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowRequestLogDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.EcologyWorkFlowListener;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyHrmService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyWorkFlowService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeePageListResult;
import com.google.common.collect.Lists;
import com.weaver.v8.workflow.WorkflowRequestInfo;
import com.weaver.v8.workflow.WorkflowServicePortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: EcologyWorkFlowServiceImpl</p>
 * <p>Description: 泛微审批流信息服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 11:54 AM
 */
@SuppressWarnings("all")
@Slf4j
@ServiceAspect
@Service
public class EcologyWorkFlowServiceImpl implements IEcologyWorkFlowService {

    @Autowired
    private BaseEmployeeRefServiceImpl employeeRefService;

    @Autowired
    private IEtlService etlService;

    @Autowired
    private IEcologyHrmService ecologyHrmService;

    @Autowired
    private OpenEcologyWorkflowDao workflowDao;

    @Autowired
    private OpenEcologyWorkflowConfigDao workflowConfigDao;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Async
    @Override
    public void syncApply(Long configId, String companyId, Date createDate, Map<String, Integer> applyNameMapping) {
        //泛微工作流配置表
        OpenEcologyWorkflowConfig workflowConfig = workflowConfigDao.findByCompanyId(companyId);
        //员工列表
        List<EmployeeBaseInfo> employeeList = getThirdEmployeeList(companyId);
        //审批流监听
        EcologyWorkFlowListener workFlowListener = getWorkFlowListener(workflowConfig.getWorkFlowListener());
        //用户信息
        Map<String, EmployeeBaseInfo> employeeMap = workFlowListener.getThirdUserMap(employeeList);
        //三方人员id
        List<Integer> thirdUserIdList = employeeMap.keySet().stream()
                .filter(org.apache.commons.lang3.StringUtils::isNumeric)
                .map(NumericUtils::obj2int)
                .collect(Collectors.toSet()).stream().collect(Collectors.toList());
        List<List<Integer>> batchUserIdList = CollectionUtils.batch(thirdUserIdList, 100);
        batchUserIdList.forEach(userIdList ->
                CompletableFuture.runAsync(() -> saveWorkFlow(configId, companyId, userIdList, createDate, employeeMap, workflowConfig, workFlowListener, applyNameMapping), taskExecutor)
                        .exceptionally(e -> {
                            log.warn("拉取泛微工作流失败,companyId=" + companyId, e);
                            return null;
                        })
        );
    }

    private List<EmployeeBaseInfo> getThirdEmployeeList(String companyId) {
        List<EmployeeBaseInfo> employeeList = Lists.newArrayList();
        int page = 1;
        EmployeePageListResult employeePageListResult = employeeRefService.getEmployeeExtService().queryEmployeeByDeptId(1, companyId, null, 120, page);
        List<EmployeeBaseInfo> pageEmployeeList = employeePageListResult == null ? null : employeePageListResult.getData();
        if (!ObjectUtils.isEmpty(pageEmployeeList)) {
            employeeList.addAll(pageEmployeeList);
        }
        int count = employeePageListResult == null ? 0 : employeePageListResult.getCount();
        while (employeeList.size() != count) {
            EmployeePageListResult pageResult = employeeRefService.getEmployeeExtService().queryEmployeeByDeptId(2, companyId, null, 120, ++page);
            List<EmployeeBaseInfo> currentPageEmployeeList = pageResult == null ? null : pageResult.getData();
            if (!ObjectUtils.isEmpty(currentPageEmployeeList)) {
                employeeList.addAll(currentPageEmployeeList);
            }
        }
        return employeeList;
    }

    private EcologyWorkFlowListener getWorkFlowListener(String workFlowListener) {
        Class clazz = null;
        try {
            clazz = Class.forName(workFlowListener);
        } catch (Exception e) {
        }
        if (clazz != null) {
            Object bean = SpringUtils.getBean(clazz);
            if (bean instanceof EcologyWorkFlowListener) {
                return ((EcologyWorkFlowListener) bean);
            }
        }
        return SpringUtils.getBean(DefaultEcologyWorkFlowListener.class);
    }

    private void saveWorkFlow(Long configId, String companyId, List<Integer> userIdList, Date createDate, Map<String, EmployeeBaseInfo> employeeMap, OpenEcologyWorkflowConfig workflowConfig, EcologyWorkFlowListener workFlowListener, Map<String, Integer> applyNameMapping) {
        userIdList.forEach(userId -> {
            List<WorkflowRequestInfo> userAllWorkflowRequestList = workFlowListener.getUserAllWorkflowRequestList(workflowConfig, userId, createDate, applyNameMapping);
            if (!ObjectUtils.isEmpty(userAllWorkflowRequestList)) {
                saveUserWorkFlow(configId, companyId, userId, createDate, userAllWorkflowRequestList, employeeMap, workflowConfig, workFlowListener, applyNameMapping);
            }
        });
    }

    private void saveUserWorkFlow(Long configId, String companyId, Integer userCode, Date createDate, List<WorkflowRequestInfo> userAllWorkflowRequestList, Map<String, EmployeeBaseInfo> employeeMap, OpenEcologyWorkflowConfig workflowConfig, EcologyWorkFlowListener workFlowListener, Map<String, Integer> applyNameMapping) {
        List transformList = etlService.transform(configId, JsonUtils.toObj(JsonUtils.toJson(userAllWorkflowRequestList), List.class));
        List<WorkflowDTO> workflowDtoList = JsonUtils.toObj(JsonUtils.toJson(transformList), new TypeReference<List<WorkflowDTO>>() {
        });
        if (!ObjectUtils.isEmpty(workflowDtoList)) {
            List<OpenEcologyWorkflow> savedWorkflowList = workflowDao.findByCompanyIdAndRequestIdList(companyId, workflowDtoList.stream().map(WorkflowDTO::getRequestId).collect(Collectors.toList()));
            List<String> savedRequestIdList = ObjectUtils.isEmpty(savedWorkflowList) ? Lists.newArrayList() : savedWorkflowList.stream().map(OpenEcologyWorkflow::getRequestId).collect(Collectors.toList());
            workflowDtoList.stream()
                    .filter(w -> !savedRequestIdList.contains(w.getRequestId()))
                    .forEach(workflowDto -> {
                        List<WorkflowFormDataDTO> formDataList = workflowDto.getFormData();
                        List<WorkflowRequestLogDTO> requestLogList = workflowDto.getRequestLog();
                        OpenEcologyWorkflow workflow = new OpenEcologyWorkflow();
                        BeanUtils.copyProperties(workflowDto, workflow);
                        workflow.setCompanyId(companyId);
                        workflow.setFormData(JsonUtils.toJson(formDataList));
                        workflow.setDetailForm1(JsonUtils.toJson(workflowDto.getDetailForm1()));
                        workflow.setDetailForm2(JsonUtils.toJson(workflowDto.getDetailForm2()));
                        workflow.setDetailForm3(JsonUtils.toJson(workflowDto.getDetailForm3()));
                        workflow.setDetailForm4(JsonUtils.toJson(workflowDto.getDetailForm4()));
                        workflow.setDetailForm5(JsonUtils.toJson(workflowDto.getDetailForm5()));
                        workflow.setProcessData(JsonUtils.toJson(requestLogList));
                        String jobNumber = StringUtils.obj2str(userCode);
                        workflow.setThirdEmployeeId(jobNumber);
                        workflow.setEmployeeId(employeeMap.get(jobNumber).getId());
                        workflow.setCreateDate(createDate);
                        workflow.setApplyType(applyNameMapping.get(workflow.getWorkflowName()));
                        //设置表单开始及结束时间
                        workFlowListener.setStartEndDateTime(workflow, workflowDto, workflowConfig);
                        workflowDao.saveSelective(workflow);
                    });
        }
    }

    private List<WorkflowRequestInfo> loadDetailWorkFlowList(WorkflowServicePortType httpPort, Integer userId, List<String> requestIdList) {
        return requestIdList.stream().map(requestId -> {
            try {
                return httpPort.getWorkflowRequest(NumericUtils.obj2int(requestId), NumericUtils.obj2int(userId), 0);
            } catch (Exception e) {
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Async
    @Override
    public void updateApply(Long configId, String companyId) {
        //未处理的工作流
        List<OpenEcologyWorkflow> unhandledWorkflowList = workflowDao.findUnhandledWorkflowList(companyId);
        if (ObjectUtils.isEmpty(unhandledWorkflowList)) {
            return;
        }
        //泛微工作流配置表
        OpenEcologyWorkflowConfig workflowConfig = workflowConfigDao.findByCompanyId(companyId);
        //审批流监听
        EcologyWorkFlowListener workFlowListener = getWorkFlowListener(workflowConfig.getWorkFlowListener());
        Map<String, List<OpenEcologyWorkflow>> workflowMap = unhandledWorkflowList.stream().collect(Collectors.groupingBy(OpenEcologyWorkflow::getCreatorId));
        List<String> userIdList = Lists.newArrayList(workflowMap.keySet());
        List<List<String>> batchUserIdList = CollectionUtils.batch(userIdList, 100);
        batchUserIdList.forEach(batchList ->
                CompletableFuture.runAsync(() -> updateWorkflow(configId, batchList, workflowMap, workflowConfig, workFlowListener), taskExecutor)
                        .exceptionally(e -> {
                            log.warn("泛微更新工作流失败,companyId=" + companyId, e);
                            return null;
                        })
        );


    }

    private void updateWorkflow(Long configId, List<String> batchList, Map<String, List<OpenEcologyWorkflow>> workflowMap, OpenEcologyWorkflowConfig workflowConfig, EcologyWorkFlowListener workFlowListener) {
        batchList.forEach(userId -> {
            List<OpenEcologyWorkflow> workflowList = workflowMap.get(userId);
            List<String> requestIdList = workflowList.stream().map(OpenEcologyWorkflow::getRequestId).collect(Collectors.toList());
            //思派工作流
            List<WorkflowRequestInfo> workflowRequestList = workFlowListener.loadDetailWorkFlowList(NumericUtils.obj2int(userId), requestIdList, workflowConfig);
            if (ObjectUtils.isEmpty(workflowRequestList)) {
                return;
            }
            Map<String, OpenEcologyWorkflow> ecologyWorkflowMap = workflowList.stream().collect(Collectors.toMap(OpenEcologyWorkflow::getRequestId, Function.identity()));
            Map<String, WorkflowRequestInfo> workflowRequestInfoMap = workflowRequestList.stream().collect(Collectors.toMap(WorkflowRequestInfo::getRequestId, Function.identity()));
            workflowRequestInfoMap.forEach((requestId, workflowRequestInfo) -> {
                if (workflowRequestInfo == null) {
                    return;
                }
                OpenEcologyWorkflow workflow = ecologyWorkflowMap.get(requestId);
                Map transformMap = etlService.transform(configId, JsonUtils.toObj(JsonUtils.toJson(workflowRequestInfo), Map.class));
                WorkflowDTO workflowDto = JsonUtils.toObj(JsonUtils.toJson(transformMap), WorkflowDTO.class);
                if (workflowDto != null && workflow != null) {
                    OpenEcologyWorkflow ecologyWorkflow = new OpenEcologyWorkflow();
                    ecologyWorkflow.setId(workflow.getId());
                    ecologyWorkflow.setCurrentNodeId(workflowDto.getCurrentNodeId());
                    ecologyWorkflow.setCurrentNodeName(workflowDto.getCurrentNodeName());
                    ecologyWorkflow.setStatus(workflowDto.getStatus());
                    ecologyWorkflow.setAgreed(workFlowListener.agreed(workflowDto));
                    ecologyWorkflow.setFormData(JsonUtils.toJson(workflowDto.getFormData()));
                    ecologyWorkflow.setProcessData(JsonUtils.toJson(workflowDto.getRequestLog()));
                    ecologyWorkflow.setApplyType(workflow.getApplyType());
                    workFlowListener.setStartEndDateTime(ecologyWorkflow, workflowDto, workflowConfig);
                    workflowDao.updateById(ecologyWorkflow);
                }
            });
        });
    }

    @Override
    public void closeApply(String companyId) {
        List<OpenEcologyWorkflow> tripWorkflow = getCloseableTripWorkflow(companyId);
        tripWorkflow.forEach(workflow -> {
            OpenEcologyWorkflow updateWf = new OpenEcologyWorkflow();
            updateWf.setId(workflow.getId());
            updateWf.setState(2);
            workflowDao.updateById(updateWf);
        });
    }

    private List<OpenEcologyWorkflow> getCloseableTripWorkflow(String companyId) {
        OpenEcologyWorkflow workflow = new OpenEcologyWorkflow();
        workflow.setCompanyId(companyId);
        workflow.setAgreed(0);
        workflow.setEndDate(DateUtils.toSimpleStr(DateUtils.yesterday(), true));
        List<OpenEcologyWorkflow> workflowList = workflowDao.findList(workflow);
        return ObjectUtils.isEmpty(workflowList) ? Lists.newArrayList() : workflowList;
    }

    @Override
    public List<OpenEcologyWorkflow> getTripWorkflow(String companyId) {
        OpenEcologyWorkflow workflow = new OpenEcologyWorkflow();
        workflow.setCompanyId(companyId);
        workflow.setAgreed(1);
        workflow.setState(0);
        workflow.setApplyType(SaasApplyType.ChaiLv.getValue());
        List<OpenEcologyWorkflow> workflowList = workflowDao.findList(workflow);
        return ObjectUtils.isEmpty(workflowList) ? Lists.newArrayList() : workflowList;
    }

    @Override
    public void createApply(String companyId) {
        //泛微工作流配置表
        OpenEcologyWorkflowConfig workflowConfig = workflowConfigDao.findByCompanyId(companyId);
        //审批流监听
        EcologyWorkFlowListener workFlowListener = getWorkFlowListener(workflowConfig.getWorkFlowListener());
        workFlowListener.createApply(companyId);
    }

}
