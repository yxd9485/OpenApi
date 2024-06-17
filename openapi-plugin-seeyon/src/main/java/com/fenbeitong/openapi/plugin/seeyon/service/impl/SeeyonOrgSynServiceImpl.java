package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.exception.ArgumentException;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccessTokenReq;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgListResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonOrgNameReq;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonExtInfo;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOpenMsgSetup;
import com.fenbeitong.openapi.plugin.seeyon.enums.GroupEnum;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonAccountService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonMsgSetupService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonOrgSynService;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.ThirdEmployeePostProcessService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.OpenThirdOrgUnitSortUtil;
import com.fenbeitong.openapi.plugin.support.util.OrgPreProcessUtil;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant.FBT_AUTH_ITEM;
import static com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant.FBT_ID_CARD_ITEM;
import static com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant.REDIS_RETURN_FAILED;
import static com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant.REDIS_RETURN_SUCCESS;

/**
 * 致远同步组织架构
 * @Auther zhang.peng
 * @Date 2021/7/23
 */
@Slf4j
@ServiceAspect
@Service
public class SeeyonOrgSynServiceImpl implements SeeyonOrgSynService {

    @Autowired
    SeeyonClientService seeyonClientService;
    @Autowired
    SeeyonAccessTokenService seeyonAccessTokenService;
    @Autowired
    SeeyonAccountService seeyonAccountService;
    @Autowired
    SeeyonDepartmentService seeyonDepartmentService;
    @Autowired
    SeeyonEmpService seeyonEmpService;
    @Autowired
    SeeyonSyncThirdOrgService seeyonSyncThirdOrgService;
    @Autowired
    SeeyonExtInfoService seeyonExtInfoService;
    @Autowired
    SeeyonEmailService seeyonEmailService;
    @Autowired
    SeeyonMsgSetupService seeyonMsgSetupService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;
    @Autowired
    private ThirdEmployeePostProcessService postProcessService;

    // 集团类型
    private static final String GROUP_TYPE = "0";

    @Override
    @Async
    public void doOrgSyn( String orgName , SeeyonClient seeyonClient, SeeyonOrgNameReq seeyonOrgNameReq, SeeyonOpenMsgSetup seeyonCompanySetup, String seeyonAccountOrgEmpKey) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, seeyonClient.getOpenapiAppId());
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                // 获取tokenHeader
                Map<String, String> tokenHeader = new HashMap<>();
                String token = getTokenInfo(seeyonClient);
                tokenHeader.put(SeeyonConstant.TOKEN_HEADER, token);
                log.info("----------------- do org syn start : -----------------");
                // 兼容集团版和集团下子公司
                List<SeeyonAccountOrgListResp> accountOrgs = buildAccountOrgs(seeyonClient,tokenHeader,orgName);
                List<OpenThirdEmployeeDTO> allEmployeeList = new ArrayList<>();
                List<OpenThirdOrgUnitDTO> allDepartmentList = new ArrayList<>();
                // 先查一下配置表 看是否需要脚本处理数据
                String companyId = seeyonClient.getOpenapiAppId();
                OpenThirdScriptConfig employeeConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.EMPLOYEE_SYNC);
                OpenThirdScriptConfig departmentConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.DEPARTMENT_SYNC);
                log.info("employeeConfig etl info : {}",employeeConfig);
                log.info("departmentConfig etl info : {}",departmentConfig);
                String seeClientAccountId = seeyonClient.getSeeyonAccountId();
                String groupType = seeyonClient.getGroupType();
                List<SeeyonAccountOrgListResp> groupAccountOrgs = accountOrgs.stream().filter(org->org.isGroup()).collect(Collectors.toList());
                // 是否只取分部信息,默认同步全部
                boolean selectPart = false;
                if (CollectionUtils.isNotEmpty(groupAccountOrgs)){
                    selectPart = !groupAccountOrgs.get(0).getOrgAccountId().equals(seeClientAccountId);
                }
                // 处理单位列表
                for (SeeyonAccountOrgListResp seeyonAccountOrgListResp : accountOrgs) {
                    // 是否只取分部
                    if (selectPart){
                        // 如果是非集团版企业,只取 seeyonClient 里配置的致远id的公司
                        if ( GROUP_TYPE.equals(groupType) && !seeClientAccountId.equals(seeyonAccountOrgListResp.getOrgAccountId()) ){
                            continue;
                        }
                    }
                    // 三方部门id取值字段名
                    String thirdUnitIdFieldName = seeyonOrgNameReq.getThirdUnitIdFieldName();
                    List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
                    List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
                    // 单位编号
                    String accountId = seeyonAccountOrgListResp.getOrgAccountId();
                    // 某个单位下所有部门
                    List<SeeyonAccountOrgResp> singleAccountOrgs = seeyonDepartmentService.getOrgInfo(seeyonClient.getOpenapiAppId(), SeeyonAccountParam.builder().orgAccountId(accountId).build(), seeyonClient.getSeeyonSysUri(), tokenHeader);
                    // 根部门 id 和 code 映射
                    Map<Long, String> idCodeMap = "code".equals(thirdUnitIdFieldName) ? singleAccountOrgs.stream().collect(Collectors.toMap(SeeyonAccountOrgResp::getId, SeeyonAccountOrgResp::getCode, (o, n) -> n)) : Maps.newHashMap();
                    idCodeMap.put(Long.valueOf(seeyonClient.getSeeyonAccountId()), "00000001");
                    // 构建根部门
                    OpenThirdOrgUnitDTO rootOrgUnit = buildRootOrgUnit(seeyonClient,seeyonAccountOrgListResp,thirdUnitIdFieldName,idCodeMap);
                    // 如果是单位是集团 , 加入到部门列表中 , 最后统一同步
                    if (seeyonAccountOrgListResp.isGroup()){
                        allDepartmentList.add(rootOrgUnit);
                        continue;
                    }
                    if (selectPart && seeClientAccountId.equals(seeyonAccountOrgListResp.getOrgAccountId())){
                        allDepartmentList.add(rootOrgUnit);
                    }
                    List<SeeyonAccountEmpResp> empInfos = getEmpInfos(seeyonClient,seeyonCompanySetup,accountId,tokenHeader);
                    // 致远部门人员同步标识key
                    setRedisInfo(seeyonCompanySetup,empInfos,seeyonAccountOrgEmpKey);
                    // 过滤正常手机号
                    List<SeeyonAccountEmpResp> normalEmpInfos = empInfos.stream().filter(empInfo -> PhoneCheckUtil.validMomile(empInfo.getTelNumber())).collect(Collectors.toList());
                    // 处理错误数据
                    processErrorData(seeyonClient,empInfos,normalEmpInfos,tokenHeader);
                    AuthDefinition authDefinitionByCompanyId = seeyonSyncThirdOrgService.getAuthDefinitionByCompanyId(seeyonClient.getOpenapiAppId());
                    String appName = authDefinitionByCompanyId.getAppName();
                    // 构建部门信息
                    buildDepartmentList(singleAccountOrgs,appName,seeyonClient,departmentList,thirdUnitIdFieldName,idCodeMap,departmentConfig);
                    SeeyonExtInfo seeyonExtInfo = seeyonExtInfoService.getSeeyonExtInfo(seeyonClient.getOpenapiAppId(), 1, 0);
                    // 构建员工信息
                    buildEmployeeList(normalEmpInfos,seeyonExtInfo,seeyonClient,thirdUnitIdFieldName,employeeList,employeeConfig);
                    // 对无序部门信息进行排序
                    List<OpenThirdOrgUnitDTO> newDepartmentList = OpenThirdOrgUnitSortUtil.sortDep(departmentList,rootOrgUnit);
                    allEmployeeList.addAll(employeeList);
                    allDepartmentList.addAll(newDepartmentList);
                }
                seeyonSyncThirdOrgService.syncThird(OpenType.SEEYON.getType(), seeyonClient.getOpenapiAppId(), allDepartmentList, allEmployeeList);
                log.info("----------------- do org syn end , departList size : {} , employeeList size : {} : -----------------",CollectionUtils.isEmpty(allDepartmentList) ? 0 : allDepartmentList.size(),CollectionUtils.isEmpty(allEmployeeList) ? 0 : allEmployeeList.size());
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁，companyId={}", seeyonClient.getOpenapiAppId());
            throw new ArgumentException("未获取到锁");
        }
    }

    public List<SeeyonAccountOrgListResp> buildAccountOrgs(SeeyonClient seeyonClient, Map<String, String> tokenHeader, String orgName ){
        List<SeeyonAccountOrgListResp> accountOrgs = new ArrayList<>();
        if ( GroupEnum.IS_GROUP.getCode().equals(seeyonClient.getGroupType()) ){
            // 致远集团版的查询单位列表
            log.info("当前 seeyon 为集团版 , 获取集团版单位列表 start , url : {} , token : {} ",seeyonClient.getSeeyonSysUri(),tokenHeader);
            accountOrgs = seeyonAccountService.getOrgAccounts(seeyonClient.getSeeyonSysUri(), tokenHeader);
            log.info("当前 seeyon 为集团版 , 获取集团版单位列表 end , accountOrgs size : {}",CollectionUtils.isEmpty(accountOrgs) ? 0 : accountOrgs.size());
        } else {
            try {
                // 取集团部门
                log.info("当前 seeyon 为非集团版 , 获取集团根集团 start , url : {} , token : {} ",seeyonClient.getSeeyonSysUri(),tokenHeader);
                List<SeeyonAccountOrgListResp> accountGroupOrgs = seeyonAccountService.getOrgAccounts(seeyonClient.getSeeyonSysUri(), tokenHeader);
                log.info("当前 seeyon 为非集团版 , 获取集团根集团 end , accountOrgs size : {}",CollectionUtils.isEmpty(accountGroupOrgs) ? 0 : accountGroupOrgs.size());
                accountGroupOrgs = accountGroupOrgs.stream().filter(org->org.isGroup()).collect(Collectors.toList());
                SeeyonAccountOrgListResp rootOrg = CollectionUtils.isEmpty(accountGroupOrgs) ? new SeeyonAccountOrgListResp() : accountGroupOrgs.get(0);
                // 取集团下子公司根部门
                SeeyonAccountParam build = SeeyonAccountParam.builder().orgName(URLEncoder.encode(orgName, "UTF-8")).build();
                SeeyonAccountResp seeyonAccountResp = seeyonAccountService.getAccountInfoByName(build, seeyonClient.getSeeyonSysUri(), tokenHeader);
                SeeyonAccountOrgListResp seeyonAccountOrgListResp = SeeyonAccountOrgListResp.Builder.build(seeyonAccountResp);
                log.info("seeyon 非集团版根集团信息 : {}", null == rootOrg ? "empty" : rootOrg.toString());
                if ( null != rootOrg ){
                    accountOrgs.add(rootOrg);
                }
                log.info("seeyon 非集团版当前公司根部门信息 : {}", null == seeyonAccountOrgListResp ? "empty" : seeyonAccountOrgListResp.toString());
                if ( null != seeyonAccountOrgListResp ){
                    accountOrgs.add(seeyonAccountOrgListResp);
                }
            } catch (Exception e){
                log.error("根据名称获取致远非集团版组织机构失败 , error :{}",e.getMessage());
            }
        }
        return accountOrgs;
    }

    public void setRedisInfo(SeeyonOpenMsgSetup seeyonCompanySetup, List<SeeyonAccountEmpResp> empInfos, String seeyonAccountOrgEmpKey){
        if (!ObjectUtils.isEmpty(seeyonCompanySetup)) {
            Integer intVal1 = seeyonCompanySetup.getIntVal1();
            if (1 == intVal1) {//总开关开
                Integer intVal2 = seeyonCompanySetup.getIntVal2();
                if(1 == intVal2){//使用Redis存储
                    String strVal1 = seeyonCompanySetup.getStrVal1();
                    if (!ObjectUtils.isEmpty(empInfos)) {
                        redisTemplate.opsForValue().set(seeyonAccountOrgEmpKey, REDIS_RETURN_SUCCESS);
                        redisTemplate.expire(seeyonAccountOrgEmpKey, NumericUtils.obj2int(strVal1), TimeUnit.SECONDS);
                    } else {
                        redisTemplate.opsForValue().set(seeyonAccountOrgEmpKey, REDIS_RETURN_FAILED);
                        redisTemplate.expire(seeyonAccountOrgEmpKey, NumericUtils.obj2int(strVal1), TimeUnit.SECONDS);
                    }
                }
            }
        }
    }

    public void buildEmployeeList(List<SeeyonAccountEmpResp> normalEmpInfos, SeeyonExtInfo seeyonExtInfo, SeeyonClient seeyonClient, String thirdUnitIdFieldName, List<OpenThirdEmployeeDTO> employeeList, OpenThirdScriptConfig employeeConfig){
        if (CollectionUtils.isEmpty(normalEmpInfos)){
            return;
        }
        normalEmpInfos.stream().forEach(emp -> {//循环遍历
            //根据不同的人员权限查询中间表对应权限信息
            //每个人的权限配置字段
            SeeyonExtInfo seeyonExtInfo1 = seeyonExtInfoService.parseSeeyonExtInfo(seeyonExtInfo, emp);
            Integer roleType = ObjectUtils.isEmpty(seeyonExtInfo1) ? null : seeyonExtInfo1.getRoleType();
            Map map = seeyonExtInfoService.parseExtInfo(emp);
            // 身份证号
            String idCard = map == null ? null : (String) map.get(FBT_ID_CARD_ITEM);
            // 权限字段
            String privStr = map == null ? null : (String) map.get(FBT_AUTH_ITEM);
            //会存在异常情况，如果格式不对
            Map<String, Object> extMap = new HashMap<>();
            try {
                extMap = JsonUtils.toObj(emp.getDescription(), Map.class);
            } catch (Exception e) {
                //格式与约定格式不一致时
                log.error("员工描述信息格式不正确 , description : {} , error : {} ",emp.getDescription(),e.getMessage());
            }
            String thirdDepartmentId = "code".equals(thirdUnitIdFieldName) ? privStr == null ? null : privStr.split("\\+")[0] : String.valueOf(emp.getOrgDepartmentId());
            thirdDepartmentId = StringUtils.isBlank(thirdDepartmentId) ? String.valueOf(emp.getOrgDepartmentId()) : thirdDepartmentId;
            OpenThirdEmployeeDTO build = OpenThirdEmployeeDTO.builder()
                    .companyId(seeyonClient.getOpenapiAppId())
                    .thirdEmployeeId(String.valueOf(emp.getId()))
                    .thirdEmployeeName(emp.getName())
                    .thirdDepartmentId(thirdDepartmentId)
                    .extInfo(extMap)
                    .thirdEmployeeGender(Lists.newArrayList(1, 2).contains(emp.getGender()) ? emp.getGender() : 0)
                    .thirdEmployeeIdCard(emp.getIdNum())
                    .status(emp.isEnabled() && emp.getState() == 1 ? 1 : 0)
                    .thirdEmployeePhone(emp.getTelNumber())
                    .thirdEmployeeEmail(emp.getEmailAddress())
                    .thirdEmployeeIdCard(idCard)
                    .build();
            build = postProcessService.process(build,emp,seeyonClient.getOpenapiAppId(),employeeConfig);

            if ( canModifyRoleType(roleType) ) {
                build.setThirdEmployeeRoleTye(StringUtils.obj2str(roleType));
            }
            employeeList.add(build);
        });
    }

    public boolean canModifyRoleType( Integer roleType){
        //如果FBT_PRIV字段值为10000时，不进行设置 roleType 值
        return roleType != null && roleType != 10000;
    }

    public void buildDepartmentList(List<SeeyonAccountOrgResp> accountOrgs, String appName, SeeyonClient seeyonClient, List<OpenThirdOrgUnitDTO> departmentList, String thirdUnitIdFieldName, Map<Long, String> idCodeMap, OpenThirdScriptConfig departmentConfig){
        if (CollectionUtils.isEmpty(accountOrgs)){
            return;
        }
        accountOrgs.stream().forEach(org -> {
                String orgFullName = "";
                if (org.getWholeName().contains(",")) {
                    orgFullName = org.getWholeName().replace(",", "/");
                } else {//不包含,则为一级部门
                    orgFullName = org.getName();
                }
                orgFullName = appName + "/" + orgFullName;
                OpenThirdOrgUnitDTO build = OpenThirdOrgUnitDTO.builder().companyId(seeyonClient.getOpenapiAppId())
                         .thirdOrgUnitId(("code".equals(thirdUnitIdFieldName) && !StringUtils.isBlank(org.getCode()) ) ? org.getCode() : StringUtils.obj2str(org.getId()))
                         .thirdOrgUnitParentId(("code".equals(thirdUnitIdFieldName) && !StringUtils.isBlank(idCodeMap.get(org.getSuperior())) ) ? idCodeMap.get(org.getSuperior()) : String.valueOf(org.getSuperior()))
//                            .thirdOrgUnitId( StringUtils.obj2str(org.getId()) )
//                            .thirdOrgUnitParentId( String.valueOf(org.getSuperior()) )
                        .thirdOrgUnitName(org.getName())
                        .thirdOrgUnitFullName(orgFullName)
                        .build();
                if ( null != departmentConfig ){
                    try {
                        Map jsonObject = JsonUtils.toObj(JsonUtils.toJson(org),Map.class);
                        OrgPreProcessUtil.departmentBeforeSyncFilter(departmentConfig,jsonObject,build);
                    } catch (Exception e){
                        log.error("脚本转换 JSON 异常 : {} ",e.getMessage());
                    }
                }
                departmentList.add(build);
            }
        );
    }

    public String getTokenInfo(SeeyonClient seeyonClient){
        return seeyonAccessTokenService.getAccessToken(
                SeeyonAccessTokenReq.builder()
                        .userName(seeyonClient.getSeeyonUsername())
                        .password(seeyonClient.getSeeyonPassword())
                        .build(),
                seeyonClient.getSeeyonSysUri());
    }

    public List<SeeyonAccountEmpResp> getEmpInfos(SeeyonClient seeyonClient, SeeyonOpenMsgSetup seeyonCompanySetup, String accountId, Map<String, String> tokenHeader){
        List<SeeyonAccountEmpResp> empInfos = new ArrayList<>();
        if (ObjectUtils.isEmpty(seeyonCompanySetup)){
            empInfos = seeyonEmpService.getEmpInfo(seeyonClient.getOpenapiAppId(),
                    SeeyonAccountParam.builder().orgAccountId(accountId).build(),
                    seeyonClient.getSeeyonSysUri(),
                    tokenHeader);
            return empInfos;
        }
        Integer intVal1 = seeyonCompanySetup.getIntVal1();
        if (intVal1 == 1) {//使用开关
            Integer intVal3 = seeyonCompanySetup.getIntVal3();
            if (1 == intVal3) {//默认配置一次拉拉取全量人员数据
                empInfos = seeyonEmpService.getAllEmployee(accountId, seeyonClient.getSeeyonSysUri(),
                        tokenHeader);
            } else {//根据部门ID拉取直属部门人员数据，循环获取人员数据.因为一次拉取全量人员会出现oom，因此进行循环获取
                empInfos = seeyonEmpService.getEmpInfo(seeyonClient.getOpenapiAppId(),
                        SeeyonAccountParam.builder().orgAccountId(accountId).build(),
                        seeyonClient.getSeeyonSysUri(),
                        tokenHeader);
            }
        } else {
            empInfos = seeyonEmpService.getEmpInfo(seeyonClient.getOpenapiAppId(),
                    SeeyonAccountParam.builder().orgAccountId(accountId).build(),
                    seeyonClient.getSeeyonSysUri(),
                    tokenHeader);
        }
        return empInfos;
    }

    public StringBuilder buildEmailInfo(List<SeeyonAccountEmpResp> errorEmpInfos, List<SeeyonAccountEmpResp> list){
        //发送html模板邮件
        StringBuilder stringBuilder = new StringBuilder();
        HashSet<SeeyonAccountEmpResp> errorEmpSet = Sets.newHashSet();
        errorEmpSet.addAll(errorEmpInfos);
        errorEmpSet.stream().forEach(error -> {
            SeeyonAccountEmpResp seeyonEmp = new SeeyonAccountEmpResp();
            String name = error.getName();
            String telNumber = error.getTelNumber();
            Long id = error.getId();
            String orgPostName = error.getOrgPostName();
            Long orgDepartmentId = error.getOrgDepartmentId();
            seeyonEmp.setName(name);
            seeyonEmp.setTelNumber(telNumber);
            seeyonEmp.setId(id);
            seeyonEmp.setOrgPostName(orgPostName);
            seeyonEmp.setOrgDepartmentId(orgDepartmentId);
            list.add(seeyonEmp);
        });
        String s = JsonUtils.toJson(list);
        stringBuilder.append(s);
        return stringBuilder;
    }

    public List<String> getDuplicateElements(List<SeeyonAccountEmpResp> list, boolean flag) {
        return list.stream() //
                .map(e -> { // 获取deptCode或deptAlias的Stream
                    return flag ? e.getTelNumber() : e.getName();
                }).collect(Collectors.toMap(e -> e, e -> 1, (a, b) -> a + b)) // 获得元素出现频率的 Map，键为元素，值为元素出现的次数
                .entrySet().stream() // 所有 entry 对应的 Stream
                .filter(entry -> entry.getValue() > 1) // 过滤出元素出现次数大于 1 的 entry
                .map(entry -> entry.getKey()) // 获得 entry 的键（重复元素）对应的 Stream
                .collect(Collectors.toList()); // 转化为 List
    }

    public List<SeeyonAccountEmpResp> getRepeat(List<SeeyonAccountEmpResp> seeyonAccountEmpResps, String repeatPhone) {
        List<SeeyonAccountEmpResp> collect = seeyonAccountEmpResps.stream().filter(o -> o.getTelNumber().equals(repeatPhone)).collect(Collectors.toList());
        return collect;
    }

    public void processErrorData(SeeyonClient seeyonClient, List<SeeyonAccountEmpResp> empInfos , List<SeeyonAccountEmpResp> normalEmpInfos , Map<String, String> tokenHeader  ){
        List<SeeyonAccountEmpResp> errorEmpInfos = empInfos.stream().filter(empInfo -> !PhoneCheckUtil.validMomile(empInfo.getTelNumber())).collect(Collectors.toList());
        List<String> duplicateElements = getDuplicateElements(normalEmpInfos, true);
        List<SeeyonAccountEmpResp> duplicatePhoneEmpInfos = com.google.common.collect.Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(duplicateElements))
            for (String duplicatePhone : duplicateElements) {
                duplicatePhoneEmpInfos = getRepeat(normalEmpInfos, duplicatePhone);
            }
        errorEmpInfos.addAll(duplicatePhoneEmpInfos);
        if (CollectionUtils.isNotEmpty(errorEmpInfos)){
            log.info("致远有错误人员信息 errorEmpInfos size : {} , 发送邮件", errorEmpInfos.size() );
            sendEmail(seeyonClient,errorEmpInfos,tokenHeader);
        }
    }

    public void sendEmail(SeeyonClient seeyonClient , List<SeeyonAccountEmpResp> errorEmpInfos , Map<String, String> tokenHeader ){
        try {
            Map sendEmailNoticeMap = Maps.newHashMap();
            sendEmailNoticeMap.put("companyId", seeyonClient.getOpenapiAppId());
            sendEmailNoticeMap.put("itemCode", "company_send_self_email_notice");

            //发送错误人员信息到客户公司制定人员,html模板
            List<SeeyonAccountEmpResp> list = new ArrayList<>();
            StringBuilder stringBuilder = buildEmailInfo(errorEmpInfos,list);
            seeyonEmailService.sendHtmlEmail(seeyonClient.getSeeyonOrgName(), seeyonClient.getSeeyonSysUri(), tokenHeader, sendEmailNoticeMap, list);
        } catch (Exception e) {
            log.info("发送邮件失败 : {}",e.getMessage());
        }
    }

    public OpenThirdOrgUnitDTO buildRootOrgUnit(SeeyonClient seeyonClient , SeeyonAccountOrgListResp org ,String thirdUnitIdFieldName ,Map<Long, String> idCodeMap ){

        return OpenThirdOrgUnitDTO.builder().companyId(seeyonClient.getOpenapiAppId())
                .thirdOrgUnitId(("code".equals(thirdUnitIdFieldName) && !StringUtils.isBlank(idCodeMap.get(Long.valueOf(org.getId()))) ) ? idCodeMap.get(Long.valueOf(org.getId())) : StringUtils.obj2str(org.getId()))
                .thirdOrgUnitParentId(String.valueOf(org.getSuperior()))
                .thirdOrgUnitName(org.getShortName())
                .thirdOrgUnitFullName(org.getName())
                .build();
    }
}
