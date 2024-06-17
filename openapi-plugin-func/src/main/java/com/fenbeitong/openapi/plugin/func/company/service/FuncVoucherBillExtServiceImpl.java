package com.fenbeitong.openapi.plugin.func.company.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.fenbeipay.api.base.ResponsePage;
import com.fenbeitong.fenbeipay.api.model.dto.vouchers.VouchersTasksReqRPCDTO;
import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VoucherCostAttributionRPCDTO;
import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VouchersOperationFlowRespRPCDTO;
import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VouchersTaskDetailsRespDTO;
import com.fenbeitong.fenbeipay.api.model.dto.vouchers.resp.VouchersTaskRespDTO;
import com.fenbeitong.fenbeipay.api.service.voucher.IVouchersPersonService;
import com.fenbeitong.fenbeipay.api.service.voucher.IVouchersTaskService;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncUserThirdInfoDTO;
import com.fenbeitong.openapi.plugin.rpc.api.func.service.IVoucherBillExtService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.EmployeeUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoReqDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoResDTO;
import com.fenbeitong.usercenter.api.model.dto.costcenter.CostCenterDto;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.costcenter.ICostCenterService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.core.SpringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncVoucherBillExtServiceImpl</p>
 * <p>Description: 分贝券发放及消费账单扩展字段 </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/19 8:57 PM
 */
@Component
@DubboService(timeout = 15000, interfaceClass = IVoucherBillExtService.class)
public class FuncVoucherBillExtServiceImpl implements IVoucherBillExtService {

    @DubboReference(check = false)
    private IVouchersTaskService vouchersTaskService;

    @DubboReference(check = false)
    private IVouchersPersonService vouchersPersonService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private ICommonService commonService;

    @DubboReference(check = false)
    private ICostCenterService costCenterService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private EmployeeUtils employeeUtils;

    @Override
    public Map<String, Map<String, Object>> getVoucherTaskExtInfo(String companyId, List<String> vouchersTaskIdList) {
        if (ObjectUtils.isEmpty(companyId) || ObjectUtils.isEmpty(vouchersTaskIdList)) {
            throw new FinhubException(500, "参数错误");
        }
        List<VouchersTaskDetailsRespDTO> vouchersTaskDetailList = Lists.newArrayList();
        int pageIndex = 1;
        ResponsePage<VouchersTaskDetailsRespDTO> taskDetailResp = vouchersTaskService.queryVouchersGrantTaskDetails(companyId, vouchersTaskIdList, pageIndex, 1000);
        while (taskDetailResp != null && !ObjectUtils.isEmpty(taskDetailResp.getDataList())) {
            List<VouchersTaskDetailsRespDTO> dataList = taskDetailResp.getDataList();
            vouchersTaskDetailList.addAll(dataList);
            taskDetailResp = vouchersTaskService.queryVouchersGrantTaskDetails(companyId, vouchersTaskIdList, ++pageIndex, 1000);
        }
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(vouchersTaskDetailList)) {
            Set<String> taskIdList = vouchersTaskDetailList.stream().map(VouchersTaskDetailsRespDTO::getVouchersTaskId).collect(Collectors.toSet());
            VouchersTasksReqRPCDTO tasksReq = new VouchersTasksReqRPCDTO();
            tasksReq.setVouchersTaskIds(Lists.newArrayList(taskIdList));
            List<VouchersTaskRespDTO> taskRespList = vouchersTaskService.queryVouchersTask(tasksReq);
            //所有员工id
            Set<String> userIdList = Sets.newHashSet();
            //发放人id
            Set<String> operateUserIdList = taskRespList.stream().map(VouchersTaskRespDTO::getOperationUserId).collect(Collectors.toSet());
            //员工id
            Set<String> employeeIdList = vouchersTaskDetailList.stream().map(VouchersTaskDetailsRespDTO::getEmployeeId).collect(Collectors.toSet());
            userIdList.addAll(operateUserIdList);
            userIdList.addAll(employeeIdList);
            //获取人员三方信息
            Map<String, FuncUserThirdInfoDTO> userThirdMap = getUserThirdMap(companyId, Lists.newArrayList(userIdList));
            Map<String, VouchersTaskRespDTO> taskRespMap = taskRespList.stream().collect(Collectors.toMap(VouchersTaskRespDTO::getVouchersTaskId, Function.identity()));
            for (VouchersTaskDetailsRespDTO detail : vouchersTaskDetailList) {
                Map<String, Object> thirdInfo = Maps.newHashMap();
                VouchersTaskRespDTO taskResp = taskRespMap.get(detail.getVouchersTaskId());
                //发放人id
                String operationUserId = taskResp.getOperationUserId();
                if ("系统操作".equals(operationUserId)) {
                    thirdInfo.put("operateUserId", "系统操作");
                    thirdInfo.put("operateDeptId", "系统操作");
                } else {
                    //发放人三方信息
                    FuncUserThirdInfoDTO operateUserThirdInfo = userThirdMap.get(operationUserId);
                    if (operateUserThirdInfo != null) {
                        thirdInfo.put("operateUserId", operateUserThirdInfo.getThirdEmployeeId());
                        thirdInfo.put("operateDeptId", operateUserThirdInfo.getThirdDeptId());
                    }
                }
                //员工id
                String employeeId = detail.getEmployeeId();
                //员工三方信息
                FuncUserThirdInfoDTO employeeUserThirdInfo = userThirdMap.get(employeeId);
                if (employeeUserThirdInfo != null) {
                    thirdInfo.put("bookerUserId", employeeUserThirdInfo.getThirdEmployeeId());
                    thirdInfo.put("bookerDeptId", employeeUserThirdInfo.getThirdDeptId());
                }
                if (!ObjectUtils.isEmpty(thirdInfo)) {
                    resultMap.put(detail.getVouchersTaskDetailsId(), thirdInfo);
                }
            }
        }
        return resultMap;
    }

    private Map<String, FuncUserThirdInfoDTO> getUserThirdMap(String companyId, List<String> userIdList) {
        Map<String, FuncUserThirdInfoDTO> userThirdMap = Maps.newHashMap();
        List<EmployeeContract> employeeList = employeeUtils.queryEmployees(userIdList, companyId);
        employeeList.forEach(employee -> {
            FuncUserThirdInfoDTO thirdInfo = new FuncUserThirdInfoDTO();
            thirdInfo.setThirdEmployeeId(employee.getThird_employee_id());
            thirdInfo.setThirdDeptId(employee.getThird_org_id());
            thirdInfo.setExpand(employee.getExpand());
            thirdInfo.setEmail(employee.getEmail());
            userThirdMap.put(employee.getId(), thirdInfo);
        });
        return userThirdMap;
    }

    @Override
    public Map<String, Map<String, Object>> getVoucherFlowExtInfo(String companyId, List<String> voucherFlowIdList) {
        if (ObjectUtils.isEmpty(companyId) || ObjectUtils.isEmpty(voucherFlowIdList)) {
            throw new FinhubException(500, "参数错误");
        }
        List<VouchersOperationFlowRespRPCDTO> voucherFlowList = vouchersPersonService.queryVoucherFlowList(voucherFlowIdList);
        return getVoucherFlowExtInfoByFlowList(companyId, voucherFlowList);
    }

    public Map<String, Map<String, Object>> getVoucherFlowExtInfoByFlowList(String companyId, List<VouchersOperationFlowRespRPCDTO> voucherFlowList) {
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(voucherFlowList)) {
            //使用人id
            Set<String> employeeIdList = voucherFlowList.stream().map(VouchersOperationFlowRespRPCDTO::getEmployeeId).collect(Collectors.toSet());
            //原始申领人id
            Set<String> originalVoucherEmployeeIds = voucherFlowList.stream().map(VouchersOperationFlowRespRPCDTO::getOriginalVoucherEmployeeId).collect(Collectors.toSet());
            if (!ObjectUtils.isEmpty(originalVoucherEmployeeIds)) {
                employeeIdList.addAll(originalVoucherEmployeeIds);
            }
            //获取人员三方信息
            Map<String, FuncUserThirdInfoDTO> userThirdMap = getUserThirdMap(companyId, Lists.newArrayList(employeeIdList));
            //费用归属信息
            List<VoucherCostAttributionRPCDTO> costAttributionList = voucherFlowList.stream().filter(flow -> !ObjectUtils.isEmpty(flow.getVoucherCostAttributions())).flatMap(flow -> flow.getVoucherCostAttributions().stream()).collect(Collectors.toList());
            //费用归属三方信息
            Map<String, Map<String, Object>> costAttributionThirdMap = getCostAttributionThirdMap(companyId, costAttributionList);
            for (VouchersOperationFlowRespRPCDTO flowRespDto : voucherFlowList) {
                Map<String, Object> thirdInfo = Maps.newHashMap();
                String employeeId = flowRespDto.getEmployeeId();
                FuncUserThirdInfoDTO thirdInfoDto = userThirdMap.get(employeeId);
                if (thirdInfoDto != null) {
                    //消费人三方信息
                    thirdInfo.put("bookerUserId", thirdInfoDto.getThirdEmployeeId());
                    thirdInfo.put("bookerDeptId", thirdInfoDto.getThirdDeptId());
                }
                String originalVoucherEmployeeId = flowRespDto.getOriginalVoucherEmployeeId();
                FuncUserThirdInfoDTO voucherEmployeeThirdInfo = userThirdMap.get(originalVoucherEmployeeId);
                if (voucherEmployeeThirdInfo != null) {
                    //原始分贝券申领人
                    thirdInfo.put("originalVoucherUserId", voucherEmployeeThirdInfo.getThirdEmployeeId());
                    thirdInfo.put("originalVoucherDeptId", voucherEmployeeThirdInfo.getThirdDeptId());
                    //扩展字段
                    String expand = voucherEmployeeThirdInfo.getExpand();
                    if (!ObjectUtils.isEmpty(expand)) {
                        List<Map<String, Object>> expandList = JsonUtils.toObj(expand, new TypeReference<List<Map<String, Object>>>() {
                        });
                        if (!ObjectUtils.isEmpty(expandList)) {
                            Map<String, Object> extMap = expandList.get(0);
                            for (String key : extMap.keySet()) {
                                if (ObjectUtils.isEmpty(key)) {
                                    continue;
                                }
                                thirdInfo.put("original" + key.substring(0, key.length() - 1), extMap.get(key));
                            }
                        }
                    }
                }
                List<VoucherCostAttributionRPCDTO> voucherCostAttributions = flowRespDto.getVoucherCostAttributions();
                if (!ObjectUtils.isEmpty(voucherCostAttributions) && !ObjectUtils.isEmpty(costAttributionThirdMap)) {
                    for (VoucherCostAttributionRPCDTO voucherCostAttribution : voucherCostAttributions) {
                        Map<String, Object> costThirdMap = costAttributionThirdMap.get(voucherCostAttribution.getCostAttributionId() + voucherCostAttribution.getCostAttributionType());
                        if (!ObjectUtils.isEmpty(costThirdMap)) {
                            thirdInfo.putAll(costThirdMap);
                        }
                    }
                }
                List<OpenMsgSetup> setupList = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("voucher_bill_ext_config"));
                OpenMsgSetup setup = ObjectUtils.isEmpty(setupList) ? null : setupList.get(0);
                if (setup != null) {
                    String extListener = setup.getStrVal1();
                    if (!ObjectUtils.isEmpty(extListener)) {
                        IVoucherBillExtListener voucherBillExtListener = getIVoucherBillExtListener(extListener);
                        if (voucherBillExtListener != null) {
                            voucherBillExtListener.setBillExt(companyId, flowRespDto, thirdInfo);
                        }
                    }
                }
                if (!ObjectUtils.isEmpty(thirdInfo)) {
                    resultMap.put(flowRespDto.getId(), thirdInfo);
                }
            }
        }
        return resultMap;
    }

    private IVoucherBillExtListener getIVoucherBillExtListener(String extListener) {
        Class clazz = null;
        try {
            clazz = Class.forName(extListener);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz != null) {
            Object bean = SpringUtils.getBean(clazz);
            if (bean != null && bean instanceof IVoucherBillExtListener) {
                return (IVoucherBillExtListener) bean;
            }
        }
        return null;
    }

    private Map<String, Map<String, Object>> getCostAttributionThirdMap(String companyId, List<VoucherCostAttributionRPCDTO> costAttributionList) {
        Map<String, Map<String, Object>> costAttributionThirdMap = Maps.newHashMap();
        if (ObjectUtils.isEmpty(costAttributionList)) {
            return costAttributionThirdMap;
        }
        Map<Integer, List<VoucherCostAttributionRPCDTO>> costAttributionMap = costAttributionList.stream().collect(Collectors.groupingBy(VoucherCostAttributionRPCDTO::getCostAttributionType));
        costAttributionMap.forEach((costAttributionType, costAttributions) -> {
            List<String> costIdList = costAttributions.stream().map(VoucherCostAttributionRPCDTO::getCostAttributionId).collect(Collectors.toList());
            CommonInfoReqDTO req = new CommonInfoReqDTO();
            req.setType(IdTypeEnums.FB_ID.getKey());
            req.setBusinessType(costAttributionType);
            req.setIdList(costIdList);
            req.setCompanyId(companyId);
            List<CommonInfoResDTO> commonInfoResList = commonService.queryCommonInfoByType(req);
            Map<String, CommonInfoResDTO> commonInfoResMap = ObjectUtils.isEmpty(commonInfoResList) ? Maps.newHashMap() : commonInfoResList.stream().collect(Collectors.toMap(CommonInfoResDTO::getId, Function.identity()));
            boolean isDept = costAttributionType == IdBusinessTypeEnums.ORG.getKey();
            boolean isProject = costAttributionType == IdBusinessTypeEnums.COSTCENTER.getKey();
            Map<String, CostCenterDto> costCenterDtoMap = null;
            if (isProject) {
                List<CostCenterDto> costCenterDtos = costCenterService.queryCostCenterListByIdList(companyId, costIdList);
                costCenterDtoMap = costCenterDtos == null ? null : costCenterDtos.stream().collect(Collectors.toMap(CostCenterDto::getId, Function.identity()));
            }
            for (VoucherCostAttributionRPCDTO costAttribution : costAttributions) {
                Map<String, Object> thirdMap = Maps.newHashMap();
                String costAttributionId = costAttribution.getCostAttributionId();
                CommonInfoResDTO commonInfoRes = commonInfoResMap.get(costAttributionId);
                if (isDept) {
                    thirdMap.put("costAttributionDeptId", commonInfoRes == null ? null : commonInfoRes.getThirdId());
                    thirdMap.put("costAttributionDeptName", commonInfoRes == null ? null : commonInfoRes.getName());
                } else if (isProject) {
                    CostCenterDto costCenterDto = costCenterDtoMap == null ? null : costCenterDtoMap.get(commonInfoRes.getId());
                    thirdMap.put("costAttributioncostId", commonInfoRes == null ? null : commonInfoRes.getThirdId());
                    thirdMap.put("costAttributioncostCode", costCenterDto == null ? null : costCenterDto.getCode());
                    thirdMap.put("costAttributioncostName", costCenterDto == null ? null : costCenterDto.getName());
                }
                if (!ObjectUtils.isEmpty(thirdMap)) {
                    costAttributionThirdMap.put(costAttributionId + costAttributionType, thirdMap);
                }
            }

        });
        return costAttributionThirdMap;
    }
}
