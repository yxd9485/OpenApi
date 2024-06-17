package com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.impl;

import com.fenbeitong.openapi.plugin.ecology.v8.sipai.constant.SipaiWorkFlowName;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowFormDataDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.DefaultEcologyWorkFlowListener;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: SipaiEcologyWorkFlowListener</p>
 * <p>Description: 思派泛微工作流监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 3:30 PM
 */
@SuppressWarnings("unchecked")
@ServiceAspect
@Service
public class SipaiEcologyWorkFlowListener extends DefaultEcologyWorkFlowListener {

    @Override
    public void setStartEndDateTime(OpenEcologyWorkflow workflow, WorkflowDTO workflowDto, OpenEcologyWorkflowConfig workflowConfig) {
        //工作流名称
        String workflowName = workflowDto.getWorkflowName();
        //加班申请
        if (SipaiWorkFlowName.OVER_TIME_APPLY.getType().equals(workflowName)) {
            Map<String, WorkflowFormDataDTO> formDataMap = workflowDto.getFormData().stream().collect(Collectors.toMap(WorkflowFormDataDTO::getFieldName, Function.identity(), (o, n) -> n));
            WorkflowFormDataDTO fromDate = formDataMap.get("fromDate");
            workflow.setStartDate(fromDate.getFieldValue());
            WorkflowFormDataDTO fromTime = formDataMap.get("fromTime");
            workflow.setStartTime(fromTime.getFieldValue() + ":00");
            WorkflowFormDataDTO toDate = formDataMap.get("toDate");
            workflow.setEndDate(toDate.getFieldValue());
            WorkflowFormDataDTO toTime = formDataMap.get("toTime");
            workflow.setEndTime(toTime.getFieldValue() + ":00");
        } else if (SipaiWorkFlowName.TRIP_APPLY.getType().equals(workflowName)) {
            String method = workflowConfig.getTripFormMethod();
            try {
                Method getMethod = WorkflowDTO.class.getMethod(method);
                getMethod.setAccessible(true);
                List<List<WorkflowFormDataDTO>> detailFormList = (List<List<WorkflowFormDataDTO>>) getMethod.invoke(workflowDto);
                if (!ObjectUtils.isEmpty(detailFormList)) {
                    //最小开始时间
                    Date minStartDateTime = null;
                    //最大结束时间
                    Date maxEndDateTime = null;
                    for (int i = 0; i < detailFormList.size(); i++) {
                        List<WorkflowFormDataDTO> rowFormDataList = detailFormList.get(0);
                        Map<String, WorkflowFormDataDTO> rowFormMap = rowFormDataList.stream().collect(Collectors.toMap(WorkflowFormDataDTO::getFieldName, Function.identity()));
                        Date currentRowStartDateTime = DateUtils.toDate(rowFormMap.get("detail_fromDate").getFieldValue() + " " + rowFormMap.get("detail_fromTime").getFieldValue() + ":00");
                        Date currentRowEndDateTime = DateUtils.toDate(rowFormMap.get("detail_toDate").getFieldValue() + " " + rowFormMap.get("detail_toTime").getFieldValue() + ":00");
                        if (i == 0) {
                            minStartDateTime = currentRowStartDateTime;
                            maxEndDateTime = currentRowEndDateTime;
                        } else {
                            minStartDateTime = minStartDateTime.compareTo(currentRowStartDateTime) > 0 ? currentRowStartDateTime : currentRowEndDateTime;
                            maxEndDateTime = maxEndDateTime.compareTo(currentRowEndDateTime) < 0 ? currentRowEndDateTime : maxEndDateTime;
                        }
                    }
                    if (minStartDateTime != null) {
                        String[] fromDateTime = DateUtils.toSimpleStr(minStartDateTime).split(" ");
                        workflow.setStartDate(fromDateTime[0]);
                        workflow.setStartTime(fromDateTime[1]);
                    }
                    if (maxEndDateTime != null) {
                        String[] toDateTime = DateUtils.toSimpleStr(maxEndDateTime).split(" ");
                        workflow.setEndDate(toDateTime[0]);
                        workflow.setEndTime(toDateTime[1]);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public Integer agreed(WorkflowDTO workflowDto) {
        String currentNodeName = workflowDto.getCurrentNodeName() == null ? "" : workflowDto.getCurrentNodeName();
        if (SipaiWorkFlowName.OVER_TIME_APPLY.getType().equals(workflowDto.getWorkflowName())) {
            return currentNodeName.endsWith("归档") ? 1 : null;
        } else if (SipaiWorkFlowName.TRIP_APPLY.getType().equals(workflowDto.getWorkflowName())) {
            return currentNodeName.endsWith("归档") ? 1 : null;
        }
        return null;
    }
}
