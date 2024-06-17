package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.openapi.plugin.support.organization.dto.OrgUnitDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse.Department;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.SyncConstant;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiDepartmentService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportBindOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportCreateOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportUpdateOrgUnitReqDTO;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyCreatVo;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: DingTalkSyncThirdOrgServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/11 5:55 PM
 */
@ServiceAspect
@Service
public class DingTalkSyncThirdOrgServiceImpl extends AbstractOrganizationService implements IDingTalkSyncThirdOrgService {

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private IApiDepartmentService apiDepartmentService;

    @DubboReference(check = false)
    private ICompanyNewInfoService companyNewInfoService;

    @SuppressWarnings("all")
    @Override
    public void syncThirdOrg(String companyId) {
        //加载分贝通部门
        List<OrgUnitDTO> fbOrgUnitList = listFbOrgUnit(companyId);
        //公司信息
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        //钉钉平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        //加载钉钉部门
        List<Department> departments = apiDepartmentService.listDepartment(thirdCorpId);
        //同步部门信息
        Map<String, Object> departmentMap = groupDepartment(fbOrgUnitList, departments, corpDefinition);
        //需要更新的部门
        List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList = (List<SupportUpdateOrgUnitReqDTO>) departmentMap.get(SyncConstant.UPDATE);
        //需要新建的部门
        List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList = (List<SupportCreateOrgUnitReqDTO>) departmentMap.get(SyncConstant.INSERT);
        //需要绑定的部门
        List<SupportBindOrgUnitReqDTO> bindOrgUnitReqList = (List<SupportBindOrgUnitReqDTO>) departmentMap.get(SyncConstant.BIND);
        //同步部门到分贝通
        sync2FbDepartment(companyId, updateOrgUnitReqList, createOrgUnitReqList, bindOrgUnitReqList);
    }

    private Map<String, Object> groupDepartment(List<OrgUnitDTO> fbOrgUnitList, List<Department> departments, PluginCorpDefinition corpDefinition) {
        //公司id
        String companyId = corpDefinition.getAppId();
        //钉钉平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        Map<String, OrgUnitDTO> fbOrgMap = fbOrgUnitList.stream().filter(o -> !ObjectUtils.isEmpty(o.getOrgThirdUnitId())).collect(Collectors.toMap(OrgUnitDTO::getOrgThirdUnitId, o -> o));
        Map<String, Object> departmentMap = Maps.newHashMap();
        String superAdmin = superAdmin(companyId);
        CompanyCreatVo companyCreatVo = companyNewInfoService.info(companyId);
        // 创建存储部门ID和名称对应关系map
        Map<Long, String> idMap = departments.stream().collect(Collectors.toMap(Department::getId, Department::getName));
        idMap.put(1L, companyCreatVo.getCompanyName());
        departments.forEach(department -> {
            Long deptId = department.getId();
            OrgUnitDTO orgUnit = fbOrgMap.get(String.valueOf(deptId));
            if (orgUnit == null) {
                List<String> nameList = apiDepartmentService.listParentDeptIds(deptId, thirdCorpId)
                        .stream().map(idMap::get).collect(Collectors.toList());
                Collections.reverse(nameList);
                String fullName = String.join("/", nameList);
                OrgUnitDTO fbOrgUnit = fbOrgUnitList.stream().filter(o -> o.getOrgUnitFullName().equals(fullName)).findFirst().orElse(null);
                //1:绑定;2:新增
                int opt = fbOrgUnit != null ? 1 : 2;
                if (opt == 1) {
                    SupportBindOrgUnitReqDTO bindOrgUnitReq = new SupportBindOrgUnitReqDTO();
                    bindOrgUnitReq.setCompanyId(companyId);
                    bindOrgUnitReq.setThirdOrgId(String.valueOf(department.getId()));
                    bindOrgUnitReq.setOperatorId(superAdmin);
                    bindOrgUnitReq.setType(2);
                    bindOrgUnitReq.setOrgId(fbOrgUnit.getOrgUnitId());
                    Collections.reverse(nameList);
                    bindOrgUnitReq.setOrgNameList(nameList);
                    departmentMap.putIfAbsent(SyncConstant.BIND, Lists.newArrayList());
                    List<SupportBindOrgUnitReqDTO> bindOrgUnitReqList = (List<SupportBindOrgUnitReqDTO>) departmentMap.get(SyncConstant.BIND);
                    bindOrgUnitReqList.add(bindOrgUnitReq);
                } else {
                    // 如果上级ID为1，则表示为根部门，将上级ID设置为公司ID
                    long dpid = department.getParentid();
                    String parentId = dpid == 1 ? thirdCorpId : String.valueOf(dpid);
                    SupportCreateOrgUnitReqDTO createOrgUnitReq = new SupportCreateOrgUnitReqDTO();
                    createOrgUnitReq.setCompanyId(companyId);
                    createOrgUnitReq.setOrgUnitName(department.getName());
                    createOrgUnitReq.setThirdParentId(parentId);
                    createOrgUnitReq.setThirdOrgId(String.valueOf(department.getId()));
                    createOrgUnitReq.setOperatorId(superAdmin);
                    departmentMap.putIfAbsent(SyncConstant.INSERT, Lists.newArrayList());
                    List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList = (List<SupportCreateOrgUnitReqDTO>) departmentMap.get(SyncConstant.INSERT);
                    createOrgUnitReqList.add(createOrgUnitReq);
                }
            } else {
                // 如果上级ID为1，则表示为根部门，将上级ID设置为公司ID
                long dpid = department.getParentid();
                String parentId = dpid == 1 ? thirdCorpId : String.valueOf(dpid);
                SupportUpdateOrgUnitReqDTO updateOrgUnitReq = new SupportUpdateOrgUnitReqDTO();
                updateOrgUnitReq.setCompanyId(companyId);
                updateOrgUnitReq.setOrgUnitName(department.getName());
                updateOrgUnitReq.setThirdParentId(parentId);
                updateOrgUnitReq.setThirdOrgId(String.valueOf(department.getId()));
                updateOrgUnitReq.setOperatorId(superAdmin);
                departmentMap.putIfAbsent(SyncConstant.UPDATE, Lists.newArrayList());
                List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList = (List<SupportUpdateOrgUnitReqDTO>) departmentMap.get(SyncConstant.UPDATE);
                updateOrgUnitReqList.add(updateOrgUnitReq);
            }
        });
        return departmentMap;
    }

    @Override
    public List<OapiDepartmentListResponse.Department> checkDingtalkDepartment(String companyId) {
        //公司信息
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        //钉钉平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        // 查询所有部门信息,按照部门级别排序
        List<OapiDepartmentListResponse.Department> dingtalkDepartments = apiDepartmentService.listDepartment(thirdCorpId);
        //加载分贝通部门
        List<OrgUnitDTO> fbOrgUnitList = listFbOrgUnit(companyId);
        fbOrgUnitList = fbOrgUnitList.stream().filter(d -> d.getOrgUnitParentId() != null).collect(Collectors.toList());
        List<String> thirdOrgIdList = fbOrgUnitList.stream().map(OrgUnitDTO::getOrgThirdUnitId).collect(Collectors.toList());
        return dingtalkDepartments.stream().filter(d -> !thirdOrgIdList.contains(String.valueOf(d.getId()))).collect(Collectors.toList());
    }
}
