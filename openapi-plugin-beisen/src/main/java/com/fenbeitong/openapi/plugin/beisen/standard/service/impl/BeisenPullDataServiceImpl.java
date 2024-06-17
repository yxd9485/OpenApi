package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.beisen.common.constant.BeiSenConstant;
import com.fenbeitong.openapi.plugin.beisen.common.dto.*;
import com.fenbeitong.openapi.plugin.beisen.standard.listener.DefaultOrgListener;
import com.fenbeitong.openapi.plugin.beisen.standard.listener.OrgListener;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenPullDataService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.common.dao.OpenAuthorityDeployDao;
import com.fenbeitong.openapi.plugin.support.common.entity.OpenAuthorityDeploy;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.dto.AddAuthRankReqDTO;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCustonmConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizeConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.init.service.FullDataSynchronizer;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.core.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 北森数据拉取的服务
 *
 * @author xiaowei
 * @data 2020/06/16
 */
@ServiceAspect
@Service
@Slf4j
public class BeisenPullDataServiceImpl implements BeisenPullDataService {
    @Autowired
    private BeisenApiService beisenApiService;
    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    private UcCompanyServiceImpl companyService;
    @Autowired
    private OpenAuthorityDeployDao openAuthorityDeployDao;
    @Autowired
    private IEtlService etlService;
    @Autowired
    OpenCustomizeConfigDao openCustomizeConfigDao;
    @Resource(name = "employeeRankTemplateFullDataSynchronizer")
    private FullDataSynchronizer rankSynchronizer;


    private static final Long AUTH_DEPLOY_CONFIG = 2320l;


    @Override
    @Async
    public void pullAllData(BeisenParamConfig beisenParamConfig) {
        //查询全量的部门信息
        List<BeisenOrgListDTO.OrgDto> orgDtoList = getBeiSenOrgList(beisenParamConfig);
        log.info("get pullAll beisen orgList data size: " + JsonUtils.toJson(orgDtoList));
        List<BeisenOrgListDTO.OrgDto> normalOrgDtoList = orgDtoList.stream().filter(orgDto -> orgDto.getStatus() == 1 && !orgDto.isStdIsDeleted()).collect(Collectors.toList());
        //查询全量的员工信息
        List<BeisenEmployeeListDTO.EmployeeDto> beisenEmployeeList = beisenApiService.getEmployeeListData(beisenParamConfig);
        log.info("get pullAll beisen data" + JsonUtils.toJson(beisenEmployeeList));
        List<BeisenEmployeeListDTO.EmployeeDto> normalEmployeeList = beisenEmployeeList.stream().filter(employeeDto -> (employeeDto.getServiceInfos().get(0).getEmployeeStatus() != 6 && employeeDto.getServiceInfos().get(0).getEmployeeStatus() != 8 && employeeDto.getServiceInfos().get(0).getEmployeeStatus() != 9)).collect(Collectors.toList());
        //查询员工的任职数据
        Map<String, BeisenEmployeeJobListDTO> beisenEmployeeJobListDTOMap = getBeisenEmployeeJobList(normalEmployeeList, beisenParamConfig);
        //查询员工的职级数据
        Map<String, BeisenEmployeeJobLevelListDTO> beisenEmployeeJobLevelListDTOMap = getBeisenEmployeeJobLevelList("JobLevel", beisenParamConfig);
        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = buildOpenThirdOrgUnitList(beisenParamConfig.getCompanyId(), normalOrgDtoList);
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = buildOpenThirdEmployeeList(beisenParamConfig.getCompanyId(), normalEmployeeList, beisenEmployeeJobListDTOMap, beisenEmployeeJobLevelListDTOMap);
        //人员数据后置监听处理
        OpenCustomizeConfig openCustomizeConfig = openCustomizeConfigDao.getOpenCustomizeConfig(beisenParamConfig.getCompanyId(), OpenCustonmConstant.open_customize_config_type.EMP_ALL);
        if (!ObjectUtils.isEmpty(openCustomizeConfig)) {
            OrgListener orgListener = getProjectListener(openCustomizeConfig);
            orgListener.filterOpenThirdOrgUnitDtoBefore(employeeList, beisenParamConfig.getCompanyId(), beisenParamConfig);
        }
        //同步
        if (departmentList.size() < 3 || employeeList.size() < 10) {
            if (beisenParamConfig.getForceDeleteFlag() != null && beisenParamConfig.getForceDeleteFlag()) {
                openSyncThirdOrgService.syncThird(OpenType.BEISEN.getType(), beisenParamConfig.getCompanyId(), departmentList, employeeList);
            } else {
                log.info("find data is not correct, not go,  param: {}", JsonUtils.toJson(beisenParamConfig));
            }
        } else {
            openSyncThirdOrgService.syncThird(OpenType.BEISEN.getType(), beisenParamConfig.getCompanyId(), departmentList, employeeList);
        }
    }


    @Override
    @Deprecated
    public boolean pullIncrementalData(BeisenParamConfig beisenParamConfig) {
        //查询当前时范围内的部门数据
        List<BeisenOrgListDTO.OrgDto> orgDtoList = getBeiSenOrgList(beisenParamConfig);
        List<BeisenOrgListDTO.OrgDto> deleteOrgDtoList = orgDtoList.stream().filter(orgDto -> DateUtils.toDate(orgDto.getModifiedTime(), DateUtils.FORMAT_DATE_PATTERN_T).after(DateUtils.toDate((DateUtils.beforeHourToNowDate(beisenParamConfig.getHour())))) && DateUtils.toDate(orgDto.getModifiedTime(), DateUtils.FORMAT_DATE_PATTERN_T).before(DateUtils.toDate((DateUtils.toSimpleStr(new Date(), false)))) && orgDto.getStatus() == 0).collect(Collectors.toList());
        List<BeisenOrgListDTO.OrgDto> normalOrgDtoList = orgDtoList.stream().filter(orgDto -> DateUtils.toDate(orgDto.getModifiedTime(), DateUtils.FORMAT_DATE_PATTERN_T).after(DateUtils.toDate((DateUtils.beforeHourToNowDate(beisenParamConfig.getHour())))) && DateUtils.toDate(orgDto.getModifiedTime(), DateUtils.FORMAT_DATE_PATTERN_T).before(DateUtils.toDate((DateUtils.toSimpleStr(new Date(), false)))) && orgDto.getStatus() == 1).collect(Collectors.toList());
        log.info("get pullAdd beisen delete orgList data: " + JsonUtils.toJson(deleteOrgDtoList) + "add normal orgList data: " + JsonUtils.toJson(normalOrgDtoList));
        //查询当前时范围内的员工数据
        List<BeisenEmployeeListDTO.EmployeeDto> beisenEmployeeList = beisenApiService.getEmployeeListData(beisenParamConfig);
        List<BeisenEmployeeListDTO.EmployeeDto> normalEmployeeList = beisenEmployeeList.stream().filter(employeeDto -> (employeeDto.getServiceInfos().get(0).getEmployeeStatus() != 6 && employeeDto.getServiceInfos().get(0).getEmployeeStatus() != 8 && employeeDto.getServiceInfos().get(0).getEmployeeStatus() != 9)).collect(Collectors.toList());
        List<BeisenEmployeeListDTO.EmployeeDto> deleteEmployeeList = beisenEmployeeList.stream().filter(employeeDto -> (employeeDto.getServiceInfos().get(0).getEmployeeStatus() == 6 || employeeDto.getServiceInfos().get(0).getEmployeeStatus() == 8 || employeeDto.getServiceInfos().get(0).getEmployeeStatus() == 9)).collect(Collectors.toList());
        log.info("get pullAdd beisen employee total data: " + JsonUtils.toJson(beisenEmployeeList) + "add delete employeeList: " + JsonUtils.toJson(deleteEmployeeList) + "add normal empolyeeList: " + JsonUtils.toJson(normalEmployeeList));
        //查询员工的任职数据
        Map<String, BeisenEmployeeJobListDTO> beisenEmployeeJobListDTOMap = getBeisenEmployeeJobList(normalEmployeeList, beisenParamConfig);
        //查询员工的职级数据
        Map<String, BeisenEmployeeJobLevelListDTO> beisenEmployeeJobLevelListDTOMap = getBeisenEmployeeJobLevelList("JobLevel", beisenParamConfig);
        log.info("get pullAdd beisen employee job data: " + JsonUtils.toJson(beisenEmployeeJobListDTOMap));
        //转换部门
        List<OpenThirdOrgUnitDTO> departmentList = buildOpenThirdOrgUnitList(beisenParamConfig.getCompanyId(), normalOrgDtoList);
        log.info("get pullAdd beisen org convert data: " + JsonUtils.toJson(departmentList));
        //转换人员
        List<OpenThirdEmployeeDTO> employeeList = buildOpenThirdEmployeeList(beisenParamConfig.getCompanyId(), normalEmployeeList, beisenEmployeeJobListDTOMap, beisenEmployeeJobLevelListDTOMap);
        log.info("get pullAdd beisen employee convert data: " + JsonUtils.toJson(employeeList));
        //同步
        //先删除员工数据再删除组织数据
        if (deleteEmployeeList.size() > 0) {
            List<OpenThirdEmployeeDTO> employees = new ArrayList<>();
            deleteEmployeeList.forEach(employeeDto -> {
                List<BeisenEmployeeListDTO.EmployeeServiceInfos> serviceInfos = employeeDto.getServiceInfos();
                if (serviceInfos.size() > 1) {
                    serviceInfos = serviceInfos.stream().sorted((t1, t2) -> Long.compare(DateUtils.toDate(t2.getCreateTime(), DateUtils.FORMAT_DATE_PATTERN_T).getTime(), DateUtils.toDate(t1.getCreateTime(), DateUtils.FORMAT_DATE_PATTERN_T).getTime())).collect(Collectors.toList());
                }
                employees.add(new OpenThirdEmployeeDTO().builder()
                        .companyId(beisenParamConfig.getCompanyId())
                        .status(0)
                        .thirdDepartmentId(serviceInfos.get(0).getDepartmentId())
                        .thirdEmployeeGender(employeeDto.getBasicInfos().getGender())
                        .thirdEmployeeId(employeeDto.basicInfos.getUserId())
                        .thirdEmployeeIdCard(employeeDto.getBasicInfos().getIdNumber())
                        .thirdEmployeePhone(employeeDto.getBasicInfos().getMobilePhone())
                        .thirdEmployeeName(employeeDto.getBasicInfos().getName())
                        .thirdEmployeeEmail(employeeDto.getBasicInfos().getEmail())
                        .build());
            });
            openSyncThirdOrgService.deleteEmployee(OpenType.BEISEN.getType(), beisenParamConfig.getCompanyId(), employees);
        }
        if (deleteOrgDtoList.size() > 0) {
            List<OpenThirdOrgUnitDTO> departments = new ArrayList<>();
            deleteOrgDtoList.stream().forEach(orgDto -> departments.add(new OpenThirdOrgUnitDTO().builder()
                    .thirdOrgUnitName(orgDto.getShortName())
                    .companyId(beisenParamConfig.getCompanyId())
                    .thirdOrgUnitFullName(orgDto.getName())
                    .thirdOrgUnitId(orgDto.getOId())
                    .thirdOrgUnitParentId(orgDto.getPoIdOrgAdmin())
                    .build()));
            openSyncThirdOrgService.deleteDepartment(OpenType.BEISEN.getType(), beisenParamConfig.getCompanyId(), departments);
        }
        openSyncThirdOrgService.syncThird(OpenType.BEISEN.getType(), beisenParamConfig.getCompanyId(), departmentList, employeeList, true);
        return true;
    }

    @Override
    @Async
    public void syncAllRank(BeisenRankParamConfig beisenRankParamConfig) {
        BeisenParamConfig beisenParamConfig = new BeisenParamConfig();
        BeanUtils.copyProperties(beisenRankParamConfig,beisenParamConfig);
        //1.获取北森职级信息
        List<BeisenRankDTO.RankDto> allRank = beisenApiService.getAllRank(beisenParamConfig);
        if (CollectionUtils.isBlank(allRank)) {
            log.info("【biesen】 syncAllRank, 查询三方项目数据为空,companyId:{}",beisenParamConfig.getCompanyId());
            return;
        }
        log.info("北森全部职级信息,companyId:{},allRank:{}",beisenParamConfig.getCompanyId(),JsonUtils.toJson(allRank));
        //2.字段转换
        List<AddAuthRankReqDTO> optRankDTOList = Lists.newArrayList();
        for (BeisenRankDTO.RankDto reqDTO : allRank) {
            AddAuthRankReqDTO optRankDTO = new AddAuthRankReqDTO();
            optRankDTO.setThirdRankId(reqDTO.getOId());
            optRankDTO.setRankName(reqDTO.getName());
            optRankDTO.setType(BeiSenConstant.RANK_TYPE);
            optRankDTOList.add(optRankDTO);
        }
        //根据三方职级id去重
        List<AddAuthRankReqDTO> distinctList = optRankDTOList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getThirdRankId()))), ArrayList::new));
        log.info("【beisen】 syncAllRank, companyId:{},参数optRankDTOList:{}", beisenParamConfig.getCompanyId(),JsonUtils.toJson(distinctList));
        //3.同步
        rankSynchronizer.sync(OpenType.BEISEN, beisenParamConfig.getCompanyId(), distinctList);
    }


    //查询当前时范围内的部门数据
    private List<BeisenOrgListDTO.OrgDto> getBeiSenOrgList(BeisenParamConfig beisenParamConfig) {
        List<BeisenOrgListDTO.OrgDto> orgListData = beisenApiService.getOrgListData(beisenParamConfig);
        List<BeisenOrgListDTO.OrgDto> normalOrgDtoList = orgListData.stream().filter(orgDto -> orgDto.getStatus() == 1 && !orgDto.isStdIsDeleted()).collect(Collectors.toList());
        //根据三方职级id去重
        List<BeisenOrgListDTO.OrgDto> distinctList = normalOrgDtoList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getOId()))), ArrayList::new));
        CompanyNewDto companyNewDto = companyService.getCompanyService().queryCompanyNewByCompanyId(beisenParamConfig.getCompanyId());
        BeisenOrgListDTO orgListDTO = new BeisenOrgListDTO();
        orgListDTO.setData(distinctList);
        if (companyNewDto != null) {
            return orgListDTO.getDepartmentList(companyNewDto.getCompanyName(), beisenParamConfig.getParentId());
        } else {
            return new ArrayList<BeisenOrgListDTO.OrgDto>();
        }

    }

    //查询员工的任职数据
    private Map<String, BeisenEmployeeJobListDTO> getBeisenEmployeeJobList(List<BeisenEmployeeListDTO.EmployeeDto> beisenEmployeeList, BeisenParamConfig beisenParamConfig) {
        int[] ids = beisenEmployeeList.stream().map(employeeDto -> NumericUtils.obj2int(employeeDto.getBasicInfos().getUserId())).collect(Collectors.toList()).stream().mapToInt(Integer::valueOf).toArray();
        List<BeisenEmployeeJobListDTO> employeeJobData = beisenApiService.getEmployeeJobData(beisenParamConfig, ids);
        return employeeJobData.stream().collect(Collectors.toMap(BeisenEmployeeJobListDTO::getUserId, Function.identity(), (o, n) -> {
            if (DateUtils.toDate(o.getCreatedTime(), DateUtils.FORMAT_DATE_PATTERN_T).after(DateUtils.toDate(n.getCreatedTime(), DateUtils.FORMAT_DATE_PATTERN_T))) {
                return o;
            } else {
                return n;
            }
        }));
    }

    //查询的职级信息数据
    private Map<String, BeisenEmployeeJobLevelListDTO> getBeisenEmployeeJobLevelList(String ObjectName, BeisenParamConfig beisenParamConfig) {
        List<BeisenEmployeeJobLevelListDTO> employeeJobData = beisenApiService.getEmployeeJobLevelData(beisenParamConfig, ObjectName);
        return employeeJobData.stream().collect(Collectors.toMap(BeisenEmployeeJobLevelListDTO::getJobLevelId, d -> d));
    }

    //转换部门数据
    private List<OpenThirdOrgUnitDTO> buildOpenThirdOrgUnitList(String companyId, List<BeisenOrgListDTO.OrgDto> orgDtoList) {
        List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
        for (BeisenOrgListDTO.OrgDto orgDto : orgDtoList) {
            OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
            openThirdOrgUnitDTO.setCompanyId(companyId);
            openThirdOrgUnitDTO.setThirdOrgUnitFullName(orgDto.getName());
            openThirdOrgUnitDTO.setThirdOrgUnitName(orgDto.getShortName());
            openThirdOrgUnitDTO.setThirdOrgUnitParentId(orgDto.getPoIdOrgAdmin());
            openThirdOrgUnitDTO.setThirdOrgUnitId(orgDto.getOId());
            departmentList.add(openThirdOrgUnitDTO);
        }
        return departmentList;
    }

    //转换员工数据
    private List<OpenThirdEmployeeDTO> buildOpenThirdEmployeeList(String companyId, List<BeisenEmployeeListDTO.EmployeeDto> beisenEmployeeList, Map<String, BeisenEmployeeJobListDTO> beisenEmployeeJobListDTOMap, Map<String, BeisenEmployeeJobLevelListDTO> beisenEmployeeJobLevelListDTOMap) {
        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("companyId", companyId);
        paramMap.put("srcColumn", "role_type");
        paramMap.put("state", "0");
        List<OpenAuthorityDeploy> openAuthorityDeployList = openAuthorityDeployDao.listOpenAuthorityDeploy(paramMap);
        Map<String, OpenAuthorityDeploy> openAuthorityDeployMap = openAuthorityDeployList.stream().collect(Collectors.toMap(OpenAuthorityDeploy::getSrcValue, Function.identity(), (o, n) -> n));
        for (BeisenEmployeeListDTO.EmployeeDto employeeDto : beisenEmployeeList) {
            List<BeisenEmployeeListDTO.EmployeeServiceInfos> serviceInfos = employeeDto.getServiceInfos();
            if (serviceInfos.size() > 1) {
                serviceInfos = serviceInfos.stream().sorted((t1, t2) -> Long.compare(DateUtils.toDate(t2.getCreateTime(), DateUtils.FORMAT_DATE_PATTERN_T).getTime(), DateUtils.toDate(t1.getCreateTime(), DateUtils.FORMAT_DATE_PATTERN_T).getTime())).collect(Collectors.toList());
            }
            OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
            openThirdEmployeeDTO.setCompanyId(companyId);
            openThirdEmployeeDTO.setThirdDepartmentId(serviceInfos.get(0).getDepartmentId());
            openThirdEmployeeDTO.setThirdEmployeeId(employeeDto.getBasicInfos().getUserId());
            openThirdEmployeeDTO.setThirdEmployeeName(employeeDto.getBasicInfos().getName());
            openThirdEmployeeDTO.setThirdEmployeePhone(employeeDto.getBasicInfos().getMobilePhone());
            openThirdEmployeeDTO.setThirdEmployeeEmail(employeeDto.getBasicInfos().getEmail());
            openThirdEmployeeDTO.setThirdEmployeeGender(employeeDto.getBasicInfos().getGender() == null ? 2 : (employeeDto.getBasicInfos().getGender() == 0 ? 1 : 2));
            // 1=已激活，2=已禁用，4=未激活，5=退出企业。
            openThirdEmployeeDTO.setStatus(1);
            if (!ObjectUtils.isEmpty(beisenEmployeeJobListDTOMap ) && !ObjectUtils.isEmpty(beisenEmployeeJobLevelListDTOMap)) {
                BeisenEmployeeJobListDTO beisenEmployeeJobListDTO = beisenEmployeeJobListDTOMap.get(employeeDto.basicInfos.getUserId());
                BeisenEmployeeJobLevelListDTO beisenEmployeeJobLevelListDTO = beisenEmployeeJobLevelListDTOMap.get(beisenEmployeeJobListDTO.getOldJobLevel());
                log.info("convert employeeId : {}  jobLevelId: {}  jobLevelName: {} ", employeeDto.basicInfos.getUserId(), beisenEmployeeJobListDTO.getOldJobLevel(), beisenEmployeeJobLevelListDTO != null ? beisenEmployeeJobLevelListDTO.getName() : null);
                if (!ObjectUtils.isEmpty(openAuthorityDeployMap) && !ObjectUtils.isEmpty(beisenEmployeeJobLevelListDTO) && !ObjectUtils.isEmpty(openAuthorityDeployMap.get(beisenEmployeeJobLevelListDTO.getName()))) {
                    openThirdEmployeeDTO.setThirdEmployeeRoleTye(StringUtils.obj2str(openAuthorityDeployMap.get(beisenEmployeeJobLevelListDTO.getName()).getRoleType()));
                }
            }
            //新身份证号
            openThirdEmployeeDTO.setThirdEmployeeIdCard(employeeDto.getBasicInfos().getIdNumber());

            employeeList.add(openThirdEmployeeDTO);
        }
        return employeeList;
    }


    /**
     * 反射获取监听类
     */
    public OrgListener getProjectListener(OpenCustomizeConfig openOrgConfig) {
        String className = openOrgConfig.getListenerClass();
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof OrgListener) {
                    return ((OrgListener) bean);
                }
            }
        }
        return SpringUtils.getBean(DefaultOrgListener.class);
    }

}
