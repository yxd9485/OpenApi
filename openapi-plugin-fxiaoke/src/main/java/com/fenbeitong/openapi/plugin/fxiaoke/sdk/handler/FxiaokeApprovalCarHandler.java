package com.fenbeitong.openapi.plugin.fxiaoke.sdk.handler;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeCorpAppDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeObjApplyDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeCorpApp;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeObjApply;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkAccessTokenService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl.FxkCustomDataServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.sdk.dto.common.TypeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class FxiaokeApprovalCarHandler extends AbstractCarApplyService implements ITaskHandler {
    @Autowired
    FxkCustomDataServiceImpl fxkCustomDataService;
    @Autowired
    IFxkAccessTokenService iFxkAccessTokenService;
    @Autowired
    FxiaokeCorpAppDao fxiaokeCorpAppDao;
    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    FxiaokeObjApplyDao fxiaokeObjApplyDao;
    @Autowired
    CommonApplyServiceImpl commonApplyService;
    @Autowired
    UserCenterService userCenterService;


    @Override
    public TaskType getTaskType() {
        return TaskType.FXIAOKE_APPROVE_CAR_CREATE;
    }

    @Override
    public TaskResult execute(Task task) {
        String corpId = task.getCorpId();
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
                            .andEqualTo("objApplyType", "12");

                    FxiaokeObjApply byExample = fxiaokeObjApplyDao.getByExample(example1);
                    if (!ObjectUtils.isEmpty(byExample)) {
                        FxkGetCustomDataReqDTO.FxkGetCustomDataCondition fxkGetCustomDataCondition = new FxkGetCustomDataReqDTO.FxkGetCustomDataCondition();
                        fxkGetCustomDataCondition.setDataObjectApiName(byExample.getObjApiName());
                        fxkGetCustomDataCondition.setObjectDataId(dataId);
                        fxkGetCustomDataReqDTO.setData(fxkGetCustomDataCondition);
                        FxkGetCustomCarApprovalRespDTO customData = fxkCustomDataService.getCarCustomData(fxkGetCustomDataReqDTO);
                        if (!ObjectUtils.isEmpty(customData)) {//查询返回具体审批详情数据
                            Integer errorCode = customData.getErrorCode();
                            if (0 == errorCode) {
                                FxkCustomCarApprovalDetail dataDetail = customData.getFxkCustomCarApprovalDetail();
                                String id = dataDetail.getId();
                                String fbtCarCityC = dataDetail.getFbtCarCityC();
                                String fbtApplyReasonC = dataDetail.getFbtApplyReasonC();
                                String fbtCarCountC = dataDetail.getFbtCarCountC();
                                String fbtCarCostC = dataDetail.getFbtCarCostC();
                                long fbtBeginDateC = dataDetail.getFbtBeginDateC();
                                long fbtEndDateC = dataDetail.getFbtEndDateC();
                                List<String> owner = dataDetail.getOwner();
                                String employeeId = owner.get(0);
                                //根据人员ID获取分贝token
                                String ucToken = userCenterService.getUcEmployeeToken(corpByThirdCorpId.getAppId(), employeeId);
                                //构建公用审批数据
                                CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
                                CommonApply commonApply = new CommonApply();
                                commonApply.setApplyReason(fbtApplyReasonC);
                                commonApply.setThirdRemark(fbtApplyReasonC);
                                commonApply.setThirdId(id);
                                commonApply.setType(12);
                                commonApply.setFlowType(4);
                                commonApplyReqDTO.setApply(commonApply);
                                CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
                                commonApplyTrip.setType(3);
                                commonApplyTrip.setStartCityName(fbtCarCityC);
                                commonApplyTrip.setArrivalCityName(fbtCarCityC);
                                //设置日期格式
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String startTime = simpleDateFormat.format(new Date(fbtBeginDateC));
                                String endTime = simpleDateFormat.format(new Date(fbtEndDateC));
                                commonApplyTrip.setStartTime(startTime);
                                commonApplyTrip.setEndTime(endTime);
                                List<CommonApplyTrip> commonApplyTripList = new ArrayList<>();
                                commonApplyTripList.add(commonApplyTrip);
                                commonApplyReqDTO.setTripList(commonApplyTripList);


                                ArrayList<TypeEntity> taxiRuleList = new ArrayList<TypeEntity>();
                                TypeEntity typeEntity = new TypeEntity();
                                typeEntity.setType("taxi_scheduling_fee");
                                typeEntity.setValue("-1");
                                TypeEntity typeEntity1 = new TypeEntity();
                                typeEntity1.setType("allow_same_city");
                                typeEntity1.setValue("false");
                                TypeEntity typeEntity2 = new TypeEntity();
                                typeEntity2.setType("allow_called_for_other");
                                typeEntity2.setValue("true");
                                TypeEntity typeEntity3 = new TypeEntity();
                                typeEntity3.setType("price_limit");
                                typeEntity3.setValue("-1");
                                TypeEntity typeEntity4 = new TypeEntity();
                                typeEntity4.setType("day_price_limit");
                                typeEntity4.setValue("-1");
                                TypeEntity typeEntity5 = new TypeEntity();
                                typeEntity5.setType("times_limit_flag");
                                typeEntity5.setValue("2");
                                TypeEntity typeEntity6 = new TypeEntity();
                                //设置使用次数
                                typeEntity6.setType("times_limit");
                                typeEntity6.setValue(fbtCarCountC);
                                TypeEntity typeEntity7 = new TypeEntity();
                                //设置金额
                                typeEntity7.setType("total_price");
                                typeEntity7.setValue(fbtCarCostC);
                                TypeEntity typeEntity8 = new TypeEntity();
                                typeEntity8.setType("city_limit");
                                typeEntity8.setValue("1");
                                TypeEntity typeEntity9 = new TypeEntity();
                                typeEntity9.setType("price_limit_flag");
                                typeEntity9.setValue("2");

                                taxiRuleList.add(typeEntity);
                                taxiRuleList.add(typeEntity1);
                                taxiRuleList.add(typeEntity2);
                                taxiRuleList.add(typeEntity3);
                                taxiRuleList.add(typeEntity4);
                                taxiRuleList.add(typeEntity5);
                                taxiRuleList.add(typeEntity6);
                                taxiRuleList.add(typeEntity7);
                                taxiRuleList.add(typeEntity8);
                                taxiRuleList.add(typeEntity9);

                                commonApplyReqDTO.setApplyTaxiRuleInfo(taxiRuleList);

                                CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
                                //创建用车审批
                                try {
                                    CreateApplyRespDTO carApprove = createCarApprove(ucToken, carApproveCreateReqDTO);
                                    if (!ObjectUtils.isEmpty(carApprove)) {
                                        String fbtApplyId = carApprove.getId();
                                        if (StringUtils.isNotBlank(fbtApplyId)) {
                                            return TaskResult.SUCCESS;
                                        }
                                    }
                                    return TaskResult.FAIL;
                                } catch (Exception e) {
                                    log.info("创建分贝通审批单失败");
                                    //进行数据处理，返回错误数据
                                    return TaskResult.FAIL;
                                }
                            } else {//返回异常错误
                                log.info("查询纷享销客用车审批异常, errorCode:{} ,errorMsg:{}", customData.getErrorCode(), customData.getErrorMessage());
                            }
                        }
                        return TaskResult.FAIL;
                    }
                }
            }
        }
        return TaskResult.ABORT;
    }
}
