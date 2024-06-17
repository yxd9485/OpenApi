package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.org;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportCreateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportDeleteOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportUpdateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaCallbackTagConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaPullOrgConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dao.YunzhijiaAddressListDao;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.*;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaOrgUnit;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaCorpAppService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaOrgService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.middleware.YunzhijiaMiddleWareService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.task.YunzhijiaTaskService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.fenbeitong.openapi.sdk.webservice.organization.FbtOrganizationService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaOrgServiceImpl extends AbstractOrganizationService implements IYunzhijiaOrgService {

    @Autowired
    YunzhijiaRemoteOrgService yunzhijiaRemoteOrgService;
    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;
    @Autowired
    IYunzhijiaCorpAppService yunzhijiaCorpAppService;

    @Autowired
    YunzhijiaMiddleWareService yunzhijiaMiddleWareService;
    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    private FbtOrganizationService fbtOrganizationService;
    @Autowired
    YunzhijiaTaskService yunzhijiaTaskService;
    @Autowired
    YunzhijiaAddressListDao yunzhijiaAddressListDao;

    @Override
    public YunzhijiaResponse<List<YunzhijiaOrgDTO>> getYunzhijiaOrgDetail(YunzhijiaOrgReqDTO yunzhijiaOrgReqDTO) {
        //1.根据企业ID查询通讯录token
        YunzhijiaAddressList yunzhijiaToken = yunzhijiaTokenService.getYunzhijiaToken(yunzhijiaOrgReqDTO.getEid());
        if (ObjectUtils.isEmpty(yunzhijiaToken)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL)));
        }
        //2.获取access_token对象构建
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .eid(yunzhijiaOrgReqDTO.getEid())
                .secret(yunzhijiaToken.getCorpSecret())
                .scope(YunzhijiaResourceLevelConstant.RES_GROUP_SECRET)
                .timestamp(System.currentTimeMillis())
                .build();
        YunzhijiaResponse<List<YunzhijiaOrgDTO>> yunzhijiaRemoteOrgDetail = yunzhijiaRemoteOrgService.getYunzhijiaRemoteOrgDetail(build, yunzhijiaOrgReqDTO);
        return yunzhijiaRemoteOrgDetail;
    }


    /**
     * 获取云之家当前部门基本信息或部门负责人,该接口为app级别
     *
     * @return
     */
    public YunzhijiaResponse<YunzhijiaOrgInChargeDTO> getYunzhijiaRemoteOrgBaseOrLeaderDetail(YunzhijiaOrgReqDTO yunzhijiaOrgReqDTO) {
        //1.根据企业ID查询通讯录token
        YunzhijiaAddressList yunzhijiaToken = yunzhijiaTokenService.getYunzhijiaToken(yunzhijiaOrgReqDTO.getEid());
        if (ObjectUtils.isEmpty(yunzhijiaToken)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL)));
        }
        //2.获取云之家app_id，云之家新建微应用
        PluginCorpAppDefinition byCorpId = yunzhijiaCorpAppService.getByCorpId(yunzhijiaOrgReqDTO.getEid());
        //3.获取access_token对象构建
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .secret(byCorpId.getThirdAppKey())
                .scope(YunzhijiaResourceLevelConstant.APP)
                .eid(yunzhijiaOrgReqDTO.getEid())
                .timestamp(System.currentTimeMillis())
                .appId(String.valueOf(byCorpId.getThirdAgentId()))
                .build();
        YunzhijiaResponse<YunzhijiaOrgInChargeDTO> yunzhijiaRemoteOrgBaseOrLeaderDetail = yunzhijiaRemoteOrgService.getYunzhijiaRemoteOrgBaseOrLeaderDetail(build, yunzhijiaOrgReqDTO.getArray().get(0));
        return yunzhijiaRemoteOrgBaseOrLeaderDetail;
    }


    /**
     * 同步云之家部门数据到分贝通
     *
     * @param yunzhijiaToken
     * @param corpId
     * @param deptId
     * @return
     */
    public Map<String, Object> syncYunzhijiaOrgUnit(String yunzhijiaToken, String corpId, String deptId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        //分贝公司id
        String companyId = corpDefinition.getAppId();
        //获取企业云之家全量部门
        List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> yunzhijiaDepartmentList = getYunzhijiaDepartmentList(yunzhijiaToken, corpId);
        //获取分贝通全量部门
        List<YunzhijiaOrgUnit> fbOrgUnitList = yunzhijiaMiddleWareService.selectYunzhijiaOrgUnitByParam(YunzhijiaOrgUnit.builder().corpId(corpId).build());
        //对部门进行分组 增删改
        return groupDepartment(yunzhijiaDepartmentList, fbOrgUnitList, corpId, companyId);
    }

    @Override
    public void addOrgUnit(String companyId, List<YunzhijiaOrgUnitAdd> addOrgList) {
        if (!ObjectUtils.isEmpty(addOrgList)) {
            addOrgList.forEach(org -> {
                List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList = org.getYunzhijiaOrgUnitList();
                List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList = org.getCreateOrgUnitReqList();
                for (int i = 0; i < yunzhijiaOrgUnitList.size(); i++) {
                    YunzhijiaOrgUnit yunzhijiaOrgUnit = yunzhijiaOrgUnitList.get(i);
                    yunzhijiaMiddleWareService.addYunzhijiaOrgUnit(yunzhijiaOrgUnit);
                    //进行任务处理，不进行分贝通服务调用
                    yunzhijiaTaskService.createYunzhijiaTask(yunzhijiaOrgUnit.getCorpId(), yunzhijiaOrgUnit.getYunzhijiaOrgId(),
                            YunzhijiaCallbackTagConstant.YUNZHIJIA_ORG_DEPT_CREATE, JsonUtils.toJson(yunzhijiaOrgUnit), String.valueOf(System.currentTimeMillis()));
//                    createDepartment(companyId, createOrgUnitReqList.get(i));
                }
            });
        }

    }

    @Override
    public void updateOrgUnit(String companyId, List<YunzhijiaOrgUnitUpdate> updateOrgList) {
        if (!ObjectUtils.isEmpty(updateOrgList)) {
            updateOrgList.forEach(org -> {
                List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList = org.getYunzhijiaOrgUnitList();
                List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList = org.getUpdateOrgUnitReqList();
                updateOrgUnitReqList.forEach(req -> updateDepartment(companyId, req));
                for (int i = 0; i < yunzhijiaOrgUnitList.size(); i++) {
                    YunzhijiaOrgUnit yunzhijiaOrgUnit = yunzhijiaOrgUnitList.get(i);
                    yunzhijiaMiddleWareService.updateYunzhijiaOrgUnit(yunzhijiaOrgUnit);
                    yunzhijiaTaskService.createYunzhijiaTask(yunzhijiaOrgUnit.getCorpId(), yunzhijiaOrgUnit.getYunzhijiaOrgId(),
                            YunzhijiaCallbackTagConstant.YUNZHIJIA_ORG_DEPT_MODIFY, JsonUtils.toJson(yunzhijiaOrgUnit), String.valueOf(System.currentTimeMillis()));
//                    updateDepartment(companyId, updateOrgUnitReqList.get(i));
                }
            });
        }
    }

    @Override
    public void deleteOrgUnit(List<YunzhijiaOrgUnitDelete> deleteOrgList) {
        if (!ObjectUtils.isEmpty(deleteOrgList)) {
            deleteOrgList.forEach(org -> {
                List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList = org.getYunzhijiaOrgUnitList();
                List<SupportDeleteOrgUnitReqDTO> deleteOrgUnitReqList = org.getDeleteOrgUnitReqList();
                deleteOrgUnitReqList.forEach(this::deleteDepartment);
                for (int i = 0; i < yunzhijiaOrgUnitList.size(); i++) {
                    YunzhijiaOrgUnit yunzhijiaOrgUnit = yunzhijiaOrgUnitList.get(i);
//                    yunzhijiaOrgUnit.setState(1);
                    yunzhijiaMiddleWareService.updateYunzhijiaOrgUnit(yunzhijiaOrgUnit);
                    yunzhijiaTaskService.createYunzhijiaTask(yunzhijiaOrgUnit.getCorpId(), yunzhijiaOrgUnit.getYunzhijiaOrgId(),
                            YunzhijiaCallbackTagConstant.YUNZHIJIA_ORG_DEPT_REMOVE, JsonUtils.toJson(yunzhijiaOrgUnit), String.valueOf(System.currentTimeMillis()));
//                    deleteDepartment(deleteOrgUnitReqList.get(i));
                }
            });
        }

    }

    private Map<String, Object> groupDepartment(List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> yunzhijiaDepartmentList, List<YunzhijiaOrgUnit> fbOrgUnitList, String corpId, String companyId) {
        fbOrgUnitList = ObjectUtils.isEmpty(fbOrgUnitList) ? Lists.newArrayList() : fbOrgUnitList;
        Map<String, Object> groupDepartMap = Maps.newHashMap();
        //原有云之家部门id列表
        List<String> yunzhijiaSrcOrgIdList = fbOrgUnitList.stream().map(YunzhijiaOrgUnit::getYunzhijiaOrgId).collect(Collectors.toList());
        //现在云之家部门id列表
        List<String> yunzhijiaCurrentOrgIdList = yunzhijiaDepartmentList.stream().map(YunzhijiaOrgRespDTO.YunzhijiaOrgDTO::getOrgId).collect(Collectors.toList());
        //要新增的部门列表
        List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> addOrgList = yunzhijiaDepartmentList.stream().filter(d -> !yunzhijiaSrcOrgIdList.contains(d.getOrgId())).collect(Collectors.toList());
        //要删除的部门列表
        List<YunzhijiaOrgUnit> deleteOrgList = fbOrgUnitList.stream().filter(d -> !yunzhijiaCurrentOrgIdList.contains(d.getYunzhijiaOrgId())).collect(Collectors.toList());
        //要更新的部门
        List<YunzhijiaOrgUnit> updateOrgList = getUpdateOrgList(yunzhijiaDepartmentList, fbOrgUnitList, deleteOrgList);
        //管理员
        PluginCorpDefinition corpByThirdCorpId = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        String superAdmin = corpByThirdCorpId.getAdminId();
        //组织增加部门
        buildAddOrg(groupDepartMap, addOrgList, corpId, companyId, superAdmin);
        //组织更新部门
        buildUpdateOrg(groupDepartMap, updateOrgList, yunzhijiaDepartmentList, corpId, companyId, superAdmin);
        //组织更新部门
        buildDeleteOrg(groupDepartMap, deleteOrgList, corpId, companyId, superAdmin);
        return groupDepartMap;
    }

    private void buildDeleteOrg(Map<String, Object> groupDepartMap, List<YunzhijiaOrgUnit> deleteOrgList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(deleteOrgList)) {
            List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList = Lists.newArrayList();
            List<SupportDeleteOrgUnitReqDTO> deleteOrgUnitReqList = Lists.newArrayList();
            deleteOrgList.forEach(dept -> {
                YunzhijiaOrgUnit yunzhijiaOrgUnit = new YunzhijiaOrgUnit();
                yunzhijiaOrgUnit.setId(dept.getId());
                yunzhijiaOrgUnit.setCorpId(corpId);
                yunzhijiaOrgUnit.setYunzhijiaOrgId(dept.getYunzhijiaOrgId());
                yunzhijiaOrgUnit.setYunzhijiaParentOrgId(dept.getYunzhijiaParentOrgId());
                yunzhijiaOrgUnit.setYunzhijiaOrgName(dept.getYunzhijiaOrgName());
//                qywxOrgUnit.setQywxOrgOrder(dept.getQywxOrgOrder());
                yunzhijiaOrgUnit.setState(1);
                yunzhijiaOrgUnit.setUpdateTime(new Date());
                yunzhijiaOrgUnitList.add(yunzhijiaOrgUnit);
                SupportDeleteOrgUnitReqDTO deleteOrgUnitReq = new SupportDeleteOrgUnitReqDTO();
                deleteOrgUnitReq.setCompanyId(companyId);
                deleteOrgUnitReq.setThirdOrgId(StringUtils.obj2str(dept.getYunzhijiaOrgId()));
                deleteOrgUnitReq.setOperatorId(superAdmin);
                deleteOrgUnitReqList.add(deleteOrgUnitReq);
            });
            YunzhijiaOrgUnitDelete delete = new YunzhijiaOrgUnitDelete();
            delete.setYunzhijiaOrgUnitList(yunzhijiaOrgUnitList);
            delete.setDeleteOrgUnitReqList(deleteOrgUnitReqList);
            groupDepartMap.put(YunzhijiaPullOrgConstant.DELETE, Lists.newArrayList(delete));
        }
    }

    private void buildUpdateOrg(Map<String, Object> groupDepartMap, List<YunzhijiaOrgUnit> updateOrgList, List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> yunzhijiaDepartmentList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(updateOrgList)) {
            Map<String, YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> wechatDepartmentMap = yunzhijiaDepartmentList.stream().collect(Collectors.toMap(YunzhijiaOrgRespDTO.YunzhijiaOrgDTO::getOrgId, o -> o));
            List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList = Lists.newArrayList();
            List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList = Lists.newArrayList();
            updateOrgList.forEach(org -> {
                YunzhijiaOrgRespDTO.YunzhijiaOrgDTO dept = wechatDepartmentMap.get(org.getYunzhijiaOrgId());
                YunzhijiaOrgUnit yunzhijiaOrgUnit = new YunzhijiaOrgUnit();
                yunzhijiaOrgUnit.setId(org.getId());
                yunzhijiaOrgUnit.setCorpId(corpId);
                yunzhijiaOrgUnit.setYunzhijiaOrgId(dept.getOrgId());
                yunzhijiaOrgUnit.setYunzhijiaParentOrgId(dept.getParentId());
                yunzhijiaOrgUnit.setYunzhijiaOrgName(dept.getName());
//                qywxOrgUnit.setQywxOrgOrder(dept.getOrder());
                yunzhijiaOrgUnit.setState(0);
                yunzhijiaOrgUnit.setUpdateTime(new Date());
                yunzhijiaOrgUnitList.add(yunzhijiaOrgUnit);
                SupportUpdateOrgUnitReqDTO updateOrgUnitReq = new SupportUpdateOrgUnitReqDTO();
                updateOrgUnitReq.setCompanyId(companyId);
                updateOrgUnitReq.setOrgUnitName(dept.getName());
                String thirdParentId = StringUtils.obj2str(dept.getParentId());
                updateOrgUnitReq.setThirdParentId("1".equals(thirdParentId) ? corpId : thirdParentId);
                updateOrgUnitReq.setThirdOrgId(StringUtils.obj2str(dept.getId()));
                updateOrgUnitReq.setOperatorId(superAdmin);
                updateOrgUnitReqList.add(updateOrgUnitReq);
            });
            YunzhijiaOrgUnitUpdate update = new YunzhijiaOrgUnitUpdate();
            update.setYunzhijiaOrgUnitList(yunzhijiaOrgUnitList);
            update.setUpdateOrgUnitReqList(updateOrgUnitReqList);
            groupDepartMap.put(YunzhijiaPullOrgConstant.UPDATE, Lists.newArrayList(update));
        }
    }

    private void buildAddOrg(Map<String, Object> groupDepartMap, List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> addOrgList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(addOrgList)) {
            List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList = Lists.newArrayList();
            List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList = Lists.newArrayList();
            addOrgList.forEach(dept -> {
                YunzhijiaOrgUnit yunzhijiaOrgUnit = new YunzhijiaOrgUnit();
                yunzhijiaOrgUnit.setCorpId(corpId);
                yunzhijiaOrgUnit.setYunzhijiaOrgId(dept.getOrgId());
                yunzhijiaOrgUnit.setYunzhijiaParentOrgId(dept.getParentId());
                yunzhijiaOrgUnit.setYunzhijiaOrgName(dept.getName());
//                qywxOrgUnit.setQywxOrgOrder(dept.getOrder());
                yunzhijiaOrgUnit.setState(0);
                yunzhijiaOrgUnit.setCreateTime(new Date());
                yunzhijiaOrgUnit.setUpdateTime(new Date());
                yunzhijiaOrgUnitList.add(yunzhijiaOrgUnit);
                SupportCreateOrgUnitReqDTO createOrgUnitReq = new SupportCreateOrgUnitReqDTO();
                createOrgUnitReq.setCompanyId(companyId);
                createOrgUnitReq.setOrgUnitName(dept.getName());
                String thirdParentId = StringUtils.obj2str(dept.getParentId());
                createOrgUnitReq.setThirdParentId("1".equals(thirdParentId) ? corpId : thirdParentId);
                createOrgUnitReq.setThirdOrgId(StringUtils.obj2str(dept.getId()));
                createOrgUnitReq.setOperatorId(superAdmin);
                createOrgUnitReqList.add(createOrgUnitReq);
            });
            YunzhijiaOrgUnitAdd add = new YunzhijiaOrgUnitAdd();
            add.setYunzhijiaOrgUnitList(yunzhijiaOrgUnitList);
            add.setCreateOrgUnitReqList(createOrgUnitReqList);
            groupDepartMap.put(YunzhijiaPullOrgConstant.INSERT, Lists.newArrayList(add));
        }
    }

    private List<YunzhijiaOrgUnit> getUpdateOrgList(List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> yunzhijiaDepartmentList, List<YunzhijiaOrgUnit> fbOrgUnitList, List<YunzhijiaOrgUnit> deleteOrgList) {
        Map<String, YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> wechatDepartmentMap = yunzhijiaDepartmentList.stream().collect(Collectors.toMap(YunzhijiaOrgRespDTO.YunzhijiaOrgDTO::getOrgId, d -> d));
        //要删除的id列表
        List<String> deleteOrgIdList = deleteOrgList.stream().map(YunzhijiaOrgUnit::getYunzhijiaOrgId).collect(Collectors.toList());
        return fbOrgUnitList.stream().filter(o -> !deleteOrgIdList.contains(o.getYunzhijiaOrgId()))
                .filter(o -> {
                    YunzhijiaOrgRespDTO.YunzhijiaOrgDTO wechatDepartment = wechatDepartmentMap.get(o.getYunzhijiaOrgId());
                    boolean update = false;
                    //父部门ID或者名称不相同则更新
                    if (!wechatDepartment.getName().equals(o.getYunzhijiaOrgName())) {
                        update = true;
                    }
                    if (!wechatDepartment.getParentId().equals(o.getYunzhijiaParentOrgId())) {
                        update = true;
                    }
                    return update;
                })
                .collect(Collectors.toList());
    }


    public List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> getYunzhijiaDepartmentList(String yunzhijiaToken, String corpId) {
        //根据公司ID获取云之家所有部门ID
        YunzhijiaOrgRespDTO departmentListResp = yunzhijiaRemoteOrgService.getAllDepByCorpId(yunzhijiaToken, corpId);

        //检查云之家部门
        checkYunzhijiaDepartmentListResp(departmentListResp);
        List<YunzhijiaOrgRespDTO.YunzhijiaOrgDTO> orgListData = departmentListResp.getData();
        return orgListData;
    }

    private void checkYunzhijiaDepartmentListResp(YunzhijiaOrgRespDTO departmentListResp) {
        if (departmentListResp == null || Optional.ofNullable(departmentListResp.getErrorCode()).orElse(-1) != 100) {
            throw new OpenApiPluginException(NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_ORG_NULL));
        }
    }


    @Data
    public static class YunzhijiaOrgUnitAdd {
        private List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList;
        private List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList;
    }

    @Data
    public static class YunzhijiaOrgUnitUpdate {
        private List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList;
        private List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList;
    }

    @Data
    public static class YunzhijiaOrgUnitDelete {
        private List<YunzhijiaOrgUnit> yunzhijiaOrgUnitList;
        private List<SupportDeleteOrgUnitReqDTO> deleteOrgUnitReqList;
    }


    public List<YunzhijiaOrgLeaderDTO> getYunzhijiaRemoteAllOrgLeaders(String eid) {
        //1.根据企业ID查询通讯录token
        YunzhijiaAddressList yunzhijiaToken = yunzhijiaTokenService.getYunzhijiaToken(eid);
        if (ObjectUtils.isEmpty(yunzhijiaToken)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL)));
        }
        Example example = new Example(YunzhijiaAddressList.class);
        example.createCriteria()
                .andEqualTo("corpId",eid);
        YunzhijiaAddressList byExample = yunzhijiaAddressListDao.getByExample(example);
        if(ObjectUtils.isEmpty(byExample)){
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_TOKEN_INFO_IS_NULL)));
        }
        //3.获取access_token对象构建
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .secret(byExample.getCorpSecret())
                .scope(YunzhijiaResourceLevelConstant.RES_GROUP_SECRET)
                .eid(eid)
                .timestamp(System.currentTimeMillis())
                .build();
        List<YunzhijiaOrgLeaderDTO> yunzhijiaRemoteAllOrgLeaders = yunzhijiaRemoteOrgService.getYunzhijiaRemoteAllOrgLeaders(build);
        return yunzhijiaRemoteAllOrgLeaders;
    }


}
