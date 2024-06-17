package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenConstant;
import com.fenbeitong.openapi.plugin.beisen.common.dto.*;
import com.fenbeitong.openapi.plugin.beisen.common.entity.BeisenCorp;
import com.fenbeitong.openapi.plugin.beisen.standard.service.IBeisenSyncEmployeeAndDept;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiService;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.employee.dto.CertDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.IEmployeeRankTemplateService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenTemplateConfigConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.rule.EmployeeAuthRankDto;
import com.fenbeitong.usercenter.api.model.enums.employee.EmployeeCert;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: BeisenSyncEmployeeAndDeptV5ServiceImpl<p>
 * <p>Description: 北森拉取部门与人员V5版本<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/9/13 01:03
 */
@ServiceAspect
@Slf4j
@Service
public class BeisenSyncEmployeeAndDeptV5ServiceImpl implements IBeisenSyncEmployeeAndDept {
    @Autowired
    private BeisenApiService beisenApiService;
    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;
    @Autowired
    DepartmentUtilService departmentUtilService;
    @Autowired
    private OpenTemplateConfigDao templateConfigDao;
    @Autowired
    IEmployeeRankTemplateService employeeRankTemplateService;

    @Override
    public List<OpenThirdOrgUnitDTO> getDeptList(BeisenJobParamDTO jobParamDTO, BeisenCorp beisenCorp) {
        // 0 查询配置
        OpenThirdScriptConfig scriptConfig = openThirdScriptConfigDao.getCommonScriptConfig(beisenCorp.getCompanyId(), EtlScriptType.DEPARTMENT_SYNC);
        // 1 构建参数
        BeisenReqBaseDTO deptReqDTO = buildDeptReq(scriptConfig, jobParamDTO);
        // 2 拉取数据
        List<Object> beisenDeptDTOList = beisenApiService.getResultByTimeWindow(deptReqDTO, beisenCorp, BeiSenConstant.BEISEN_DEPT_ORGANIZATION_V5);
        if (CollectionUtils.isBlank(beisenDeptDTOList)) {
            return null;
        }
        List<BeisenDeptV5DTO> deptV5DTOList = JsonUtils.toObj(JsonUtils.toJson(beisenDeptDTOList), new TypeReference<List<BeisenDeptV5DTO>>() {
        });
        // 3 转换数据
        return transferDept(jobParamDTO, beisenCorp, deptV5DTOList, scriptConfig);
    }

    private List<OpenThirdOrgUnitDTO> transferDept(BeisenJobParamDTO jobParamDTO, BeisenCorp beisenCorp, List<BeisenDeptV5DTO> deptV5DTOList, OpenThirdScriptConfig scriptConfig) {
        if (CollectionUtils.isBlank(deptV5DTOList)) {
            return null;
        }
        Map<Integer, BeisenDeptV5DTO> thirdOrgMap = deptV5DTOList.stream().collect(Collectors.toMap(BeisenDeptV5DTO::getOId, Function.identity(), (k1, k2) -> k2));
        List<OpenThirdOrgUnitDTO> departmentList = deptV5DTOList.stream()
            .map(deptV5DTO -> {
                OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                openThirdOrgUnitDTO.setCompanyId(jobParamDTO.getCompanyId());
                openThirdOrgUnitDTO.setThirdOrgUnitFullName(getFullOrgName(beisenCorp, thirdOrgMap, deptV5DTO.getOId(), null));
                openThirdOrgUnitDTO.setThirdOrgUnitName(deptV5DTO.getName());
                openThirdOrgUnitDTO.setThirdOrgUnitId(String.valueOf(deptV5DTO.getOId()));
                //如果是根部门，父级三方id置空
                if (beisenCorp.getCorpId().equals(String.valueOf(deptV5DTO.getOId()))) {
                    openThirdOrgUnitDTO.setThirdOrgUnitParentId(null);
                } else {
                    openThirdOrgUnitDTO.setThirdOrgUnitParentId(String.valueOf(deptV5DTO.getPOIdOrgAdmin()));
                }
                openThirdOrgUnitDTO.setOrgUnitMasterIds(deptV5DTO.getPersonInCharge());
                //后置脚本处理
                if (Optional.ofNullable(scriptConfig).map(OpenThirdScriptConfig::getScript).isPresent()) {
                    return EtlUtils.etlFilter(scriptConfig, new HashMap<String, Object>() {{
                        put("beisenParamConfig", jobParamDTO);
                        put("beisenDeptDTO", deptV5DTO);
                        put("openThirdOrgUnitDTO", openThirdOrgUnitDTO);
                    }});
                }
                return openThirdOrgUnitDTO;
            })
            .collect(Collectors.toList());
        //部门排序
        List<OpenThirdOrgUnitDTO> sortedDepartmentList = new ArrayList<>();
        sortDept(departmentList, Collections.singletonList(beisenCorp.getCorpId()), sortedDepartmentList);
        return sortedDepartmentList;
    }

    private String getFullOrgName(BeisenCorp beisenCorp, Map<Integer, BeisenDeptV5DTO> thirdOrgMap, Integer thirdOrgId, String fullOrgName) {
        if (thirdOrgMap == null || thirdOrgMap.get(thirdOrgId) == null) {
            return null;
        }
        BeisenDeptV5DTO deptV5DTO = thirdOrgMap.get(thirdOrgId);
        if (beisenCorp.getCorpId().equals(String.valueOf(thirdOrgId))) {
            //公司名称以beisen_corp中的company_name为准
            return StringUtils.isBlank(fullOrgName) ? beisenCorp.getCompanyName() : beisenCorp.getCompanyName().concat("/").concat(fullOrgName);
        }
        fullOrgName = StringUtils.isBlank(fullOrgName) ? deptV5DTO.getName() : deptV5DTO.getName().concat("/").concat(fullOrgName);
        thirdOrgId = deptV5DTO.getPOIdOrgAdmin();
        return getFullOrgName(beisenCorp, thirdOrgMap, thirdOrgId, fullOrgName);

    }

    private void sortDept(List<OpenThirdOrgUnitDTO> departmentList, List<String> parentIds, List<OpenThirdOrgUnitDTO> sortedDepartmentList) {
        if (CollectionUtils.isBlank(parentIds)) {
            return;
        }
        List<String> childIds = new ArrayList<>();
        parentIds.forEach(parentId -> {
            List<OpenThirdOrgUnitDTO> childDeptList = departmentList.stream().filter(deptDto -> parentId.equals(deptDto.getThirdOrgUnitParentId())).collect(Collectors.toList());
            if (CollectionUtils.isNotBlank(childDeptList)) {
                sortedDepartmentList.addAll(childDeptList);
                childIds.addAll(childDeptList.stream().map(OpenThirdOrgUnitDTO::getThirdOrgUnitId).collect(Collectors.toList()));
            }
        });
        sortDept(departmentList, childIds, sortedDepartmentList);
    }

    /**
     * 北森请求参数支持通过脚本获取,未配置脚本走默认参数
     *
     * @param scriptConfig 脚本配置
     * @return
     */
    private BeisenReqBaseDTO buildDeptReq(OpenThirdScriptConfig scriptConfig, BeisenJobParamDTO jobParamDTO) {
        if (Optional.ofNullable(scriptConfig).map(OpenThirdScriptConfig::getParamJson).isPresent()) {
            return JsonUtils.toObj(scriptConfig.getParamJson(), BeisenReqBaseDTO.class);
        } else {
            BeisenReqBaseDTO deptReqDTO = new BeisenReqBaseDTO();
            deptReqDTO.setTimeWindowQueryType(1);
            deptReqDTO.setStartTime(StringUtils.isBlank(jobParamDTO.getStartDate()) ? BeiSenConstant.START_DATE : jobParamDTO.getStartDate());
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.FORMAT_DATE_PATTERN_T_1);
            deptReqDTO.setStopTime(StringUtils.isBlank(jobParamDTO.getStartDate()) ? dateFormat.format(new Date()) : jobParamDTO.getEndDate());
            deptReqDTO.setCapacity(300);
            deptReqDTO.setColumns(new String[]{"oId", "name", "pOIdOrgAdmin", "shortName", "status", "createdTime", "modifiedTime", "personInCharge"});
            BeisenReqBaseDTO.ExtQuery extQuery = BeisenReqBaseDTO.ExtQuery.builder().fieldName("Status").queryType(5).values(new String[]{"1"}).build();
            List<BeisenReqBaseDTO.ExtQuery> extQueries = new ArrayList<>();
            extQueries.add(extQuery);
            deptReqDTO.setExtQueries(extQueries);
            return deptReqDTO;
        }
    }

    @Override
    public List<OpenThirdEmployeeDTO> getEmployeeList(BeisenJobParamDTO jobParamDTO, BeisenCorp beisenCorp) {
        // 0 查询配置
        OpenThirdScriptConfig scriptConfig = openThirdScriptConfigDao.getCommonScriptConfig(beisenCorp.getCompanyId(), EtlScriptType.EMPLOYEE_SYNC);
        // 1 构建参数
        BeisenEmployeeV5ReqDTO employeeV5ReqDTO = buildEmployeeReq(scriptConfig, jobParamDTO);
        // 2 拉取数据
        List<Object> beisenEmployeeDTOList = beisenApiService.getResultByTimeWindow(employeeV5ReqDTO, beisenCorp, BeiSenConstant.BEISEN_EMPLOYEE_ORGANIZATION_V5);
        if (CollectionUtils.isBlank(beisenEmployeeDTOList)) {
            return null;
        }
        List<BeisenEmployeeV5DTO> beisenEmployeeV5DTOList = JsonUtils.toObj(JsonUtils.toJson(beisenEmployeeDTOList), new TypeReference<List<BeisenEmployeeV5DTO>>() {
        });
        // 3 转换数据
        return transferEmployee(jobParamDTO, beisenEmployeeV5DTOList, scriptConfig);
    }

    private List<OpenThirdEmployeeDTO> transferEmployee(BeisenJobParamDTO jobParamDTO, List<BeisenEmployeeV5DTO> beisenEmployeeV5DTOList, OpenThirdScriptConfig scriptConfig) {
        if (CollectionUtils.isBlank(beisenEmployeeV5DTOList)) {
            return null;
        }
        final Map<String, String> rankMap = getRankMap(jobParamDTO.getCompanyId());
        return beisenEmployeeV5DTOList.stream()
            .map(employeeV5DTO -> {
                OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                openThirdEmployeeDTO.setCompanyId(jobParamDTO.getCompanyId());
                openThirdEmployeeDTO.setThirdEmployeeId(String.valueOf(employeeV5DTO.getEmployeeInfo().getUserID()));
                openThirdEmployeeDTO.setThirdDepartmentId(String.valueOf(employeeV5DTO.getRecordInfo().getOIdDepartment()));
                openThirdEmployeeDTO.setThirdEmployeeName(employeeV5DTO.getEmployeeInfo().getName());
                openThirdEmployeeDTO.setThirdEmployeeEmail(employeeV5DTO.getEmployeeInfo().getEmail());
                openThirdEmployeeDTO.setThirdEmployeePhone(PhoneCheckUtil.getMobileWithoutCountryCode(employeeV5DTO.getEmployeeInfo().getMobilePhone()));
                openThirdEmployeeDTO.setThirdEmployeeGender(employeeV5DTO.getEmployeeInfo().getGender() == null ? 2 : (employeeV5DTO.getEmployeeInfo().getGender() == 0 ? 1 : 2));
                openThirdEmployeeDTO.setThirdEmployeeBirthday(buildBirthDay(employeeV5DTO));
                openThirdEmployeeDTO.setEmployeeNumber(employeeV5DTO.getRecordInfo().getJobNumber());
                openThirdEmployeeDTO.setCerts(buildCertDto(employeeV5DTO.getEmployeeInfo()));
                if (rankMap != null) {
                    // 根据职级三方id得到分贝通id，并设置到thirdEmployeeRankId（历史遗留问题）
                    openThirdEmployeeDTO.setThirdEmployeeRankId(rankMap.get(employeeV5DTO.getRecordInfo().getOIdJobLevel()));
                }
                //后置脚本处理
                if (Optional.ofNullable(scriptConfig).map(OpenThirdScriptConfig::getScript).isPresent()) {
                    return EtlUtils.etlFilter(scriptConfig, new HashMap<String, Object>() {{
                        put("beisenParamConfig", jobParamDTO);
                        put("beisenEmployeeDTO", employeeV5DTO);
                        put("openThirdEmployeeDTO", openThirdEmployeeDTO);
                    }});
                }
                return openThirdEmployeeDTO;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取生日 yyyy-MM-dd 变为 yyyyMMdd
     */
    private String buildBirthDay(BeisenEmployeeV5DTO employeeV5DTO) {
        Optional<String> birthOptional = Optional.ofNullable(employeeV5DTO).map(BeisenEmployeeV5DTO::getEmployeeInfo).map(BeisenEmployeeV5DTO.EmployeeInfo::getBirthday);
        return birthOptional.map(s -> s.substring(0, s.indexOf("T")).replaceAll("-", "")).orElse(null);
    }


    /**
     * 构建证件信息
     *
     * @param employeeInfo 北森人员信息
     * @return 证件信息
     */
    private List<CertDTO> buildCertDto(BeisenEmployeeV5DTO.EmployeeInfo employeeInfo) {
        if (ObjectUtils.isEmpty(employeeInfo) || StringUtils.isBlank(employeeInfo.getIDNumber())) {
            return null;
        }
        CertDTO certDTO = new CertDTO();
        certDTO.setCertNo(employeeInfo.getIDNumber());
        //北森员工数据中证件类型有可能为null，证件类型为null，support层默认为身份证
        if (StringUtils.isNotBlank(employeeInfo.getIDType())) {
            switch (employeeInfo.getIDType()) {
                case "1":
                case "9":
                case "11":
                case "12":
                case "13":
                    // 北森 1 : 身份证 ,9 : 港澳身份证,11 : 香港永久性居民身份证,12 : 台湾身份证,13 : 澳门特别行政区永久性居民身份证---分贝通 1：身份证
                    certDTO.setCertType(EmployeeCert.IdCard.getKey());
                    break;
                case "2":
                case "10":
                    // 北森 2: 中国护照 10：外国护照 ---分贝通 2：护照
                    certDTO.setCertType(EmployeeCert.Passport.getKey());
                    break;
                case "3":
                    // 北森 3：港澳居民来往内地通行证 --- 分贝通 3：回乡证
                    certDTO.setCertType(EmployeeCert.TwowayPermit.getKey());
                    break;
                case "4":
                    // 北森 4：台湾居民来往大陆通行证 --- 分贝通：4：台胞证
                    certDTO.setCertType(EmployeeCert.TaiwanPassport.getKey());
                    break;
                case "14":
                    // 北森 14:外国人永久居留证 -- 分贝通 9：外国人永久居留证
                    certDTO.setCertType(EmployeeCert.ForeignersPermanentResidencePermit.getKey());
                    break;
                default:
                    certDTO.setCertType(EmployeeCert.Other.getKey());
            }
        }
        return Lists.newArrayList(certDTO);

    }

    private Map<String, String> getRankMap(String companyId) {
        OpenTemplateConfig openTemplateConfig = templateConfigDao.selectByCompanyId(companyId, OpenTemplateConfigConstant.TYPE.RANK, OpenType.BEISEN.getType());
        if (openTemplateConfig != null) {
            List<EmployeeAuthRankDto> rankDtoList = employeeRankTemplateService.getRanks(companyId);
            if (!CollectionUtils.isBlank(rankDtoList)) {
                return rankDtoList.stream().collect(Collectors.toMap(EmployeeAuthRankDto::getThird_rank_id, EmployeeAuthRankDto::getRank_id, (k1, k2) -> k2));
            }
        }
        return null;
    }

    private BeisenEmployeeV5ReqDTO buildEmployeeReq(OpenThirdScriptConfig scriptConfig, BeisenJobParamDTO jobParamDTO) {
        if (Optional.ofNullable(scriptConfig).map(OpenThirdScriptConfig::getParamJson).isPresent()) {
            // 通过脚本 配置查询参数
            return JsonUtils.toObj(scriptConfig.getParamJson(), BeisenEmployeeV5ReqDTO.class);
        } else {
            BeisenEmployeeV5ReqDTO employeeV5ReqDTO = new BeisenEmployeeV5ReqDTO();
            employeeV5ReqDTO.setTimeWindowQueryType(1);
            // 2 试用 3 正式 默认支持这两种
            employeeV5ReqDTO.setEmpStatus(new String[]{"2", "3"});
            // 0 内部员工 1 外部人员 2 实习生
            employeeV5ReqDTO.setEmployType(new String[]{"0", "2"});
            employeeV5ReqDTO.setStartTime(StringUtils.isBlank(jobParamDTO.getStartDate()) ? BeiSenConstant.START_DATE : jobParamDTO.getStartDate());
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.FORMAT_DATE_PATTERN_T_1);
            employeeV5ReqDTO.setStopTime(StringUtils.isBlank(jobParamDTO.getStartDate()) ? dateFormat.format(new Date()) : jobParamDTO.getEndDate());
            employeeV5ReqDTO.setCapacity(300);
            employeeV5ReqDTO.setColumns(new String[]{"userID", "name", "mobilePhone", "oIdDepartment", "iDType", "iDNumber", "gender", "birthday", "oIdJobLevel"});
            return employeeV5ReqDTO;
        }
    }

}
