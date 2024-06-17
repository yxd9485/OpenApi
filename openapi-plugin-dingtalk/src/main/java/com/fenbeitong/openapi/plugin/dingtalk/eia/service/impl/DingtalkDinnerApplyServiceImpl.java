package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.finhub.common.utils.NumericUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.constant.DingtalkProcessBizActionType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dao.DingtalkProcessInstanceDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkCarApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkProcessInstance;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkProcessApplyService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.DinnerApproveApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.DinnerApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.DinnerApproveDetail;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractDinnerApplyService;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * 用餐申请单
 *
 * @author yan.pb
 * @date 2021/2/2
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkDinnerApplyServiceImpl extends AbstractDinnerApplyService implements IDingtalkProcessApplyService {

    @Autowired
    private OpenApiAuthServiceImpl openApiAuthService;

    @Autowired
    private DingtalkProcessInstanceDao dingtalkProcessInstanceDao;

    @Autowired
    CityCodeService cityCodeService;


    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition dingtalkCorp, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) throws BindException {
        String bizAction = processInstanceTopVo.getBizAction();
        if (DingtalkProcessBizActionType.NONE.getValue().equalsIgnoreCase(bizAction)) {
            DinnerApproveCreateReqDTO processInfo = parse(dingtalkCorp.getAppId(), apply.getProcessType(), task.getDataId(), processInstanceTopVo);
            if (processInfo == null) {
                log.info("不符合分贝通审批用车申请单创建规则， 标记为废弃任务, taskId: {}, processInstanceId: {}", task.getId(), task.getDataId());
                return TaskResult.ABORT;
            }

            String token = openApiAuthService.getEmployeeFbToken(dingtalkCorp.getAppId(), processInstanceTopVo.getOriginatorUserid(), "1");
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            String data = gson.toJson(processInfo);
            DinnerApproveCreateReqDTO carApproveCreateReqDTO = JsonUtils.toObj(data, DinnerApproveCreateReqDTO.class);
            createDinnerApply(token, processInfo);
        }
        //记录审批实例信息
        saveDingtalkProcessInstance(task, apply, processInstanceTopVo);
        return TaskResult.SUCCESS;
    }


    private void saveDingtalkProcessInstance(Task task, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        // 记录审批实例信息
        DingtalkProcessInstance instance = new DingtalkProcessInstance();
        instance.setCorpId(task.getCorpId());
        instance.setTitle(processInstanceTopVo.getTitle());
        instance.setBizAction(processInstanceTopVo.getBizAction());
        instance.setBusinessId(processInstanceTopVo.getBusinessId());
        instance.setInstanceId(task.getDataId());
        instance.setProcessCode(apply.getProcessCode());
        instance.setUserId(processInstanceTopVo.getOriginatorUserid());
        dingtalkProcessInstanceDao.saveSelective(instance);
    }

    private DinnerApproveCreateReqDTO parse(String companyId, int processType, String instanceId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        DinnerApproveCreateReqDTO processInfo = new DinnerApproveCreateReqDTO();
        // 设置审批信息
        DinnerApproveApply apply = new DinnerApproveApply();
        apply.setType(processType);
        //必须为4
        apply.setFlowType(4);
        apply.setThirdId(instanceId);
        apply.setCompanyId(companyId);
        apply.setThirdRemark(processInstanceTopVo.getTitle());
        // 行程表单信息
        List<OapiProcessinstanceGetResponse.FormComponentValueVo> formComponentList = processInstanceTopVo.getFormComponentValues();
        log.info("钉钉用餐申请表单-{}", com.luastar.swift.base.json.JsonUtils.toJson(formComponentList));

        List<DinnerApproveDetail> tripList = Lists.newArrayList();
        DinnerApproveDetail detail = new DinnerApproveDetail();
        for (OapiProcessinstanceGetResponse.FormComponentValueVo formComponent : formComponentList) {
            switch (formComponent.getName()) {
                case DingTalkConstant.dinner.KEY_APPLY_REASON:
                    apply.setApplyReason(ObjectUtils.isEmpty(formComponent.getValue()) || "null".equals(formComponent.getValue()) ? null : formComponent.getValue());
                    break;
                case DingTalkConstant.dinner.KEY_START_CITY:
                    CityBaseInfo city = cityCodeService.getIdByName(formComponent.getValue());
                    if (ObjectUtil.isNotNull(city)) {
                        detail.setStartCityId(city.getId());
                    }
                    break;
                case DingTalkConstant.dinner.KEY_START_END_TIME:
                    List<String> values = JsonUtils.toObj(formComponent.getValue(), List.class);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        if (!CollectionUtils.isBlank(values)) {
                            detail.setStartTime(sdf.parse(values.get(0)));
                            detail.setEndTime(sdf.parse(values.get(1)));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case DingTalkConstant.dinner.KEY_USER_COUNT:
                    detail.setPersonCount(Integer.parseInt(formComponent.getValue()));
                    break;
                case DingTalkConstant.dinner.KEY_TRIP_FEE:
                    detail.setEstimatedAmount(NumericUtils.obj2int(formComponent.getValue()) * 100);
                    break;
                default:
                    break;
            }
        }
        apply.setBudget(detail.getEstimatedAmount());
        tripList.add(detail);
        processInfo.setTripList(tripList);
        processInfo.setApply(apply);
        return processInfo;
    }
}

