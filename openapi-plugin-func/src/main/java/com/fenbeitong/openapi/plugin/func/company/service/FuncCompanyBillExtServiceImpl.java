package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenCompanyBillExtConfigDao;
import com.fenbeitong.openapi.plugin.func.company.dto.CostAttributionDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoResDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncProjectDetailDTO;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.handler.FuncExceptionHandler;
import com.fenbeitong.openapi.plugin.rpc.api.func.model.CompanyBillExtInfoReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.SaasApplyCustomFieldRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.service.ICompanyService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoReqDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoResDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitResult;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncCompanyBillExtServiceImpl</p>
 * <p>Description: 公司账单三方id</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/1/15 8:52 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncCompanyBillExtServiceImpl implements IFuncCompanyBillExtService {

    @Autowired
    private ICompanyService companyService;

    @Autowired
    private FuncEmployeeService employeeService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private ICommonService commonService;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    @Autowired
    private FuncCompanyOrderServiceImpl companyOrderService;

    @Autowired
    private IEtlService etlService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private OpenCompanyBillExtConfigDao companyBillExtConfigDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private UcProjectServiceImpl projectService;

    @Override
    public Map<String, Object> getOrderThirdInfo(CompanyBillExtInfoReqDTO req) {
        //检查参数
        checkReq(req);
        return doGetExtInfo(req);
    }

    private Map<String, Object> doGetExtInfo(CompanyBillExtInfoReqDTO req) {
        try {
            Map<String, Object> result = Maps.newHashMap();
            Map<String, Object> data = getExtInfoByConfig(req);
            if (!ObjectUtils.isEmpty(data)) {
                result.putAll(data);
            }
            return result;
        } catch (Exception e) {
            FuncResultEntity funcResultEntity = FuncExceptionHandler.handlerException(e);
            throw new FinhubException(funcResultEntity.getCode(), funcResultEntity.getMsg());
        }
    }

    private Map<String, Object> getExtInfoByConfig(CompanyBillExtInfoReqDTO req) {
        Map<String, Object> srcData = companyOrderService.getOrder(req.getOrderId(), req.getTicketId(), req.getType());
        if (!ObjectUtils.isEmpty(srcData)) {
            Long companyBillEtlConfigId = (Long) srcData.get("companyBillEtlConfigId");
            Map<String, Object> transformMap = getTransformData(companyBillEtlConfigId, srcData, req.getCompanyId());
            log.info("transformMap1: {}", JsonUtils.toJson(transformMap));
            FuncBillExtInfoTransformDTO transformDto = JsonUtils.toObj(JsonUtils.toJson(transformMap), FuncBillExtInfoTransformDTO.class);
            return getExtInfo(req.getCompanyId(), srcData, transformDto);
        }
        return Maps.newHashMap();
    }

    @Override
    public Map<String, Object> getExtInfo(String companyId, Map<String, Object> srcData, FuncBillExtInfoTransformDTO transformDto) {
        if (transformDto != null) {
            //如果为空先重置一下使用人和同住人
            setPassengerInfo(companyId, transformDto);
            log.info("transformMap2: {}", JsonUtils.toJson(transformDto));
            FuncBillExtInfoResDTO result = buildFuncBillExtInfoResDTO(companyId, transformDto);
            log.info("FuncBillExtInfoResDTO: {}", JsonUtils.toJson(result));
            Map<String, Object> resultMap = JsonUtils.toObj(JsonUtils.obj2JsonByFiledOrder(result, false), Map.class);
            Map extMap = (Map) resultMap.get("extMap");
            if (extMap != null) {
                resultMap.putAll(extMap);
            }
            List<OpenMsgSetup> setupList = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_bill_ext_etl_config_id"));
            OpenMsgSetup setup = ObjectUtils.isEmpty(setupList) ? null : setupList.get(0);
            if (setup == null) {
                return resultMap;
            }
            long transferEtlId = NumericUtils.obj2long(setup.getIntVal1(), -1);
            Map<String, Object> newResultMap = transferEtlId == -1 ? resultMap : etlService.transform(transferEtlId, resultMap);
            log.info("final transform resultMap: {}", JsonUtils.toJson(newResultMap));
            String extListener = setup.getStrVal1();
            if (!ObjectUtils.isEmpty(extListener)) {
                ICompanyBillExtListener companyBillExtListener = getCompanyBillExtListener(extListener);
                if (companyBillExtListener != null) {
                    companyBillExtListener.setBillExt(companyId, srcData, newResultMap, transformDto);
                }
            }
            log.info("final resultMap: {}", JsonUtils.toJson(newResultMap));
            return newResultMap;
        }
        return Maps.newHashMap();
    }

    private void setPassengerInfo(String companyId, FuncBillExtInfoTransformDTO transformDto) {
        String userId = transformDto.getUserId();
        String userDeptId = transformDto.getUserDeptId();
        List<String> userPhones = ObjectUtils.isEmpty(transformDto.getUserPhone()) ? Lists.newArrayList() : Lists.newArrayList(transformDto.getUserPhone().split(","));
        String liveWithUserId = transformDto.getLiveWithUserId();
        String liveWithDeptId = transformDto.getLiveWithDeptId();
        List<String> liveWithUserPhones = ObjectUtils.isEmpty(transformDto.getLiveWithUserPhone()) ? Lists.newArrayList() : Lists.newArrayList(transformDto.getLiveWithUserPhone().split(","));
        Set<String> userPhoneSet = Sets.newHashSet();
        boolean updateUser = (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(userDeptId)) && !ObjectUtils.isEmpty(userPhones);
        boolean updateLiveWithUser = (ObjectUtils.isEmpty(liveWithUserId) || ObjectUtils.isEmpty(liveWithDeptId)) && !ObjectUtils.isEmpty(liveWithUserPhones);
        if (updateUser) {
            userPhoneSet.addAll(userPhones);
        }
        if (updateLiveWithUser) {
            userPhoneSet.addAll(liveWithUserPhones);
        }
        Map<String, EmployeeContract> employeeContractMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(userPhoneSet)) {
            for (String phoneNum : userPhoneSet) {
                List<EmployeeContract> employeeList = employeeExtService.queryByPhoneAndCompanyId(companyId, phoneNum);
                EmployeeContract employee = ObjectUtils.isEmpty(employeeList) ? null : employeeList.get(0);
                if (employee != null && ObjectUtils.isEmpty(employee.getOrg_id())) {
                    employee = employeeExtService.queryEmployeeInfo(employee.getId(), companyId);
                }
                if (employee != null) {
                    employeeContractMap.put(employee.getPhone_num(), employee);
                }
            }
        }
        if (!ObjectUtils.isEmpty(employeeContractMap) && updateUser) {
            List<String> userEmployeeIds = Lists.newArrayList();
            List<String> userOrgIds = Lists.newArrayList();
            getPassengerUserIdOrgId(userPhones, employeeContractMap, userEmployeeIds, userOrgIds);
            transformDto.setUserId(userEmployeeIds.isEmpty() ? null : String.join(",", userEmployeeIds));
            transformDto.setUserDeptId(userOrgIds.isEmpty() ? null : String.join(",", userOrgIds));
        }
        if (!ObjectUtils.isEmpty(employeeContractMap) && updateLiveWithUser) {
            List<String> userEmployeeIds = Lists.newArrayList();
            List<String> userOrgIds = Lists.newArrayList();
            getPassengerUserIdOrgId(liveWithUserPhones, employeeContractMap, userEmployeeIds, userOrgIds);
            transformDto.setLiveWithUserId(userEmployeeIds.isEmpty() ? null : String.join(",", userEmployeeIds));
            transformDto.setLiveWithDeptId(userOrgIds.isEmpty() ? null : String.join(",", userOrgIds));
        }
    }

    private void getPassengerUserIdOrgId(List<String> userPhones, Map<String, EmployeeContract> employeeContractMap, List<String> userEmployeeIds, List<String> userOrgIds) {
        for (String phone : userPhones) {
            EmployeeContract employee = employeeContractMap.get(phone);
            if (employee == null) {
                continue;
            }
            String userId = employee.getId();
            if (!ObjectUtils.isEmpty(userId)) {
                userEmployeeIds.add(userId);
            }
            String orgId = employee.getOrg_id();
            if (!ObjectUtils.isEmpty(orgId)) {
                userOrgIds.add(orgId);
            }
        }
    }

    private ICompanyBillExtListener getCompanyBillExtListener(String extListener) {
        Class clazz = null;
        try {
            clazz = Class.forName(extListener);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz != null) {
            Object bean = SpringUtils.getBean(clazz);
            if (bean != null && bean instanceof ICompanyBillExtListener) {
                return (ICompanyBillExtListener) bean;
            }
        }
        return null;
    }

    private Map getTransformData(Long etlConfigId, Map<String, Object> data, String companyId) {
        Map transformMap = etlService.transform(etlConfigId, data);
        String employeeId = (String) transformMap.get("employeeId");
        String deptId = (String) transformMap.get("deptId");
        if (!ObjectUtils.isEmpty(employeeId) && ObjectUtils.isEmpty(deptId)) {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(employeeId, companyId);
            if (employeeContract != null) {
                transformMap.put("deptId", employeeContract.getOrg_id());
            }
        }
        return transformMap;
    }

    private FuncBillExtInfoResDTO buildFuncBillExtInfoResDTO(String companyId, FuncBillExtInfoTransformDTO transformDto) {
        FuncBillExtInfoResDTO resDto = new FuncBillExtInfoResDTO();
        //设置第三方人员id
        setThirdUserInfo(companyId, transformDto, resDto);
        //设置第三方部门id
        setThirdDeptInfo(companyId, transformDto, resDto);
        //设置费用归属
        setCostAttribution(companyId, transformDto, resDto);
        //设置审批字段
        setApply(companyId, transformDto, resDto);
        return resDto;
    }

    private void setApply(String companyId, FuncBillExtInfoTransformDTO transformDto, FuncBillExtInfoResDTO resDto) {
        String applyId = transformDto.getApplyId();
        if (!ObjectUtils.isEmpty(applyId)) {
            //加载审批单信息
            SaasApplyCustomFieldRespDTO applyInfo = commonApplyService.getApplyCustomFields(companyId, applyId);
            String thirdId = applyInfo.getThirdId();
            //用车
            if ("3".equals(transformDto.getType())) {
                resDto.setTaxiApprovalID(thirdId);
            }
            //机酒火 国际
            else if (Lists.newArrayList("7", "11", "15", "40").contains(transformDto.getType())) {
                resDto.setTravelApprovalID(thirdId);
            }
            //外卖
            else if ("50".equals(transformDto.getType())) {
                resDto.setTakeawayApprovalID(thirdId);
            }
            //用餐
            else if ("60".equals(transformDto.getType())) {
                resDto.setDinnerApprovalID(thirdId);
            }
            String customFields = applyInfo.getCustomFields();
            resDto.setTravelApprovalFields(customFields);
            Map extMap = resDto.getExtMap();
            if (extMap == null) {
                extMap = Maps.newHashMap();
                resDto.setExtMap(extMap);
            }
            Map customFieldMap = applyInfo.getCustomFieldMap();
            if (!ObjectUtils.isEmpty(customFieldMap)) {
                extMap.putAll(customFieldMap);
            }
        }
        //订单审批单id
        String orderApplyId = transformDto.getOrderApplyId();
        if (!ObjectUtils.isEmpty(orderApplyId)) {
            //加载订单审批单信息
            SaasApplyCustomFieldRespDTO applyInfo = commonApplyService.getApplyCustomFields(companyId, orderApplyId);
            String thirdId = applyInfo.getThirdId();
            resDto.setOrderApprovalID(thirdId);
        }
    }

    private void setCostAttribution(String companyId, FuncBillExtInfoTransformDTO transformDto, FuncBillExtInfoResDTO resDto) {
        List<CostAttributionDTO> costAttributionList = transformDto.getCostAttributionList();
        if (ObjectUtils.isEmpty(costAttributionList)) {
            setSingleCostAttribution(companyId, transformDto, resDto);
        } else {
            setMultiCostAttribution(companyId, resDto, costAttributionList);
        }
    }

    private void setMultiCostAttribution(String companyId, FuncBillExtInfoResDTO resDto, List<CostAttributionDTO> costAttributionList) {
        Map<String, List<CostAttributionDTO>> costAttributionMap = costAttributionList.stream().collect(Collectors.groupingBy(CostAttributionDTO::getCategory));
        costAttributionMap.forEach((category, costAttributions) -> {
            //部门
            boolean dept = "部门".equals(category) || "1".equals(category);
            //项目
            boolean project = "项目".equals(category) || "2".equals(category);
            if (dept || project) {
                List<String> costIdList = costAttributions.stream().map(CostAttributionDTO::getId).collect(Collectors.toList());
                if (ObjectUtils.isEmpty(costIdList)) {
                    return;
                }
                List<CommonIdDTO> idDtoList = commonService.queryIdDTO(companyId, costIdList, 1, dept ? 1 : 2);
                List<String> thirdIdList = ObjectUtils.isEmpty(idDtoList) ? null : idDtoList.stream().filter(id -> !ObjectUtils.isEmpty(id.getThirdId())).map(CommonIdDTO::getThirdId).collect(Collectors.toList());
                if (!ObjectUtils.isEmpty(thirdIdList)) {
                    if (dept) {
                        List<OpenMsgSetup> setupList = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_bill_ext_fixcost_user_dept"));
                        OpenMsgSetup setup = ObjectUtils.isEmpty(setupList) ? null : setupList.get(0);
                        String fixThirdDeptId = setup == null ? null : setup.getStrVal1();
                        String thirdIdListStr = String.join(",", thirdIdList.stream().map(thirdId -> thirdId.equals(fixThirdDeptId) ? resDto.getPassengerDeptId() : thirdId).collect(Collectors.toSet()));
                        resDto.setCostAttributionDeptId(thirdIdListStr);
                        setCostAttributionDeptName(resDto, companyId);
                    } else {
                        String thirdIdListStr = String.join(",", new HashSet<>(thirdIdList));
                        resDto.setCostAttributioncostId(thirdIdListStr);
                        List<FuncProjectDetailDTO> projectDetailList = thirdIdList.stream().map(thirdId -> projectService.queryFuncProjectDetail(companyId, thirdId)).filter(Objects::nonNull).collect(Collectors.toList());
                        resDto.setCostAttributioncostCode(String.join(",", projectDetailList.stream().map(FuncProjectDetailDTO::getCode).collect(Collectors.toList())));
                        resDto.setCostAttributioncostName(String.join(",", projectDetailList.stream().map(FuncProjectDetailDTO::getName).collect(Collectors.toList())));
                        resDto.setCostAttributioncostDesp(String.join(",", projectDetailList.stream().map(FuncProjectDetailDTO::getDescription).collect(Collectors.toList())));
                    }
                } else if (project) {
                    List<FuncProjectDetailDTO> projectDetailList = costIdList.stream().map(id -> projectService.queryFuncProjectDetailById(companyId, id)).filter(Objects::nonNull).collect(Collectors.toList());
                    resDto.setCostAttributioncostCode(String.join(",", projectDetailList.stream().map(FuncProjectDetailDTO::getCode).collect(Collectors.toList())));
                    resDto.setCostAttributioncostName(String.join(",", projectDetailList.stream().map(FuncProjectDetailDTO::getName).collect(Collectors.toList())));
                    resDto.setCostAttributioncostDesp(String.join(",", projectDetailList.stream().map(FuncProjectDetailDTO::getDescription).collect(Collectors.toList())));
                }
            }
        });
    }

    private void setSingleCostAttribution(String companyId, FuncBillExtInfoTransformDTO transformDto, FuncBillExtInfoResDTO resDto) {
        String costAttributionCategory = transformDto.getCostAttributionCategory();
        //部门
        boolean dept = "部门".equals(costAttributionCategory) || "1".equals(costAttributionCategory);
        //项目
        boolean project = "项目".equals(costAttributionCategory) || "2".equals(costAttributionCategory);
        if (dept || project) {
            String costAttributionId = transformDto.getCostAttributionId();
            List<CommonIdDTO> idDtoList = commonService.queryIdDTO(companyId, Lists.newArrayList(costAttributionId), 1, dept ? 1 : 2);
            String thirdId = ObjectUtils.isEmpty(idDtoList) ? null : idDtoList.get(0) == null ? null : idDtoList.get(0).getThirdId();
            if (!ObjectUtils.isEmpty(thirdId)) {
                if (dept) {
                    List<OpenMsgSetup> setupList = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_bill_ext_fixcost_user_dept"));
                    OpenMsgSetup setup = ObjectUtils.isEmpty(setupList) ? null : setupList.get(0);
                    String thirdCostDeptId = thirdId;
                    if (setup != null) {
                        String fixThirdDeptId = setup.getStrVal1();
                        if (fixThirdDeptId != null && fixThirdDeptId.equals(thirdCostDeptId)) {
                            thirdCostDeptId = resDto.getPassengerDeptId();
                        }
                    }
                    resDto.setCostAttributionDeptId(thirdCostDeptId);
                    setCostAttributionDeptName(resDto, companyId);
                } else {
                    resDto.setCostAttributioncostId(thirdId);
                    FuncProjectDetailDTO projectDetailDto = projectService.queryFuncProjectDetail(companyId, thirdId);
                    if (projectDetailDto != null) {
                        resDto.setCostAttributioncostCode(projectDetailDto.getCode());
                        resDto.setCostAttributioncostName(projectDetailDto.getName());
                        resDto.setCostAttributioncostDesp(projectDetailDto.getDescription());
                    }
                }
            } else if (project) {
                FuncProjectDetailDTO projectDetailDto = projectService.queryFuncProjectDetailById(companyId, costAttributionId);
                if (projectDetailDto != null) {
                    resDto.setCostAttributioncostCode(projectDetailDto.getCode());
                    resDto.setCostAttributioncostName(projectDetailDto.getName());
                    resDto.setCostAttributioncostDesp(projectDetailDto.getDescription());
                }
            }
        }
    }

    private void setCostAttributionDeptName(FuncBillExtInfoResDTO resDto, String companyId) {
        String costAttributionDeptId = resDto.getCostAttributionDeptId();
        if (!ObjectUtils.isEmpty(costAttributionDeptId)) {
            List<String> deptIdList = Lists.newArrayList(costAttributionDeptId.split(","));
            CommonInfoReqDTO req = new CommonInfoReqDTO();
            req.setType(IdTypeEnums.THIRD_ID.getKey());
            req.setBusinessType(IdBusinessTypeEnums.ORG.getKey());
            req.setIdList(deptIdList);
            req.setCompanyId(companyId);
            List<CommonInfoResDTO> resList = commonService.queryCommonInfoByType(req);
            if (!ObjectUtils.isEmpty(resList)) {
                Map<String, String> thirdOrgNameMap = resList.stream().collect(Collectors.toMap(CommonInfoResDTO::getThirdId, CommonInfoResDTO::getFullName));
                String deptNames = deptIdList.stream().map(thirdOrgNameMap::get).collect(Collectors.joining(","));
                resDto.setCostAttributionDeptName(deptNames);
            }
        }
    }

    private void setThirdDeptInfo(String companyId, FuncBillExtInfoTransformDTO transformDto, FuncBillExtInfoResDTO resDto) {
        List<String> fbDeptIdList = transformDto.getFbDeptIdList();
        if (!ObjectUtils.isEmpty(fbDeptIdList)) {
            List<OrgUnitResult> orgUnitList = orgUnitService.batchQueryOrgUnitResultList(companyId, fbDeptIdList);
            Map<String, OrgUnitResult> orgUnitMap = orgUnitList.stream().collect(Collectors.toMap(OrgUnitResult::getId, Function.identity()));
            resDto.setBookerDeptId(getThirdDeptId(orgUnitMap, transformDto.getDeptId()));
            resDto.setPassengerDeptId(getThirdDeptId(orgUnitMap, transformDto.getUserDeptId()));
            resDto.setCohabitantDeptId(getThirdDeptId(orgUnitMap, transformDto.getLiveWithDeptId()));
            //设置部门的扩展属性
            setThirdDeptExtInfo(orgUnitMap, transformDto.getDeptId(), 1, resDto);
            setThirdDeptExtInfo(orgUnitMap, transformDto.getUserDeptId(), 2, resDto);
            setThirdDeptExtInfo(orgUnitMap, transformDto.getLiveWithDeptId(), 3, resDto);
        }
    }

    private String getThirdDeptId(Map<String, OrgUnitResult> orgUnitMap, String fbDeptId) {
        List<String> thirdDeptIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(fbDeptId)) {
            Lists.newArrayList(fbDeptId.split(",")).forEach(deptId -> {
                OrgUnitResult orgUnit = orgUnitMap.get(deptId);
                String thirdOrgUnitId = orgUnit == null ? null : orgUnit.getThird_org_id();
                if (!ObjectUtils.isEmpty(thirdOrgUnitId) && !thirdDeptIdList.contains(thirdOrgUnitId)) {
                    thirdDeptIdList.add(thirdOrgUnitId);
                }
            });
        }
        return thirdDeptIdList.isEmpty() ? null : String.join(",", thirdDeptIdList);
    }

    private void setThirdUserInfo(String companyId, FuncBillExtInfoTransformDTO transformDto, FuncBillExtInfoResDTO resDto) {
        List<String> userIdList = transformDto.getFbUserIdList();
        //人员列表
        List<EmployeeContract> employeeList = employeeExtService.batchQueryEmployeeListInfo(userIdList, companyId);
        Map<String, EmployeeContract> employeeMap = employeeList.stream().collect(Collectors.toMap(EmployeeContract::getEmployee_id, Function.identity()));
        //预订人不会存在多个，根据下单人，使用人和同住人进行分组
        resDto.setBookerUserId(getThirdEmployeeId(employeeMap, transformDto.getEmployeeId()));
        //使用人/同住人会存在多个，酒店是同住人
        resDto.setPassengerUserId(getThirdEmployeeId(employeeMap, transformDto.getUserId()));
        resDto.setCohabitantUserId(getThirdEmployeeId(employeeMap, transformDto.getLiveWithUserId()));
        //设置人员扩展属性信息
        setThirdEmployeeExtInfo(employeeMap, transformDto.getEmployeeId(), 1, resDto);
        setThirdEmployeeExtInfo(employeeMap, transformDto.getUserId(), 2, resDto);
        setThirdEmployeeExtInfo(employeeMap, transformDto.getLiveWithUserId(), 3, resDto);
        //预订人不会存在多个，根据下单人，使用人和同住人进行分组
        resDto.setBookerEmployeeNumber(getEmployeeNumber(employeeMap, transformDto.getEmployeeId()));
        //使用人/同住人会存在多个，酒店是同住人
        resDto.setPassengerEmployeeNumber(getEmployeeNumber(employeeMap, transformDto.getUserId()));
        resDto.setCohabitantEmployeeNumber(getEmployeeNumber(employeeMap, transformDto.getLiveWithUserId()));
    }

    private String getEmployeeNumber(Map<String, EmployeeContract> employeeMap, String employeeId) {
        List<String> employeeNumberList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(employeeId)) {
            Lists.newArrayList(employeeId.split(",")).forEach(userId -> {
                EmployeeContract employee = employeeMap.get(userId);
                String employeeNumber = employee == null ? null : employee.getEmployee_number();
                if (!ObjectUtils.isEmpty(employeeNumber)) {
                    employeeNumberList.add(employeeNumber);
                }
            });
        }
        return employeeNumberList.isEmpty() ? null : String.join(",", employeeNumberList);
    }


    private void setThirdDeptExtInfo(Map<String, OrgUnitResult> deptMap, String deptId, int deptType, FuncBillExtInfoResDTO resDto) {
        if (!ObjectUtils.isEmpty(deptId)) {
            //使用人和同住人会存在多个情况，需要用隔离符号进行分割成字符串
            Map<String, Object> extMap = ObjectUtils.isEmpty(resDto.getExtMap()) ? Maps.newHashMap() : resDto.getExtMap();
            Lists.newArrayList(deptId.split(",")).forEach(depId -> {
                OrgUnitResult department = deptMap.get(depId);
                //人员扩展属性，人员中存在三种角色，预订人，使用人，同住人，每个属性会有三个个
                //例如：address属性会分成三个address1,address2,address3
                List<Map<String, Object>> extInfoList = department == null ? null : department.getExpand_list();
                setExtFieldValue(deptType, extMap, extInfoList);
            });
            resDto.setExtMap(extMap);
        }
    }


    private void setThirdEmployeeExtInfo(Map<String, EmployeeContract> employeeMap, String employeeId, int employeeIdType, FuncBillExtInfoResDTO resDto) {
        if (!ObjectUtils.isEmpty(employeeId)) {
            //使用人和同住人会存在多个情况，需要用隔离符号进行分割成字符串
            Map<String, Object> extMap = ObjectUtils.isEmpty(resDto.getExtMap()) ? Maps.newHashMap() : resDto.getExtMap();
            Lists.newArrayList(employeeId.split(",")).forEach(userId -> {
                EmployeeContract employee = employeeMap.get(userId);
                //人员扩展属性，人员中存在三种角色，预订人，使用人，同住人，每个属性会有三个个
                //例如：address属性会分成三个address1,address2,address3
                List<Map<String, Object>> extInfoList = employee == null ? null : employee.getExpand_list();
                setExtFieldValue(employeeIdType, extMap, extInfoList);
            });
            resDto.setExtMap(extMap);
        }
    }

    private void setExtFieldValue(int type, Map<String, Object> extMap, List<Map<String, Object>> list) {
        if (ObjectUtils.isEmpty(list)) {
            return;
        }
        Map<String, Object> extInfoMap = list.get(0);
        Set<String> keyList = extInfoMap.keySet().stream().map(k -> k.substring(0, k.length() - 1) + type).collect(Collectors.toSet());
        keyList.forEach(key -> {
            String extValue = (String) extInfoMap.get(key);
            if (!ObjectUtils.isEmpty(extValue)) {
                String value = (String) extMap.get(key);
                if (ObjectUtils.isEmpty(value)) {
                    extMap.put(key, extValue);
                } else if (!value.contains(extValue)) {
                    extMap.put(key, value + "," + extValue);
                }
            }
        });
    }


    private String getThirdEmployeeId(Map<String, EmployeeContract> employeeMap, String employeeId) {
        List<String> thirdUserIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(employeeId)) {
            Lists.newArrayList(employeeId.split(",")).forEach(userId -> {
                EmployeeContract employee = employeeMap.get(userId);
                String thirdEmployeeId = employee == null ? null : employee.getThird_employee_id();
                if (!ObjectUtils.isEmpty(thirdEmployeeId)) {
                    thirdUserIdList.add(thirdEmployeeId);
                }
            });
        }
        return thirdUserIdList.isEmpty() ? null : String.join(",", thirdUserIdList);
    }

    private void checkReq(CompanyBillExtInfoReqDTO req) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(req);
        if (openApiBindException != null) {
            FuncResultEntity funcResultEntity = FuncExceptionHandler.handlerException(openApiBindException);
            throw new FinhubException(funcResultEntity.getCode(), funcResultEntity.getMsg());
        }
    }
}
