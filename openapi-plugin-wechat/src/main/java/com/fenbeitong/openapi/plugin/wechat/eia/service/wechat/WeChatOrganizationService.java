package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;

import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.OrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportCreateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportDeleteOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportUpdateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO.WechatDepartment;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WechatEiaPullOrgConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxOrgUnit;
import com.fenbeitong.openapi.plugin.wechat.eia.service.organization.WeChatEiaMiddlewareOrgUnitService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.organization.WeChatEiaOrgUnitService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Title: WechatOrganizationService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 6:07 PM
 */
@SuppressWarnings("all")
@ServiceAspect
@Service
public class WeChatOrganizationService extends AbstractOrganizationService {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private WeChatEiaOrgUnitService weChatEiaOrgUnitService;

    @Autowired
    private WeChatEiaMiddlewareOrgUnitService weChatEiaMiddlewareOrgUnitService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    public Map<String, Object> syncWechatOrgUnit(String wechatToken, String corpId, String deptId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        //分贝公司id
        String companyId = corpDefinition.getAppId();
        String companyName = corpDefinition.getAppName();
        //获取企业微信全量部门
        List<WechatDepartment> wechatDepartmentList = getWechatDepartmentList(wechatToken, deptId, companyName);
        //获取分贝通全量部门
        List<QywxOrgUnit> fbOrgUnitList = weChatEiaMiddlewareOrgUnitService.selectQywxOrgUnitByParam(QywxOrgUnit.builder().corpId(corpId).build());
        //对部门进行分组 增删改
        return groupDepartment(wechatDepartmentList, fbOrgUnitList, corpId, companyId);
    }

    public void deleteOrgUnit(List<WechatOrgUnitDelete> deleteOrgList) {
        if (!ObjectUtils.isEmpty(deleteOrgList)) {
            deleteOrgList.forEach(org -> {
                List<QywxOrgUnit> qywxOrgUnitList = org.getQywxOrgUnitList();
                List<SupportDeleteOrgUnitReqDTO> deleteOrgUnitReqList = org.getDeleteOrgUnitReqList();
                deleteOrgUnitReqList.forEach(this::deleteDepartment);
                for (int i = 0; i < qywxOrgUnitList.size(); i++) {
                    weChatEiaMiddlewareOrgUnitService.updateQywxOrgUnit(qywxOrgUnitList.get(i));
                    deleteDepartment(deleteOrgUnitReqList.get(i));
                }
            });
        }
    }

    public void addOrgUnit(String companyId, List<WechatOrgUnitAdd> addOrgList) {
        if (!ObjectUtils.isEmpty(addOrgList)) {
            addOrgList.forEach(org -> {
                List<QywxOrgUnit> qywxOrgUnitList = org.getQywxOrgUnitList();
                List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList = org.getCreateOrgUnitReqList();
                for (int i = 0; i < qywxOrgUnitList.size(); i++) {
                    weChatEiaMiddlewareOrgUnitService.addQywxOrgUnit(qywxOrgUnitList.get(i));
                    createDepartment(companyId, createOrgUnitReqList.get(i));
                }
            });
        }
    }

    public void updateOrgUnit(String companyId, List<WechatOrgUnitUpdate> updateOrgList) {
        if (!ObjectUtils.isEmpty(updateOrgList)) {
            updateOrgList.forEach(org -> {
                List<QywxOrgUnit> qywxOrgUnitList = org.getQywxOrgUnitList();
                List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList = org.getUpdateOrgUnitReqList();
                updateOrgUnitReqList.forEach(req -> updateDepartment(companyId, req));
                for (int i = 0; i < qywxOrgUnitList.size(); i++) {
                    weChatEiaMiddlewareOrgUnitService.updateQywxOrgUnit(qywxOrgUnitList.get(i));
                    updateDepartment(companyId, updateOrgUnitReqList.get(i));
                }
            });
        }
    }

    public Map<String, Object> groupDepartment(List<WechatDepartment> wechatDepartmentList, List<QywxOrgUnit> fbOrgUnitList, String corpId, String companyId) {
        fbOrgUnitList = ObjectUtils.isEmpty(fbOrgUnitList) ? Lists.newArrayList() : fbOrgUnitList;
        Map<String, Object> groupDepartMap = Maps.newHashMap();
        //原有微信部门id列表
        List<Long> wechatSrcOrgIdList = fbOrgUnitList.stream().map(QywxOrgUnit::getQywxOrgId).collect(Collectors.toList());
        //现在微信部门id列表
        List<Long> wechatCurrentOrgIdList = wechatDepartmentList.stream().map(WechatDepartment::getId).collect(Collectors.toList());
        //要新增的部门列表
        List<WechatDepartment> addOrgList = wechatDepartmentList.stream().filter(d -> !wechatSrcOrgIdList.contains(d.getId())).collect(Collectors.toList());
        //要删除的部门列表
        List<QywxOrgUnit> deleteOrgList = fbOrgUnitList.stream().filter(d -> !wechatCurrentOrgIdList.contains(d.getQywxOrgId())).collect(Collectors.toList());
        //要更新的部门
        List<QywxOrgUnit> updateOrgList = getUpdateOrgList(wechatDepartmentList, fbOrgUnitList, deleteOrgList);
        //管理员
        String superAdmin = superAdmin(companyId);
        //组织增加部门
        buildAddOrg(groupDepartMap, addOrgList, corpId, companyId, superAdmin);
        //组织更新部门
        buildUpdateOrg(groupDepartMap, updateOrgList, wechatDepartmentList, corpId, companyId, superAdmin);
        //组织更新部门
        buildDeleteOrg(groupDepartMap, deleteOrgList, corpId, companyId, superAdmin);
        return groupDepartMap;
    }

    private void buildDeleteOrg(Map<String, Object> groupDepartMap, List<QywxOrgUnit> deleteOrgList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(deleteOrgList)) {
            List<QywxOrgUnit> qywxOrgUnitList = Lists.newArrayList();
            List<SupportDeleteOrgUnitReqDTO> deleteOrgUnitReqList = Lists.newArrayList();
            Collections.reverse(deleteOrgList);
            deleteOrgList.forEach(dept -> {
                QywxOrgUnit qywxOrgUnit = new QywxOrgUnit();
                qywxOrgUnit.setId(dept.getId());
                qywxOrgUnit.setCorpId(corpId);
                qywxOrgUnit.setQywxOrgId(dept.getId());
                qywxOrgUnit.setQywxParentOrgId(dept.getQywxParentOrgId());
                qywxOrgUnit.setQywxOrgName(dept.getQywxOrgName());
                qywxOrgUnit.setQywxOrgOrder(dept.getQywxOrgOrder());
                qywxOrgUnit.setState(1);
                qywxOrgUnit.setUpdateTime(new Date());
                qywxOrgUnitList.add(qywxOrgUnit);
                SupportDeleteOrgUnitReqDTO deleteOrgUnitReq = new SupportDeleteOrgUnitReqDTO();
                deleteOrgUnitReq.setCompanyId(companyId);
                deleteOrgUnitReq.setThirdOrgId(StringUtils.obj2str(dept.getQywxOrgId()));
                deleteOrgUnitReq.setOperatorId(superAdmin);
                deleteOrgUnitReqList.add(deleteOrgUnitReq);
            });
            WechatOrgUnitDelete delete = new WechatOrgUnitDelete();
            delete.setQywxOrgUnitList(qywxOrgUnitList);
            delete.setDeleteOrgUnitReqList(deleteOrgUnitReqList);
            groupDepartMap.put(WechatEiaPullOrgConstant.DELETE, Lists.newArrayList(delete));
        }
    }

    private void buildUpdateOrg(Map<String, Object> groupDepartMap, List<QywxOrgUnit> updateOrgList, List<WechatDepartment> wechatDepartmentList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(updateOrgList)) {
            Map<Long, WechatDepartment> wechatDepartmentMap = wechatDepartmentList.stream().collect(Collectors.toMap(WechatDepartment::getId, o -> o));
            List<QywxOrgUnit> qywxOrgUnitList = Lists.newArrayList();
            List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList = Lists.newArrayList();
            updateOrgList.forEach(org -> {
                WechatDepartment dept = wechatDepartmentMap.get(org.getQywxOrgId());
                QywxOrgUnit qywxOrgUnit = new QywxOrgUnit();
                qywxOrgUnit.setId(org.getId());
                qywxOrgUnit.setCorpId(corpId);
                qywxOrgUnit.setQywxOrgId(dept.getId());
                qywxOrgUnit.setQywxParentOrgId(dept.getParentId());
                qywxOrgUnit.setQywxOrgName(dept.getName());
                qywxOrgUnit.setQywxOrgOrder(NumericUtils.obj2int(dept.getOrder()));
                qywxOrgUnit.setState(0);
                qywxOrgUnit.setUpdateTime(new Date());
                qywxOrgUnitList.add(qywxOrgUnit);
                SupportUpdateOrgUnitReqDTO updateOrgUnitReq = new SupportUpdateOrgUnitReqDTO();
                updateOrgUnitReq.setCompanyId(companyId);
                updateOrgUnitReq.setOrgUnitName(dept.getName());
                String thirdParentId = StringUtils.obj2str(dept.getParentId());
                if("0".equals(thirdParentId)) {
                    updateOrgUnitReq.setThirdParentId(null);
                } else if("1".equals(thirdParentId)) {
                    updateOrgUnitReq.setThirdParentId(corpId);
                } else {
                    updateOrgUnitReq.setThirdParentId(thirdParentId);
                }
                String thirdOrgId = StringUtils.obj2str(dept.getId());
                updateOrgUnitReq.setThirdOrgId("1".equals(thirdOrgId) ? corpId : thirdOrgId);
                updateOrgUnitReq.setOperatorId(superAdmin);
                updateOrgUnitReqList.add(updateOrgUnitReq);
            });
            WechatOrgUnitUpdate update = new WechatOrgUnitUpdate();
            update.setQywxOrgUnitList(qywxOrgUnitList);
            update.setUpdateOrgUnitReqList(updateOrgUnitReqList);
            groupDepartMap.put(WechatEiaPullOrgConstant.UPDATE, Lists.newArrayList(update));
        }
    }

    public void buildAddOrg(Map<String, Object> groupDepartMap, List<WechatDepartment> addOrgList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(addOrgList)) {
            List<QywxOrgUnit> qywxOrgUnitList = Lists.newArrayList();
            List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList = Lists.newArrayList();
            addOrgList.forEach(dept -> {
                QywxOrgUnit qywxOrgUnit = new QywxOrgUnit();
                qywxOrgUnit.setCorpId(corpId);
                qywxOrgUnit.setQywxOrgId(dept.getId());
                qywxOrgUnit.setQywxParentOrgId(dept.getParentId());
                qywxOrgUnit.setQywxOrgName(dept.getName());
                qywxOrgUnit.setQywxOrgOrder(NumericUtils.obj2int(dept.getOrder()));
                qywxOrgUnit.setState(0);
                qywxOrgUnit.setCreateTime(new Date());
                qywxOrgUnit.setUpdateTime(new Date());
                qywxOrgUnitList.add(qywxOrgUnit);
                SupportCreateOrgUnitReqDTO createOrgUnitReq = new SupportCreateOrgUnitReqDTO();
                createOrgUnitReq.setCompanyId(companyId);
                createOrgUnitReq.setOrgUnitName(dept.getName());
                String thirdParentId = StringUtils.obj2str(dept.getParentId());
                createOrgUnitReq.setThirdParentId("1".equals(thirdParentId) ? corpId : thirdParentId);
                String thirdOrgId = StringUtils.obj2str(dept.getId());
                createOrgUnitReq.setThirdOrgId("1".equals(thirdOrgId) ? corpId : thirdOrgId);
                createOrgUnitReq.setOperatorId(superAdmin);
                createOrgUnitReqList.add(createOrgUnitReq);
            });
            WechatOrgUnitAdd add = new WechatOrgUnitAdd();
            add.setQywxOrgUnitList(qywxOrgUnitList);
            add.setCreateOrgUnitReqList(createOrgUnitReqList);
            groupDepartMap.put(WechatEiaPullOrgConstant.INSERT, Lists.newArrayList(add));
        }
    }

    private List<QywxOrgUnit> getUpdateOrgList(List<WechatDepartment> wechatDepartmentList, List<QywxOrgUnit> fbOrgUnitList, List<QywxOrgUnit> deleteOrgList) {
        Map<Long, WechatDepartment> wechatDepartmentMap = wechatDepartmentList.stream().collect(Collectors.toMap(WechatDepartment::getId, d -> d));
        //要删除的id列表
        List<Long> deleteOrgIdList = deleteOrgList.stream().map(QywxOrgUnit::getId).collect(Collectors.toList());
        return fbOrgUnitList.stream().filter(o -> !deleteOrgIdList.contains(o.getId()))
                .filter(o -> {
                    WechatDepartment wechatDepartment = wechatDepartmentMap.get(o.getQywxOrgId());
                    boolean update = false;
                    //父部门ID或者名称不相同则更新
                    if (!ObjectUtils.isEmpty(wechatDepartment.getName()) && !wechatDepartment.getName().equals(o.getQywxOrgName())) {
                        update = true;
                    }
                    if (!ObjectUtils.isEmpty(wechatDepartment.getParentId()) && !wechatDepartment.getParentId().equals(o.getQywxParentOrgId())) {
                        update = true;
                    }
                    return update;
                })
                .collect(Collectors.toList());
    }


    public List<WechatDepartment> getWechatDepartmentList(String wechatToken, String deptId, String companyName) {
        WechatDepartmentListRespDTO departmentListResp = weChatEiaOrgUnitService.getAllDepByDepId(wechatToken, deptId);
        //检查微信部门
        checkWechatDepartmentListResp(departmentListResp);
        return departmentListResp.getIsvDepartmentList(companyName);
    }

    private void checkWechatDepartmentListResp(WechatDepartmentListRespDTO departmentListResp) {
        if (departmentListResp == null || Optional.ofNullable(departmentListResp.getErrCode()).orElse(-1) != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_DEPT_IS_NULL));
        }
    }

    public Object checkDepartment(String wechatToken, String companyId, String corpId, String companyName) {
        List<OrgUnitDTO> fbOrgUnitList = listFbOrgUnit(companyId);
        fbOrgUnitList = ObjectUtils.isEmpty(fbOrgUnitList) ? Lists.newArrayList() : fbOrgUnitList;
        List<String> thirdOrgIdList = fbOrgUnitList.stream().filter(o -> !ObjectUtils.isEmpty(o.getOrgThirdUnitId())).map(OrgUnitDTO::getOrgThirdUnitId).collect(Collectors.toList());
        List<WechatDepartment> wechatDepartmentList = getWechatDepartmentList(wechatToken, "1", companyName);
        List<WechatDepartment> unSyncDepartmentList = wechatDepartmentList.stream().filter(d -> d.getParentId() != 0).filter(d -> !thirdOrgIdList.contains(StringUtils.obj2str(d.getId()))).collect(Collectors.toList());
        Map<String, Object> resultMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(unSyncDepartmentList)) {
            resultMap.put("error_count", unSyncDepartmentList.size());
            String errorDeptIds = String.join(",", unSyncDepartmentList.stream().map(d -> "'" + d.getId() + "'").collect(Collectors.toList()));
            resultMap.put("error_dept_ids", "(" + errorDeptIds + ")");
            String errorMsg = "以下部门未同步到分贝通:" + String.join(",", unSyncDepartmentList.stream().map(d -> d.getId() + ":" + d.getParentId() + ":" + d.getName()).collect(Collectors.toList()));
            resultMap.put("error_msg", errorMsg);
        }
        return resultMap;
    }

    @Data
    public static class WechatOrgUnitAdd {

        private List<QywxOrgUnit> qywxOrgUnitList;

        private List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList;
    }

    @Data
    public static class WechatOrgUnitUpdate {

        private List<QywxOrgUnit> qywxOrgUnitList;

        private List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList;
    }

    @Data
    public static class WechatOrgUnitDelete {

        private List<QywxOrgUnit> qywxOrgUnitList;

        private List<SupportDeleteOrgUnitReqDTO> deleteOrgUnitReqList;
    }
}
