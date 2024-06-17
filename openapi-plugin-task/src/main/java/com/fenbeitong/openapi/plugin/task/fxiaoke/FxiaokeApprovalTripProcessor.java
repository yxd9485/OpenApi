package com.fenbeitong.openapi.plugin.task.fxiaoke;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeApplyTripDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeCorpAppDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeObjApplyDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeTripRoundDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeApplyTrip;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeObjApply;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeTripRound;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl.FxkCustomDataServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class FxiaokeApprovalTripProcessor extends AbstractTripApplyService implements ITaskProcessor {

    @Autowired
    private FxkCustomDataServiceImpl fxkCustomDataService;

    @Autowired
    private IFxkAccessTokenService iFxkAccessTokenService;

    @Autowired
    private FxiaokeCorpAppDao fxiaokeCorpAppDao;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private FxiaokeObjApplyDao fxiaokeObjApplyDao;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private FxiaokeApplyTripDao fxiaokeApplyTripDao;

    @Autowired
    private FxiaokeTripRoundDao fxiaokeTripRoundDao;

    @Autowired
    private TaskConfig taskConfig;


    @Override
    public Integer getTaskType() {
        return TaskType.FXIAOKE_APPROVE_TRIP_CREATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        Example example = new Example(FxiaokeCorpApp.class);
        example.createCriteria().andEqualTo("corpId", corpId)
            .andEqualTo("appState", 0);
        FxiaokeCorpApp fxiaokeCorpApp = fxiaokeCorpAppDao.getByExample(example);
        if (!ObjectUtils.isEmpty(fxiaokeCorpApp)) {
            FxkGetCorpAccessTokenReqDTO tokenReqDTO = FxkGetCorpAccessTokenReqDTO.builder()
                .appId(fxiaokeCorpApp.getAppId())
                .appSecret(fxiaokeCorpApp.getAppSecret())
                .permanentCode(fxiaokeCorpApp.getPermanent())
                .build();
            FxkGetCorpAccessTokenRespDTO corpAccessToken = iFxkAccessTokenService.getCorpAccessToken(tokenReqDTO);
            if (!ObjectUtils.isEmpty(corpAccessToken)) {
                PluginCorpDefinition corpByThirdCorpId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
                if (!ObjectUtils.isEmpty(corpByThirdCorpId)) {
                    FxkGetCustomDataReqDTO fxkGetCustomDataReqDTO = new FxkGetCustomDataReqDTO();
                    fxkGetCustomDataReqDTO.setCorpAccessToken(corpAccessToken.getCorpAccessToken());
                    fxkGetCustomDataReqDTO.setCorpId(corpId);
                    //配置的调用接口人员ID，在配置数据时，需要客户提供
                    fxkGetCustomDataReqDTO.setCurrentOpenUserId(corpByThirdCorpId.getThirdAdminId());
                    Example example1 = new Example(FxiaokeObjApply.class);
                    example1.createCriteria()
                        .andEqualTo("corpId", corpId)
                        .andEqualTo("objState", 0)
                        .andEqualTo("objApplyType", 1);
//
                    FxiaokeObjApply byExample = fxiaokeObjApplyDao.getByExample(example1);
                    FxkGetCustomDataReqDTO.FxkGetCustomDataCondition fxkGetCustomDataCondition = new FxkGetCustomDataReqDTO.FxkGetCustomDataCondition();
                    fxkGetCustomDataCondition.setDataObjectApiName(byExample.getObjApiName());
                    fxkGetCustomDataCondition.setObjectDataId(dataId);
                    fxkGetCustomDataReqDTO.setData(fxkGetCustomDataCondition);
                    FxkGetCustomTripApprovalRespDTO tripCustomData = fxkCustomDataService.getTripCustomData(fxkGetCustomDataReqDTO);
                    if (!ObjectUtils.isEmpty(tripCustomData)) {
                        Integer errorCode = tripCustomData.getErrorCode();
                        if (0 == errorCode) {
                            FxkCustomTripApprovalDetail fxkCustomTripApprovalDetail = tripCustomData.getFxkCustomTripApprovalDetail();
                            String id = fxkCustomTripApprovalDetail.getId();
                            List<String> owner = fxkCustomTripApprovalDetail.getOwner();
                            String employeeId = owner.get(0);
                            // TODO 交通工具，包含多个类型，而且接收参数为ID，无法识别具体的类型，
                            // 需要根据数据库进行存储，然后根据传递过来的ID匹配出对应的交通工具名称
                            List<String> fbtTripTypeC = fxkCustomTripApprovalDetail.getFbtTripTypeC();
                            //出发城市
                            String fbtDepartureCityC = fxkCustomTripApprovalDetail.getFbtDepartureCityC();
                            //目的城市
                            String fbtDestinationCityC = fxkCustomTripApprovalDetail.getFbtDestinationCityC();
                            //
                            long fbtBeginDateC = fxkCustomTripApprovalDetail.getFbtBeginDateC();
                            long fbtEndDateC = fxkCustomTripApprovalDetail.getFbtEndDateC();
                            //单程往返
                            String fbtIsSingleC = fxkCustomTripApprovalDetail.getFbtIsSingleC();
                            Example fxiaokeTripR = new Example(FxiaokeTripRound.class);
                            fxiaokeTripR.createCriteria()
                                .andEqualTo("corpId", corpId)
                                .andEqualTo("state", 0)
                                .andEqualTo("tripId", fbtIsSingleC);
                            FxiaokeTripRound byExample2 = fxiaokeTripRoundDao.getByExample(fxiaokeTripR);
                            Integer roundTrip = 1;
                            if (!ObjectUtils.isEmpty(byExample2)) {
                                roundTrip = byExample2.getRoundTrip();
                            }
                            //出行人
                            List<String> fbtCompanionC = fxkCustomTripApprovalDetail.getFbtCompanionC();
                            long createTime = fxkCustomTripApprovalDetail.getCreateTime();
                            //申请事由
                            String fbtApplyReasonC = fxkCustomTripApprovalDetail.getFbtApplyReasonC();
                            //构建请求参数
                            //根据人员ID获取分贝token
                            String employeeToken = userCenterService.getUcEmployeeToken(corpByThirdCorpId.getAppId(), employeeId);
                            //构建公用审批数据
                            CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
                            CommonApply commonApply = new CommonApply();
                            commonApply.setApplyReason(fbtApplyReasonC);
                            commonApply.setThirdRemark(fbtApplyReasonC);
                            commonApply.setThirdId(id);
                            commonApply.setType(1);
                            commonApply.setFlowType(4);
                            commonApply.setBudget(0);
                            commonApply.setCompanyId(corpByThirdCorpId.getAppId());
                            commonApplyReqDTO.setApply(commonApply);

                            //根据解析出的交通工具类型进行行程数据的填充,行程列表
                            List<CommonApplyTrip> tripList = Lists.newArrayList();
                            Integer finalRoundTrip = roundTrip;
                            fbtTripTypeC.stream().forEach(trip -> {
                                Example example2 = new Example(FxiaokeApplyTrip.class);
                                example2.createCriteria()
                                    .andEqualTo("corpId", corpId)
                                    .andEqualTo("tripId", trip)
                                    .andEqualTo("tripStatus", 0);
                                FxiaokeApplyTrip byExample1 = fxiaokeApplyTripDao.getByExample(example2);

                                if (!ObjectUtils.isEmpty(byExample1)) {//根据交通工具ID可以查询到指定的交通工具类型
                                    //具体交通工具类型
                                    String tripType = byExample1.getTripType();
                                    CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
                                    commonApplyTrip.setType(Integer.valueOf(tripType));
                                    commonApplyTrip.setTripType(finalRoundTrip);
                                    commonApplyTrip.setEstimatedAmount(0);
                                    if ("11".equals(tripType)) {//酒店，城市全部取目的城市
                                        commonApplyTrip.setStartCityName(fbtDestinationCityC);
                                    } else {
                                        commonApplyTrip.setStartCityName(fbtDepartureCityC);
                                    }
                                    commonApplyTrip.setArrivalCityName(fbtDestinationCityC);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    String startTime = simpleDateFormat.format(new Date(fbtBeginDateC));
                                    String endTime = simpleDateFormat.format(new Date(fbtEndDateC));
                                    commonApplyTrip.setStartTime(startTime);
                                    commonApplyTrip.setEndTime(endTime);

                                    tripList.add(commonApplyTrip);
                                }
                            });
                            commonApplyReqDTO.setTripList(tripList);
                            //TODO 同行人数据可以先忽略
                            TripApproveCreateReqDTO tripApproveCreateReqDTO = (TripApproveCreateReqDTO) commonApplyService.convertApply(employeeToken, commonApplyReqDTO);
                            try {
                                CreateApplyRespDTO tripApprove = createTripApprove(employeeToken, tripApproveCreateReqDTO);
                                if (!ObjectUtils.isEmpty(tripApprove)) {
                                    String fbtApplyId = tripApprove.getId();
                                    if (StringUtils.isNotBlank(fbtApplyId)) {
                                        return TaskProcessResult.success("success");
                                    }
                                }
                                return TaskProcessResult.fail("fail");
                            } catch (Exception e) {
                                log.info("创建分贝通审批单失败");
                                //进行数据处理，返回错误数据
                                return TaskProcessResult.fail("创建分贝通审批单失败");
                            }
                        }
                    }
                }
            }
            return TaskProcessResult.fail("fail");
        }
        return TaskProcessResult.success("abort success");
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
