package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowFormDataDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.EcologyWorkFlowListener;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.weaver.v8.workflow.WorkflowRequestInfo;
import com.weaver.v8.workflow.WorkflowServiceLocator;
import com.weaver.v8.workflow.WorkflowServicePortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import javax.xml.rpc.ServiceException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lizhen on 2020/12/19.
 */
@ServiceAspect
@Service
@Slf4j
public abstract class AbstractEcologyWorkFlowListener implements EcologyWorkFlowListener {

    @Autowired
    private OpenEcologyWorkflowDao workflowDao;

    @Override
    public Map<String, EmployeeBaseInfo> getThirdUserMap(List<EmployeeBaseInfo> employeeList) {
        return employeeList.stream().filter(e -> !ObjectUtils.isEmpty(e.getThirdEmployeeId())).collect(Collectors.toMap(EmployeeBaseInfo::getThirdEmployeeId, Function.identity(), (o, n) -> n));
    }

    @Override
    public List<WorkflowRequestInfo> getUserAllWorkflowRequestList(OpenEcologyWorkflowConfig workflowConfig, Integer userId, Date createDate, Map<String, Integer> applyNameMapping) {
        WorkflowServiceLocator locator = new WorkflowServiceLocator(workflowConfig.getWsUrl());
        WorkflowServicePortType httpPort = null;
        WorkflowRequestInfo[] workflowRequestInfos = null;
        int count;
        try {
            httpPort = locator.getWorkflowServiceHttpPort();
            if (createDate == null) {
                createDate = DateUtils.now(true);
            }
//            String[] conditions = new String[]{String.format("t1.createdate='%s'", DateUtils.toSimpleStr(createDate, true)), String.format("t1.creater=%d", userId)};
//            String[] conditions = new String[]{String.format("t1.creater=%d", userId)};
            String[] conditions = new String[]{String.format("t1.createdate>='%s'", DateUtils.toSimpleStr(DateUtils.addDay(createDate, -1), true)), String.format("t1.createdate<='%s'", DateUtils.toSimpleStr(createDate, true)), String.format("t1.creater=%d", userId)};
            count = httpPort.getAllWorkflowRequestCount(NumericUtils.obj2int(userId), conditions);
            log.info("getAllWorkflowRequestCount ip:{}, condition:{}, response:{}", workflowConfig.getWsIp(), StringUtils.joinStr(",", conditions), count);
            if (count > 0) {
                workflowRequestInfos = httpPort.getAllWorkflowRequestList(1, count, count, NumericUtils.obj2int(userId), conditions);
            }
        } catch (Exception e) {
        }
        List<String> requestIdList = ObjectUtils.isEmpty(workflowRequestInfos)
                ? Lists.newArrayList()
                : Lists.newArrayList(workflowRequestInfos).stream()
                .filter(w -> filterRequestName(w.getWorkflowBaseInfo().getWorkflowName(), applyNameMapping)
                ).map(WorkflowRequestInfo::getRequestId)
                .collect(Collectors.toList());
        return ObjectUtils.isEmpty(workflowRequestInfos) ? Lists.newArrayList() : loadDetailWorkFlowList(httpPort, userId, requestIdList);
    }

    protected boolean filterRequestName(String workflowName, Map<String, Integer> applyNameMapping) {
        if (!ObjectUtils.isEmpty(applyNameMapping.get(workflowName))) {
            return true;
        }
        return false;
    }

    @Override
    public List<WorkflowRequestInfo> loadDetailWorkFlowList(int userId, List<String> requestIdList, OpenEcologyWorkflowConfig workflowConfig) {
        WorkflowServiceLocator locator = new WorkflowServiceLocator(workflowConfig.getWsUrl());
        WorkflowServicePortType httpPort = null;
        try {
            httpPort = locator.getWorkflowServiceHttpPort();
        } catch (ServiceException e) {
        }
        return httpPort == null ? Lists.newArrayList() : loadDetailWorkFlowList(httpPort, userId, requestIdList);
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

    @Override
    public void setStartEndDateTime(OpenEcologyWorkflow workflow, WorkflowDTO workflowDto, OpenEcologyWorkflowConfig workflowConfig) {
        workflow.setStartDate(DateUtils.toSimpleStr(new Date(), true));
        workflow.setStartTime("00:00:00");
        workflow.setEndDate(DateUtils.toSimpleStr(new Date(), true));
        workflow.setEndTime("23:59:59");
    }

    @Override
    public abstract Integer agreed(WorkflowDTO workflowDto);




    /**
     * 构建主表信息
     *
     * @param workflow
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T buildTripApplyDTO(OpenEcologyWorkflow workflow, Class<T> clazz) {
        //加载主表单数据
        List<WorkflowFormDataDTO> formDataList = JsonUtils.toObj(workflow.getFormData(), new TypeReference<List<WorkflowFormDataDTO>>() {
        });
        T applyDto = null;
        if (!ObjectUtils.isEmpty(formDataList)) {
            Map<String, Object> data = Maps.newHashMap();
            formDataList.forEach(formData -> data.put(formData.getFieldName(), formData.getFieldValue()));
            applyDto = JsonUtils.toObj(JsonUtils.toJson(data), clazz);
        }
        return applyDto;
    }

    /**
     * 构建子表信息
     *
     * @param workflow
     * @param clazz
     * @return
     */
    public <T> List<T> buildTripApplyDetailDTO(OpenEcologyWorkflow workflow, OpenEcologyWorkflowConfig workflowConfig, Class<T> clazz) {
        //加载子表数据
        String method = workflowConfig.getTripFormMethod();
        List<List<WorkflowFormDataDTO>> detailList = null;
        try {
            Method getMethod = workflow.getClass().getMethod(method);
            getMethod.setAccessible(true);
            String deatilTripJson = (String) getMethod.invoke(workflow);
            //行程表单数据
            detailList = JsonUtils.toObj(deatilTripJson, new TypeReference<List<List<WorkflowFormDataDTO>>>() {
            });
        } catch (Exception e) {
        }
        if (ObjectUtils.isEmpty(detailList)) {
            return null;
        }
        List<T> tripApplyDetailDTOList = detailList.stream().map(detail -> {
            Map<String, Object> data = Maps.newHashMap();
            detail.forEach(formData -> {
                String fieldName = formData.getFieldName();
                String fieldValue = formData.getFieldValue();
                data.put(fieldName, fieldValue);
                data.put(fieldName + "_show_value", formData.getFieldShowValue());
            });
            T tripApplyDetailDTO = JsonUtils.toObj(JsonUtils.toJson(data), clazz);
            return tripApplyDetailDTO;
        }).collect(Collectors.toList());
        return tripApplyDetailDTOList;
    }


    public void updateWorkFlow(OpenEcologyWorkflow workflow, Map<String, Object> applyMap, int state) {
        OpenEcologyWorkflow updateWf = new OpenEcologyWorkflow();
        updateWf.setId(workflow.getId());
        updateWf.setState(state);
        Map<String, Object> extInfo = Maps.newLinkedHashMap();
        extInfo.put("apply_info", applyMap);
        updateWf.setExtInfo(JsonUtils.toJson(extInfo));
        workflowDao.updateById(updateWf);
    }

    /**
     * 创建审批，标准表单
     * @param companyId
     */
    @Override
    public abstract void createApply(String companyId);
}
