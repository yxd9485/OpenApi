package com.fenbeitong.openapi.plugin.func.budget.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckPushReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckReqDTO;
import com.fenbeitong.openapi.plugin.func.budget.dto.ThirdBudgetCheckRespDTO;
import com.fenbeitong.openapi.plugin.func.budget.service.IFuncThirdBudgetService;
import com.fenbeitong.openapi.plugin.func.budget.service.IThirdBudgetCheckService;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.util.EmployeeUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncThirdBudgetServiceImpl</p>
 * <p>Description: 三方预算服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/6 5:08 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncThirdBudgetServiceImpl implements IFuncThirdBudgetService {

    @DubboReference(check = false)
    private ICommonService commonService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private ThirdCallbackConfDao thirdCallbackConfDao;

    @Autowired
    private RestHttpUtils httpUtils;

    @Autowired
    private EmployeeUtils employeeUtils;

    @Override
    public ThirdBudgetCheckRespDTO checkBudget(ThirdBudgetCheckReqDTO checkReq) {
        ThirdCallbackConf thirdCallbackConf = thirdCallbackConfDao.queryByCompanyIdAndCallBackType(checkReq.getCompanyId(), CallbackType.BUDGET_CHECK.getType());
        if (thirdCallbackConf == null) {
            return buildThirdBudgetCheckFailResp();
        }
        String callbackUrl = thirdCallbackConf.getCallbackUrl();
        String result = null;
        try {
            result = httpUtils.postJson(callbackUrl, buildData(checkReq));
        } catch (Exception e) {
            log.warn("post budget error", e);
            return buildThirdBudgetCheckFailResp();
        }
        Class callbackServiceClass = getCallbackServiceClass(thirdCallbackConf.getCallbackService());
        if (callbackServiceClass == null) {
            return buildThirdBudgetCheckFailResp();
        }
        return checkThirdBudget(callbackServiceClass, checkReq, result);
    }

    @SuppressWarnings("unchecked")
    private ThirdBudgetCheckRespDTO checkThirdBudget(Class callbackServiceClass, ThirdBudgetCheckReqDTO checkReq, String result) {
        IThirdBudgetCheckService budgetService = (IThirdBudgetCheckService) SpringUtils.getBean(callbackServiceClass);
        return budgetService.checkThirdBudget(checkReq, result);
    }

    private Class getCallbackServiceClass(String callbackService) {
        Class clazz = null;
        try {
            clazz = callbackService == null ? ThirdBudgetCheckFailedServiceImpl.class : Class.forName(callbackService);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz == null ? ThirdBudgetCheckFailedServiceImpl.class : clazz;
    }

    private String buildData(ThirdBudgetCheckReqDTO checkReq) {
        List<EmployeeContract> employeeList = employeeUtils.queryEmployees(Lists.newArrayList(checkReq.getEmployeeId()), checkReq.getCompanyId());
        EmployeeContract employeeContract = employeeList.get(0);
        ThirdBudgetCheckPushReqDTO pushReq = new ThirdBudgetCheckPushReqDTO();
        pushReq.setOrgUnitId(employeeContract.getThird_org_id());
        pushReq.setOrgUnitName(employeeContract.getOrg_name());
        pushReq.setOrgUnitFullName(employeeContract.getOrg_full_name());
        pushReq.setEmployeeId(employeeContract.getThird_employee_id());
        pushReq.setEmployeeName(employeeContract.getName());
        pushReq.setOrderId(checkReq.getOrderId());
        pushReq.setType(checkReq.getType());
        pushReq.setTotalPrice(checkReq.getTotalPrice());
        List<ThirdBudgetCheckReqDTO.ThirdBudgetCheckCostAttribution> attributionList = checkReq.getCostAttributionList();
        if (!ObjectUtils.isEmpty(attributionList)) {
            List<ThirdBudgetCheckPushReqDTO.ThirdBudgetCheckPushCostAttribution> pushCostAttributionList = Lists.newArrayList();
            Map<Integer, List<ThirdBudgetCheckReqDTO.ThirdBudgetCheckCostAttribution>> costMap = attributionList.stream().collect(Collectors.groupingBy(ThirdBudgetCheckReqDTO.ThirdBudgetCheckCostAttribution::getCostAttributionCategory));
            costMap.forEach((type, costList) -> {
                List<String> costIdList = costList.stream().map(ThirdBudgetCheckReqDTO.ThirdBudgetCheckCostAttribution::getCostAttributionId).collect(Collectors.toList());
                if (!ObjectUtils.isEmpty(costIdList)) {
                    List<CommonIdDTO> idDtoList = commonService.queryIdDTO(checkReq.getCompanyId(), costIdList, 1, type);
                    Map<String, String> idMap = idDtoList.stream().filter(id->!ObjectUtils.isEmpty(id.getThirdId())).collect(Collectors.toMap(CommonIdDTO::getId, CommonIdDTO::getThirdId, (o, n) -> n));
                    costList.forEach(cost -> {
                        ThirdBudgetCheckPushReqDTO.ThirdBudgetCheckPushCostAttribution costAttribution = new ThirdBudgetCheckPushReqDTO.ThirdBudgetCheckPushCostAttribution();
                        costAttribution.setCostAttributionId(idMap.get(cost.getCostAttributionId()));
                        costAttribution.setCostAttributionName(cost.getCostAttributionName());
                        costAttribution.setCostAttributionCategory(type);
                        pushCostAttributionList.add(costAttribution);
                    });
                }
            });
            pushReq.setCostAttributionList(pushCostAttributionList);
        }
        return JsonUtils.toJson(pushReq);
    }

    private ThirdBudgetCheckRespDTO buildThirdBudgetCheckFailResp() {
        return ThirdBudgetCheckRespDTO.builder().withinBudget(2).budgetInfo("调用第三方预算失败，请稍后尝试").build();
    }
}
