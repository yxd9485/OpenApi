package com.fenbeitong.openapi.plugin.ecology.v8.kailin.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.ecology.v8.common.WorkFlowState;
import com.fenbeitong.openapi.plugin.ecology.v8.kailin.constant.KaiLinTripType;
import com.fenbeitong.openapi.plugin.ecology.v8.kailin.dto.KaiLinTripApplyDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.kailin.dto.KaiLinTripApplyDetailDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowFormDataDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyTripApplyService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyWorkFlowService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcolotyCarApplyService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.AbstractEcologyWorkFlowListener;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.ExceptionUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 开林泛微公共工作流监听
 *
 * @author lizhen
 */
@SuppressWarnings("unchecked")
@ServiceAspect
@Service
@Slf4j
public class KaiLinEcologyWorkFlowListener extends AbstractEcologyWorkFlowListener {


    @Autowired
    private OpenEcologyWorkflowDao workflowDao;

    @Autowired
    private OpenEcologyWorkflowConfigDao workflowConfigDao;

    @Autowired
    private BaseEmployeeRefServiceImpl employeeService;

    @Autowired
    private IEcologyWorkFlowService ecologyWorkFlowService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private IEcologyTripApplyService ecologyTripApplyService;

    @Autowired
    private IEcolotyCarApplyService ecolotyCarApplyService;

    @Override
    public void setStartEndDateTime(OpenEcologyWorkflow workflow, WorkflowDTO workflowDto, OpenEcologyWorkflowConfig workflowConfig) {
        if (SaasApplyType.ChaiLv.getValue() == workflow.getApplyType()) {
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
                        Date currentRowStartDateTime = DateUtils.toDate(rowFormMap.get("ksrq").getFieldValue() + " " + rowFormMap.get("kssj").getFieldValue() + ":00");
                        Date currentRowEndDateTime = DateUtils.toDate(rowFormMap.get("jsrq").getFieldValue() + " " + rowFormMap.get("jssj").getFieldValue() + ":00");
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
                log.warn("开林设置startTime, endTime失败", e);
            }
        }
    }

    @Override
    public Integer agreed(WorkflowDTO workflowDto) {
        String currentNodeName = workflowDto.getCurrentNodeName() == null ? "" : workflowDto.getCurrentNodeName();
        if ("结束".equals(currentNodeName)) {
            return 1;
        }
        return null;
    }


    @Override
    @Async
    public void createApply(String companyId) {
        //泛微工作流配置表
        OpenEcologyWorkflowConfig workflowConfig = workflowConfigDao.findByCompanyId(companyId);
        List<OpenEcologyWorkflow> tripWorkflowList = ecologyWorkFlowService.getTripWorkflow(companyId);
        TripApproveCreateReqDTO tripApproveCreateReqDTO = null;
        for (OpenEcologyWorkflow workflow : tripWorkflowList) {
            try {
                //先置空，防止上一次结果干扰
                tripApproveCreateReqDTO = null;
                List<CarApproveCreateReqDTO> carApproveCreateReqDTOList = null;
                List<String> applyId = new ArrayList<>();
                Map<String, Object> applyMap = Maps.newLinkedHashMap();
                CommonApplyReqDTO commonApplyReqDTO = parseKaiLinTripApprovalForm(workflow, workflowConfig);
                String thirdEmployeeId = workflow.getThirdEmployeeId();
                String ucToken = userCenterService.getUcEmployeeToken(companyId, thirdEmployeeId);
                //生成行程DTO
                TripAndCarApproveCreateReqDTO tripAndCarApproveCreateReqDTO = commonApplyService.convertApplyAutoHotelCar(commonApplyReqDTO, ucToken);
                //行程审批+酒店
                tripApproveCreateReqDTO = tripAndCarApproveCreateReqDTO.getTripApproveCreateReqDTO();
                CreateApplyRespDTO tripApproveRespDTO = ecologyTripApplyService.createTripApprove(ucToken, tripApproveCreateReqDTO);
                if (!ObjectUtils.isEmpty(tripApproveRespDTO) && !StringUtils.isBlank(tripApproveRespDTO.getId())) {
                    applyId.add(tripApproveRespDTO.getId());
                }
                //用车审批
                carApproveCreateReqDTOList = tripAndCarApproveCreateReqDTO.getCarApproveCreateReqDTOList();
                if (!ObjectUtils.isEmpty(carApproveCreateReqDTOList)) {
                    for (CarApproveCreateReqDTO carApproveCreateReqDTO : carApproveCreateReqDTOList) {
                        CreateApplyRespDTO feiShuCarApprove = ecolotyCarApplyService.createCarApprove(ucToken, carApproveCreateReqDTO);
                        if (!ObjectUtils.isEmpty(feiShuCarApprove) && !StringUtils.isBlank(feiShuCarApprove.getId())) {
                            applyId.add(feiShuCarApprove.getId());
                        }
                    }
                }
                applyMap.put("apply_id", applyId);
                applyMap.put("request_trip_dto", tripApproveCreateReqDTO);
                applyMap.put("request_car_dto", carApproveCreateReqDTOList);
                updateWorkFlow(workflow, applyMap, WorkFlowState.SUCESS.getKey());
            } catch (Exception e) {
                log.warn("开林创建审批失败", e);
                Map<String, Object> applyMap = Maps.newLinkedHashMap();
                applyMap.put("request_dto", tripApproveCreateReqDTO);
                applyMap.put("error_message", ExceptionUtils.getErrorMessageWithNestedException(e));
                updateWorkFlow(workflow, applyMap, WorkFlowState.CLOSEED.getKey());
            }
        }

    }

    private CommonApplyReqDTO parseKaiLinTripApprovalForm(OpenEcologyWorkflow workflow, OpenEcologyWorkflowConfig workflowConfig) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        List<CommonApplyTrip> tripList = new ArrayList();
        //主表
        KaiLinTripApplyDTO kaiLinTripApplyDTO = buildTripApplyDTO(workflow, KaiLinTripApplyDTO.class);
        //子表
        List<KaiLinTripApplyDetailDTO> kaiLinTripApplyDetailDTOList = buildTripApplyDetailDTO(workflow, workflowConfig, KaiLinTripApplyDetailDTO.class);
        String reason = kaiLinTripApplyDTO.getCcsy() + kaiLinTripApplyDTO.getCcbz();
        String approvalId = workflow.getRequestId();
        CommonApply commonApply = new CommonApply();
        commonApply.setCompanyId(workflow.getCompanyId());
        commonApply.setApplyReason(reason);
        commonApply.setApplyReasonDesc(reason);
        commonApply.setThirdRemark(reason);
        commonApply.setThirdId(approvalId);
        commonApply.setType(SaasApplyType.ChaiLv.getValue());
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApply.setCompanyId(workflow.getCompanyId());
        commonApplyReqDTO.setApply(commonApply);

        String planeKey = KaiLinTripType.PLANE.getKey();
        String trainKey = KaiLinTripType.TRAIN.getKey();
        String selfDrivingKey = KaiLinTripType.SELF_DRIVING.getKey();

        for (KaiLinTripApplyDetailDTO kaiLinTripApplyDetailDTO : kaiLinTripApplyDetailDTOList) {
            //出发城市
            //String departure = kaiLinTripApplyDetailDTO.getCfcsShowValue();
            //目地城市
            //String destination = kaiLinTripApplyDetailDTO.getMdcsShowValue();
            // 出发城市编码
            String startCityId = kaiLinTripApplyDetailDTO.getCfcs();
            // 目的城市编码
            String arrivalCityId = kaiLinTripApplyDetailDTO.getMdcs();
            //交通工具
            String transport = kaiLinTripApplyDetailDTO.getJtgjx();
            //单程往返
            String oneRound = kaiLinTripApplyDetailDTO.getDcwfnew();
            //开始时间
            String startTime = kaiLinTripApplyDetailDTO.getKsrq();
            //结束时间
            String endTime = kaiLinTripApplyDetailDTO.getJsrq();

            //国际机票暂时不考虑
            int tripType = 0;
            if (transport.equals(planeKey)) {
                tripType = OrderCategoryEnum.Air.getKey();
            } else if (transport.equals(trainKey)) {
                tripType = OrderCategoryEnum.Train.getKey();
            } else if (transport.equals(selfDrivingKey)) {
                tripType = OrderCategoryEnum.Hotel.getKey();
            } else {
                throw new FinhubException(-1, "trip type不正确：" + transport);
            }
            //单程往返
            int rountTrip = 1;
            rountTrip = oneRound.equals("1") ? 2 : 1;
            CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
            commonApplyTrip.setType(tripType);
            commonApplyTrip.setTripType(rountTrip);
            commonApplyTrip.setEstimatedAmount(0);
            //if (OrderCategoryEnum.Hotel.getKey() == tripType) {//酒店，城市全部取目的城市
            //    commonApplyTrip.setStartCityName(destination);
            //} else {
            //    commonApplyTrip.setStartCityName(departure);
            //}
            //commonApplyTrip.setArrivalCityName(destination);
            if (OrderCategoryEnum.Hotel.getKey() == tripType) {//酒店，城市全部取目的城市
                commonApplyTrip.setStartCityId(arrivalCityId);
            } else {
                commonApplyTrip.setStartCityId(startCityId);
            }
            commonApplyTrip.setArrivalCityId(arrivalCityId);
            commonApplyTrip.setStartTime(startTime);
            commonApplyTrip.setEndTime(endTime);
            commonApplyTrip.setEstimatedAmount(0);
            commonApplyTrip.setCityRelationType(CityRelationType.Ecology.getCode());
            log.info("commonApplyTrip is {}", commonApplyTrip.toString());
            tripList.add(commonApplyTrip);
        }
        commonApplyReqDTO.setTripList(tripList);
        //设置出行人(同行人+申请人)
        List<String> guestThirdIds = Lists.newArrayList();
        String thr = kaiLinTripApplyDTO.getThr();
        if (StringUtils.isBlank(thr)) {
            Lists.newArrayList(thr.split(","));
        }
        guestThirdIds.add(workflow.getThirdEmployeeId());
        commonApplyService.setGustList(commonApplyReqDTO, guestThirdIds);
        return commonApplyReqDTO;
    }


}
