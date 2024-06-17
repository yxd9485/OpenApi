package com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.constant.SipaiWorkFlowName;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto.SipaiTripApplyDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto.SipaiTripApplyDetailDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.ISipaiApplyService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowFormDataDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyHrmService;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveGuest;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.weaver.v8.hrm.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: SipaiApplyServiceImpl</p>
 * <p>Description: 思派行程用车审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/30 7:01 PM
 */
@SuppressWarnings("unchecked")
@ServiceAspect
@Service
public class SipaiApplyServiceImpl implements ISipaiApplyService {

    @Autowired
    private OpenEcologyWorkflowDao workflowDao;

    @Autowired
    private OpenEcologyWorkflowConfigDao workflowConfigDao;

    @Autowired
    private BaseEmployeeRefServiceImpl employeeService;

    @Autowired
    private IEcologyHrmService ecologyHrmService;

    @Autowired
    private SipaiTripApplyServiceImpl tripApplyService;

    @Async
    @Override
    public void createApply(String companyId) {
        //泛微工作流配置表
        OpenEcologyWorkflowConfig workflowConfig = workflowConfigDao.findByCompanyId(companyId);
        List<OpenEcologyWorkflow> tripWorkflowList = getTripWorkflow(companyId);
        tripWorkflowList.forEach(workflow -> {
            //加载主表单数据
            List<WorkflowFormDataDTO> formDataList = JsonUtils.toObj(workflow.getFormData(), new TypeReference<List<WorkflowFormDataDTO>>() {
            });
            //
            SipaiTripApplyDTO sipaiTripApplyDto = buildTripApplyDto(formDataList);
            List<SipaiTripApplyDetailDTO> tripDetailList = null;
            if (sipaiTripApplyDto != null) {
                tripDetailList = buildTripDetailList(workflow, workflowConfig);
            }
            if (!ObjectUtils.isEmpty(tripDetailList)) {
                tripApplyService.createTripApply(companyId, workflow, sipaiTripApplyDto, tripDetailList, sipaiTripApplyDto.getJtgj());
            }
        });
    }

    @SuppressWarnings("all")
    private List<SipaiTripApplyDetailDTO> buildTripDetailList(OpenEcologyWorkflow workflow, OpenEcologyWorkflowConfig workflowConfig) {
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
            return Lists.newArrayList();
        }
        return detailList.stream().map(detail -> {
            Map<String, Object> data = Maps.newHashMap();
            detail.forEach(formData -> {
                String fieldName = formData.getFieldName();
                String fieldValue = formData.getFieldValue();
                data.put(fieldName, fieldValue);
                if ("detail_companion".equals(fieldName)) {
                    String fieldShowValue = formData.getFieldShowValue();
                    List<String> showValueList = fieldShowValue == null ? Lists.newArrayList() : Lists.newArrayList(fieldShowValue.trim().split(" "));
                    List<String> detailCompanionUserCodeList = Lists.newArrayList();
                    for (int i = 0; i < showValueList.size(); i++) {
                        if ((i + 1) % 2 == 0) {
                            detailCompanionUserCodeList.add(showValueList.get(i));
                        }
                    }
                    data.put("detail_companion_user_code_list", detailCompanionUserCodeList);
                }
            });
            SipaiTripApplyDetailDTO applyDetail = JsonUtils.toObj(JsonUtils.toJson(data), SipaiTripApplyDetailDTO.class);
            //设置同行人信息
            setGusetList(workflow, applyDetail, workflowConfig, workflow.getEmployeeId());
            return applyDetail;
        }).collect(Collectors.toList());
    }

    private void setGusetList(OpenEcologyWorkflow workflow, SipaiTripApplyDetailDTO applyDetail, OpenEcologyWorkflowConfig workflowConfig, String employeeId) {
        String detailCompanion = applyDetail.getDetailCompanion();
        List<TripApproveGuest> guestList = Lists.newArrayList();
        //添加自己到出行人
        EmployeeContract selfEmployeeContract = employeeService.getEmployeeExtService().queryEmployeeInfo(workflow.getEmployeeId(), workflowConfig.getCompanyId());
        TripApproveGuest self = new TripApproveGuest();
        self.setId(selfEmployeeContract.getId());
        self.setIsEmployee(true);
        self.setName(selfEmployeeContract.getName());
        self.setPhoneNum(selfEmployeeContract.getPhone_num());
        guestList.add(self);
        if (!ObjectUtils.isEmpty(detailCompanion)) {
            //同行人
            List<String> detailCompanionUserCodeList = applyDetail.getDetailCompanionUserCodeList();
            List<UserBean> userBeanList = Lists.newArrayList();
            if (!ObjectUtils.isEmpty(detailCompanionUserCodeList)) {
                detailCompanionUserCodeList.forEach(userCode -> {
                    UserBean userBean = ecologyHrmService.getUserByUserCode(workflowConfig, userCode);
                    if (userBean != null) {
                        userBeanList.add(userBean);
                    }
                });
            }
            userBeanList.forEach(userBean -> {
                EmployeeContract employeeContract = employeeService.getEmployeeExtService().queryEmployeeInfo(userBean.getWorkcode(), workflowConfig.getCompanyId());
                TripApproveGuest guest = new TripApproveGuest();
                guest.setId(employeeContract == null ? null : employeeContract.getId());
                guest.setIsEmployee(employeeContract != null);
                guest.setName(employeeContract == null ? userBean.getLastname().split(" ")[0].trim() : employeeContract.getName());
                guest.setPhoneNum(userBean.getMobile());
                guestList.add(guest);
            });
        }
        applyDetail.setGuestList(guestList);
    }

    private SipaiTripApplyDTO buildTripApplyDto(List<WorkflowFormDataDTO> formDataList) {
        SipaiTripApplyDTO applyDto = null;
        if (!ObjectUtils.isEmpty(formDataList)) {
            Map<String, Object> data = Maps.newHashMap();
            formDataList.forEach(formData -> data.put(formData.getFieldName(), formData.getFieldValue()));
            applyDto = JsonUtils.toObj(JsonUtils.toJson(data), SipaiTripApplyDTO.class);
        }
        return applyDto;
    }

    private List<OpenEcologyWorkflow> getTripWorkflow(String companyId) {
        OpenEcologyWorkflow workflow = new OpenEcologyWorkflow();
        workflow.setCompanyId(companyId);
        workflow.setWorkflowName(SipaiWorkFlowName.TRIP_APPLY.getType());
        workflow.setAgreed(1);
        workflow.setState(0);
        List<OpenEcologyWorkflow> workflowList = workflowDao.findList(workflow);
        return ObjectUtils.isEmpty(workflowList) ? Lists.newArrayList() : workflowList;
    }

}
