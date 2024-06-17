//package com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.impl;
//
//import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowDao;
//import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
//import com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto.SipaiTripApplyDTO;
//import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveApply;
//import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
//import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveDetail;
//import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
//import com.fenbeitong.openapi.plugin.util.JsonUtils;
//import com.fenbeitong.openapi.sdk.dto.approve.CreateCarApproveRespDTO;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
//import org.springframework.util.ObjectUtils;
//
//import java.util.List;
//import java.util.Map;
//
//import static com.fenbeitong.openapi.sdk.dto.approve.CreateCarApproveReqDTO.CreateCarApplyReqTaxiRule;
//
///**
// * <p>Title: SipaiTripApplyServiceImpl</p>
// * <p>Description: 思派用车审批</p>
// * <p>Company: www.fenbeitong.com</p>
// *
// * @author hwangsy
// * @date 2020/5/7 10:57 AM
// */
//@ServiceAspect
//@Service
//public class SipaiCarApplyServiceImpl extends AbstractCarApplyService {
//
//    @Autowired
//    private SipaiEmployeeServiceImpl employeeService;
//
//    @Autowired
//    private OpenEcologyWorkflowDao workflowDao;
//
//    public void createCarApply(String companyId, OpenEcologyWorkflow workflow, SipaiTripApplyDTO sipaiTripApplyDto) {
//        String token = employeeService.getEmployeeFbToken(companyId, workflow.getEmployeeId(), "0");
//        CarApproveCreateReqDTO createReq = new CarApproveCreateReqDTO();
//        CarApproveApply carApproveApply = new CarApproveApply();
//        carApproveApply.setThirdId(workflow.getRequestId());
//        carApproveApply.setThirdRemark(ObjectUtils.isEmpty(sipaiTripApplyDto.getRemark()) ? "无" : sipaiTripApplyDto.getRemark());
//        carApproveApply.setApplyReason(sipaiTripApplyDto.getReason());
//        createReq.setApply(carApproveApply);
//        CarApproveDetail approveDetail = new CarApproveDetail();
//        approveDetail.setStartCityId("1000001");
//        approveDetail.setStartTime(workflow.getStartDate());
//        approveDetail.setArrivalCityId("1000001");
//        approveDetail.setEndTime(workflow.getEndDate());
//        createReq.setTripList(Lists.newArrayList(approveDetail));
//        createReq.setApplyTaxiRuleInfo(buildRuleInfoList());
//        try {
//            CreateCarApproveRespDTO carApproveRes = createCarApprove(token, createReq);
//            updateWorkFlow(workflow, carApproveRes.getId());
//        } catch (Exception e) {
//        }
//    }
//
//    private void updateWorkFlow(OpenEcologyWorkflow workflow, String id) {
//        OpenEcologyWorkflow updateWf = new OpenEcologyWorkflow();
//        updateWf.setId(workflow.getId());
//        updateWf.setState(1);
//        Map<String, Object> extInfo = Maps.newLinkedHashMap();
//        extInfo.put("apply_id", id);
//        extInfo.put("third_id", workflow.getRequestId());
//        extInfo.put("type", 12);
//        extInfo.put("type_name", "审批用车");
//        updateWf.setExtInfo(JsonUtils.toJson(extInfo));
//        workflowDao.updateById(updateWf);
//    }
//
//    private List<CreateCarApplyReqTaxiRule> buildRuleInfoList() {
//        List<CreateCarApplyReqTaxiRule> ruleList = Lists.newArrayList();
//        CreateCarApplyReqTaxiRule allowSameCity = new CreateCarApplyReqTaxiRule();
//        allowSameCity.setType("allow_same_city");
//        allowSameCity.setValue(false);
//        ruleList.add(allowSameCity);
//        CreateCarApplyReqTaxiRule priceLimitFlag = new CreateCarApplyReqTaxiRule();
//        priceLimitFlag.setType("price_limit_flag");
//        priceLimitFlag.setValue(0);
//        ruleList.add(priceLimitFlag);
//        CreateCarApplyReqTaxiRule priceLimit = new CreateCarApplyReqTaxiRule();
//        priceLimit.setType("price_limit");
//        priceLimit.setValue(-1);
//        ruleList.add(priceLimit);
//        CreateCarApplyReqTaxiRule dayPriceLimit = new CreateCarApplyReqTaxiRule();
//        dayPriceLimit.setType("day_price_limit");
//        dayPriceLimit.setValue(-1);
//        ruleList.add(dayPriceLimit);
//        CreateCarApplyReqTaxiRule taxiSchedulingFee = new CreateCarApplyReqTaxiRule();
//        taxiSchedulingFee.setType("taxi_scheduling_fee");
//        taxiSchedulingFee.setValue(-1);
//        ruleList.add(taxiSchedulingFee);
//        CreateCarApplyReqTaxiRule allowCalledForOther = new CreateCarApplyReqTaxiRule();
//        allowCalledForOther.setType("allow_called_for_other");
//        allowCalledForOther.setValue(false);
//        ruleList.add(allowCalledForOther);
//        CreateCarApplyReqTaxiRule timesLimitFlag = new CreateCarApplyReqTaxiRule();
//        timesLimitFlag.setType("times_limit_flag");
//        timesLimitFlag.setValue(0);
//        ruleList.add(timesLimitFlag);
//        CreateCarApplyReqTaxiRule timesLimit = new CreateCarApplyReqTaxiRule();
//        timesLimit.setType("times_limit");
//        timesLimit.setValue(-1);
//        ruleList.add(timesLimit);
//        CreateCarApplyReqTaxiRule cityLimit = new CreateCarApplyReqTaxiRule();
//        cityLimit.setType("city_limit");
//        cityLimit.setValue(0);
//        ruleList.add(cityLimit);
//        return ruleList;
//    }
//}
