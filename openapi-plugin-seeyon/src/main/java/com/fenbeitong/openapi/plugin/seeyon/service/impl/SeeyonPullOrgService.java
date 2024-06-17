package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonFbOrgEmpService;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcGetOrgUnitRequest;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.OrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportBindOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.util.DateUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ServiceAspect
@Service
@Slf4j
public class SeeyonPullOrgService extends AbstractOrganizationService {

    @Autowired
    private SeeyonDepartmentService seeyonDepartmentService;
    @Autowired
    private SeeyonClientService seeyonClientService;
    @Autowired
    private SeeyonFbOrgEmpService seeyonFbOrgEmpService;
    @Autowired
    private SeeyonMiddlewareService seeyonMiddlewareService;
    @Autowired
    SeeyonOpenMsgSetupDao seeyonOpenMsgSetupDao;
    @Autowired
    UserCenterService userCenterService;


    /**
     * <p>//判断组织机构数据，生成FB推送数据
     *
     * @param seeyonClient       : 企业信息
     * @param accountOrgResponse : 组织机构数据
     * @param compareDateGap     : 时间间隔
     * @return boolean
     */
    public boolean filterOrgData(
            SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse, Long compareDateGap, Integer syncFreq) {
        LocalDate createDate =
                Jsr310DateHelper.getDateTimeOfTimestamp(accountOrgResponse.getCreateTime()).toLocalDate();
        LocalDate updateDate =
                Jsr310DateHelper.getDateTimeOfTimestamp(accountOrgResponse.getUpdateTime()).toLocalDate();
        //比较创建日期和更新日期，如果小于2分钟则代表新增
        long l = DateUtil.compareTimestamp(accountOrgResponse.getCreateTime(), accountOrgResponse.getUpdateTime());
        long syncTag = DateUtil.compareTimestamp(accountOrgResponse.getUpdateTime(), System.currentTimeMillis());
        /*
         * 组织机构创建
         * 1. 创建时间 = 指定时间
         * 2. 状态为有效
         */
        if (Jsr310DateHelper.equalComparedDayGaps(createDate, LocalDate.now(), compareDateGap)
                && SeeyonConstant.ORG_ENABLED_TRUE.equals(accountOrgResponse.isEnabled()) && l < 2 && syncTag < syncFreq) {
            log.info(seeyonClient.getSeeyonOrgName() + "需要新增的部门数据: {}", JsonUtils.toJson(accountOrgResponse));
            SeeyonFbOrgEmp c = SeeyonFbOrgEmp.builder()
                    .companyId(seeyonClient.getOpenapiAppId())
                    .dataType(0)
                    .orgPath(accountOrgResponse.getPath())
                    .sort("c")
                    .build();
            List<SeeyonFbOrgEmp> seeyonFbOrgEmpsByfbOrgEmp = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsByfbOrgEmp(c);
            if (ObjectUtils.isEmpty(seeyonFbOrgEmpsByfbOrgEmp)) {//查询结果返回没有数据时，进行添加操作
                return seeyonFbOrgEmpService.createOrg(seeyonClient, accountOrgResponse);
            }
            return false;
            //更新时间是今天，并且最新更新时间和当前时间小于更新的频率才进行更新，每一小时更新一次，更新时间一定是小于一小时之前的数据，大于一小时的数据说明前一次同步已经更新了
        } else if (Jsr310DateHelper.equalComparedDayGaps(updateDate, LocalDate.now(), compareDateGap) && syncTag < syncFreq) {
            /*
             * 组织机构更新
             * 1. 更新时间 = 指定时间
             * 2. 状态为有效
             */
            if (SeeyonConstant.ORG_ENABLED_TRUE.equals(accountOrgResponse.isEnabled())) {

                SeeyonFbOrgEmp f = SeeyonFbOrgEmp.builder()
                        .companyId(seeyonClient.getOpenapiAppId())
                        .dataType(0)
                        .orgPath(accountOrgResponse.getPath())
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .sort("f")
                        .build();
                List<SeeyonFbOrgEmp> seeyonFbOrgEmpsByfbOrgEmp = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsByfbOrgEmp(f);
                //因数据为全量更新导致每次拉取更新时间均为每五小时更新一次，因此每次更新的部门数量就比较大
                //调用分贝通，查询部门详情
                UcGetOrgUnitRequest openApi = UcGetOrgUnitRequest.builder()
                        .companyId(seeyonClient.getOpenapiAppId())
                        .orgId(String.valueOf(accountOrgResponse.getId()))
                        .operatorId(seeyonClient.getEmployeeIdThird())
                        .type(2)
                        .build();
                Map ucOrgUnit = userCenterService.getUcOrgUnit(openApi);
                if (!ObjectUtils.isEmpty(ucOrgUnit)) {
                    int code = (int) ucOrgUnit.get("code");
                    if (0 == code) {
                        Map dataMap = (Map) ucOrgUnit.get("data");
                        String orgUnitName = (String) dataMap.get("name");
                        String orgUnitParentId = "";
                        List parent_dept_list = (List) dataMap.get("parent_dept_list");
                        if (!ObjectUtils.isEmpty(parent_dept_list)) {
                            Map<String, String> parentMap = (Map) parent_dept_list.get(0);
                            orgUnitParentId = parentMap.get("third_org_id");
                        }
                        //名称不同，父部门ID不同
                        if(accountOrgResponse.getName().equals("测试B")){
                            log.info("测试B部门 {}");
                        }
                        if (!(orgUnitName.equals(accountOrgResponse.getName())) || !(orgUnitParentId.equals(String.valueOf(accountOrgResponse.getSuperior())))) {
                            if (ObjectUtils.isEmpty(seeyonFbOrgEmpsByfbOrgEmp)) {//查询结构返回没有数据时，进行添加操作
                                log.info(seeyonClient.getSeeyonOrgName() + "需要更新的部门数据: {}",
                                    JsonUtils.toJson(accountOrgResponse));
                                return seeyonFbOrgEmpService.updateOrg(seeyonClient, accountOrgResponse);
                            }
                            return false;
                        }
                    }
                }
                return false;
            }
            /*
             * 组织机构删除
             * 1. 更新时间 = 指定时间
             * 2. 状态为无效
             */
            else if (SeeyonConstant.ORG_ENABLED_FALSE.equals(accountOrgResponse.isEnabled())) {
                //TODO 状态变更为无效时，也可以尝试进行禁用操作，看fbt现在是否可以支持更新人员状态
                //TODO 根据部门ID和操作类型查询fbOrgEmp表里是否包含今天的数据，如果有，说明今天已经删除过了，不需要再次新增
                log.info(seeyonClient.getSeeyonOrgName() + "需要删除的部门数据: {}", JsonUtils.toJson(accountOrgResponse));
                SeeyonFbOrgEmp b = SeeyonFbOrgEmp.builder()
                        .companyId(seeyonClient.getOpenapiAppId())
                        .dataType(0)
                        .orgPath(accountOrgResponse.getPath())
                        .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                        .sort("b")
                        .build();
                List<SeeyonFbOrgEmp> seeyonFbOrgEmpsByfbOrgEmp = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsByfbOrgEmp(b);
                if (ObjectUtils.isEmpty(seeyonFbOrgEmpsByfbOrgEmp)) {//查询结构返回没有数据时，进行添加操作
                    return seeyonFbOrgEmpService.delOrg(seeyonClient, accountOrgResponse);
                }
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @param seeyonClient    : 公司信息
     * @param accountOrgResps : 致远人员数据
     * @return java.lang.Boolean
     */
    public Boolean filterDiffOrg(SeeyonClient seeyonClient, List<SeeyonAccountOrgResp> accountOrgResps) throws IOException {
        List<OrgUnitDTO> orgUnitDTOS = queryFbAllOrgUnit(seeyonClient.getOpenapiAppId());
        //先删除子部门后删除父部门，根据部门全名称排序部门，子部门的全部门名称最长，最先删除，
        List<OrgUnitDTO> sortedOrgUnitDTOS = orgUnitDTOS.stream().sorted(Comparator.comparing(OrgUnitDTO::getOrgUnitFullName).reversed()).collect(Collectors.toList());
        List<String> fbOrgUnitIdList = sortedOrgUnitDTOS.stream().map(OrgUnitDTO::getOrgThirdUnitId).collect(Collectors.toList());
        List<String> seeyonOrgUnitIdList = accountOrgResps.stream().map(orgResp -> String.valueOf(orgResp.getId())).collect(Collectors.toList());
        //获取差异的部门数据
        List<String> diffList = fbOrgUnitIdList.stream().filter(num -> (!seeyonOrgUnitIdList.contains(num) && !num.equals(seeyonClient.getSeeyonAccountId())))
                .collect(Collectors.toList());
        boolean result = false;
        List<SeeyonAccountOrgResp> deleteList = Lists.newArrayList();
        diffList.stream().forEach(diffId -> {
            if (StringUtils.isNotBlank(diffId)) {
                SeeyonAccountOrgResp build = SeeyonAccountOrgResp.builder().id(Long.valueOf(diffId)).build();
                deleteList.add(build);
            }
        });
        log.info(seeyonClient.getSeeyonOrgName() + "需要删除的部门数据: {}", deleteList);
        for (SeeyonAccountOrgResp seeyonAccountOrgResp1 : deleteList) {
            result = seeyonFbOrgEmpService.delOrg(seeyonClient, seeyonAccountOrgResp1);
        }

        return result;
    }

    /**
     * <p>//组织机构数据初始化,新增
     *
     * @param seeyonClient       : 公司信息
     * @param accountOrgResponse : 组织机构信息
     * @return boolean
     */
    public boolean initOrgData(SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse) {
        if (SeeyonConstant.ORG_ENABLED_FALSE.equals(accountOrgResponse.isEnabled())) {
            return true;
        }
        return seeyonFbOrgEmpService.createOrg(seeyonClient, accountOrgResponse);
    }


    public Map bindAllDepIdByNameList(String accountId, String companyId, List<SeeyonAccountOrgResp> seeyonAccountOrgs, List excludeList) {
        //根据企业token获取组织架构
        List<SeeyonOrgDepartment> seeyonOrgDepartments = new LinkedList<>();
        for (int i = 0; i < seeyonAccountOrgs.size(); i++) {
            SeeyonAccountOrgResp seeyonAccountOrgResp = seeyonAccountOrgs.get(i);
            SeeyonOrgDepartment seeyonOrgDepartment = new SeeyonOrgDepartment();
            seeyonOrgDepartment.setId(seeyonAccountOrgResp.getId());
            seeyonOrgDepartment.setName(seeyonAccountOrgResp.getName());
            seeyonOrgDepartment.setSuperior(seeyonAccountOrgResp.getSuperior());

            seeyonOrgDepartments.add(seeyonOrgDepartment);
        }
        // 去除例外列表
//        if (!CollectionUtils.isEmpty(excludeList)) {
//            dingtalkDepartments = dingtalkDepartments.stream()
//                    .filter(department -> !excludeList.contains(String.valueOf(department.getId())))
//                    .collect(Collectors.toList());
//        }
        // 按照每个部门的上级部门路径绑定
        List depIdNameList = getDepNameList(seeyonOrgDepartments, accountId);
        Map<String, Object> unExpectedMap = Maps.newHashMap();
        // 根部门ID设置为企业微信企业ID
        for (int i = 0; i < depIdNameList.size(); i++) {
            Map nameParentId = (Map) depIdNameList.get(i);
            Iterator iterator = nameParentId.entrySet().iterator();
            String key = "";
            List nameList = null;
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                key = (String) entry.getKey();
                nameList = (List) entry.getValue();
            }
            nameList.add("理士国际技术有限公司");
            String deptId = key.equals(accountId) ? accountId : key;
            SupportBindOrgUnitReqDTO supportBindOrgUnitReqDTO = new SupportBindOrgUnitReqDTO();
            supportBindOrgUnitReqDTO.setCompanyId(companyId);
            supportBindOrgUnitReqDTO.setOrgId(deptId);
            supportBindOrgUnitReqDTO.setOrgNameList(nameList);
            OpenApiRespDTO openApiRespDTO = null;
            try {
                openApiRespDTO = bindDepartmentForAPI(supportBindOrgUnitReqDTO);
            } catch (Exception e) {
                String msg = openApiRespDTO.getMsg();
                unExpectedMap.put(deptId, msg);
            }
        }
        return unExpectedMap;
    }

    public List<Map<String, Object>> getDepNameList(List<SeeyonOrgDepartment> list, String accoutId) {
        ArrayList depNameList = Lists.newArrayList();
        List<SeeyonOrgDepartment> collect = list.stream().sorted(Comparator.comparing(SeeyonOrgDepartment::getId).reversed()).collect(Collectors.toList());
        //map结构，depId->nameList
        HashMap<String, Object> hashMap = Maps.newLinkedHashMap();
        for (int i = 0; i < collect.size(); i++) {
            SeeyonOrgDepartment seeyonOrgDepartment = collect.get(i);
            Long id = seeyonOrgDepartment.getId();
            String sId = String.valueOf(id);
            Long parentid = seeyonOrgDepartment.getSuperior();
            String sparentid = String.valueOf(parentid);
            String name = seeyonOrgDepartment.getName();
            HashMap<String, Object> hashMap1 = Maps.newHashMap();
            hashMap1.put("parentid", sparentid);
            hashMap1.put("name", name);
            hashMap.put(sId, hashMap1);
        }
        Set<String> strings = hashMap.keySet();
        for (String key : strings) {
            ArrayList<String> list1 = Lists.newArrayList();
            Map depIdNameListMap = getNpNameMap(key, hashMap, list1, accoutId);
            depNameList.add(depIdNameListMap);
        }
        return depNameList;
    }


    public Map getNpNameMap(String depId, Map NPMap, List list, String accountId) {
        HashMap<String, Object> depIdNameMap = Maps.newHashMap();

        Map nameAndParentIDMap = (Map) NPMap.get(depId);
        String parentid = (String) nameAndParentIDMap.get("parentid");
        String name = (String) nameAndParentIDMap.get("name");
        list.add(name);
        depIdNameMap.put(depId, list);
        if (StringUtils.isNotBlank(parentid) && !parentid.equals(accountId)) {//存在父部门，需要排除根部门
            getNpNameMap(parentid, NPMap, list, accountId);
        }
        return depIdNameMap;
    }

}
