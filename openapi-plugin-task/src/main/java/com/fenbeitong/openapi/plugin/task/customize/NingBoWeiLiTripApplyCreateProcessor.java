package com.fenbeitong.openapi.plugin.task.customize;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLiTripApplyDetailsDto;
import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl.NingBoWeiLiCarServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 创建差旅审批
 * @Author duhui
 * @Date 2020-12-03
 **/
@Component
@Slf4j
public class NingBoWeiLiTripApplyCreateProcessor extends AbstractTripApplyService implements ITaskProcessor {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private NingBoWeiLiCarServiceImpl ningBoWeiLiCarService;

    @Autowired
    private TaskConfig taskConfig;

    @Override
    public Integer getTaskType() {
        return TaskType.NINGBOWEILI_TRIP_APPLY_CREATE.getCode();
    }


    @Override
    public TaskProcessResult process(FinhubTask task) {
        // 1.解析task
        String corpId = task.getCompanyId();
        // 2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskProcessResult.success("企业不存在，任务丢弃 success");
        }
        NingBoWeiLiTripApplyDetailsDto.Records records = JSONObject.parseObject(task.getDataContent(), NingBoWeiLiTripApplyDetailsDto.Records.class);
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), records.getCreatedBy().toString());
        // 封装数据
        CommonApplyReqDTO commonApplyReqDTO = setData(records, ucToken, pluginCorpDefinition.getAppId());
        try {
            if (commonApplyReqDTO.getApply() != null && commonApplyReqDTO.getTripList().size() > 0 && commonApplyReqDTO.getApply().getType() == SaasApplyType.ChaiLv.getValue()) {
                TripApproveCreateReqDTO tripApproveCreateReqDTO = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
                CreateApplyRespDTO tripApproveRespDTO = createTripApprove(ucToken, tripApproveCreateReqDTO);
                if (ObjectUtils.isEmpty(tripApproveRespDTO) || StringUtils.isBlank(tripApproveRespDTO.getId())) {
                    throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "宁波伟立创建审行程批单失败");
                }
                log.info("create trip apply companyId: {}  applyId : {} , result: {} ", pluginCorpDefinition.getAppId(), tripApproveCreateReqDTO.getApply().getThirdId(), tripApproveRespDTO != null ? tripApproveRespDTO.getId() : null);
            }
        } catch (Exception ex) {
            throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, ExceptionUtils.getStackTraceAsString(ex));
        }
        return TaskProcessResult.success("success");
    }


    /**
     * 数据封装
     */
    public CommonApplyReqDTO setData(NingBoWeiLiTripApplyDetailsDto.Records records, String token, String companyId) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        // 申请单
        CommonApply commonApply = new CommonApply();
        commonApply.setApplyReason(records.getCustomItem5__c());
        commonApply.setApplyReasonDesc(records.getCustomItem5__c());
        commonApply.setThirdRemark("宁波伟立三方行程审批");
        commonApply.setThirdId(records.getCustomItem7__c());
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApply.setType(1);
        commonApplyReqDTO.setApply(commonApply);


        // 默认生成酒店
        List<Integer> typeList = new ArrayList() {{
            add(2);
            add(3);
            add(5);
        }};
        typeList.retainAll(records.getCustomItem6__c());
        if (typeList.size() > 0) {
            records.getCustomItem6__c().add(-1);
        }

        // 行程单
        List<CommonApplyTrip> tripList = new ArrayList();
        records.getCustomItem6__c().forEach(t -> {
            CommonApplyTrip commonApplyFromTrip = new CommonApplyTrip();
            // 单程往返 1:单程;2:往返
            commonApplyFromTrip.setTripType(1);
            // 预估金额
            commonApplyFromTrip.setEstimatedAmount(0);
            // 出发城市
            commonApplyFromTrip.setStartCityName("北京市");
            // 到达城市
            commonApplyFromTrip.setArrivalCityName("北京市");
            // 开始时间
            commonApplyFromTrip.setStartTime(DateUtils.parseDate1Str(records.getCustomItem2__c()));
            // 结束时间
            commonApplyFromTrip.setEndTime(DateUtils.parseDate1Str(records.getCustomItem3__c()));
            //3:用车 7:机票 11:酒店 15:火车 40:国际机票
            // 出差类型
            switch (t) {
                case -1:
                    commonApplyFromTrip.setType(11);
                    tripList.add(commonApplyFromTrip);
                    break;
                case 2:
                    commonApplyFromTrip.setType(15);
                    tripList.add(commonApplyFromTrip);
                    break;
                case 3:
                    commonApplyFromTrip.setType(7);
                    tripList.add(commonApplyFromTrip);
                    break;
                case 4:
                    commonApplyFromTrip.setType(3);
                    createCarApply(records, token, companyId);
                    break;
                case 5:
                    commonApplyFromTrip.setType(40);
                    tripList.add(commonApplyFromTrip);
                    break;
                default:
                    log.info("宁波伟立审批单{}类型未知", records.getCustomItem7__c());

            }

        });
        commonApplyReqDTO.setTripList(tripList);
        return commonApplyReqDTO;
    }


    /**
     * 创建用车审批
     */
    private void createCarApply(NingBoWeiLiTripApplyDetailsDto.Records records, String token, String companyId) {
        CarApproveCreateReqDTO carApproveCreateReqDTO = new CarApproveCreateReqDTO();
        CarApproveApply carApproveApply = new CarApproveApply();
        carApproveApply.setCompanyId(companyId);
        carApproveApply.setApplyReason(records.getCustomItem5__c());
        carApproveApply.setThirdId(records.getCustomItem7__c());
        carApproveApply.setThirdRemark(records.getCustomItem5__c());
        carApproveCreateReqDTO.setApply(carApproveApply);
        List<CarApproveDetail> carApproveDetailyList = new ArrayList<>();
        CarApproveDetail carApproveDetail = new CarApproveDetail();
        carApproveDetail.setStartTime(DateUtils.parseDate3Str(records.getCustomItem2__c()));
        carApproveDetail.setEndTime(DateUtils.parseDate3Str(records.getCustomItem3__c()));
        carApproveDetailyList.add(carApproveDetail);
        carApproveCreateReqDTO.setTripList(carApproveDetailyList);
        ningBoWeiLiCarService.createCarApprove(token, carApproveCreateReqDTO);
    }

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }

}
