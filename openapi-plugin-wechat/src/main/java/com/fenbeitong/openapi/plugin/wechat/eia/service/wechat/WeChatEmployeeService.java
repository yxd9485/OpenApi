package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;

import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.dto.*;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatUserListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatUserListRespDTO.WechatUser;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WechatEiaPullOrgConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxEmployee;
import com.fenbeitong.openapi.plugin.wechat.eia.service.employee.WeChatEiaEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.employee.WeChatEiaMiddlewareEmployeeService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Title: WechatEmployeeService</p>
 * <p>Description: 企业微信员工服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/25 6:06 PM
 */
@ServiceAspect
@Service
public class WeChatEmployeeService extends AbstractEmployeeService {

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private WeChatEiaEmployeeService weChatEiaEmployeeService;

    @Autowired
    private WeChatEiaMiddlewareEmployeeService weChatEiaMiddlewareEmployeeService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    public Map<String, Object> syncWechatUser(String wechatToken, String corpId, String deptId) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        //分贝公司id
        String companyId = corpDefinition.getAppId();
        //获取企业微信全量人员
        List<WechatUser> wechatUserList = getWechatUserList(wechatToken, deptId);
        //获取分贝通全量人员
        List<QywxEmployee> fbEmployeeList = weChatEiaMiddlewareEmployeeService.getEmployeeListByCorpId(corpId);
        //对人员进行分组 增删改
        return groupUser(wechatUserList, fbEmployeeList, corpId, companyId);
    }

    public void createEmployee(WeChatUserAdd userAdd) {
        if (userAdd != null) {
            List<QywxEmployee> qywxEmployeeList = userAdd.getQywxEmployeeList();
            List<SupportCreateEmployeeReqDTO> createUserReqList = userAdd.getCreateUserReqList();
            List<List<QywxEmployee>> batchQywxEmployeeList = CollectionUtils.batch(qywxEmployeeList, 50);
            List<List<SupportCreateEmployeeReqDTO>> batchSupportCreateEmployeeReqList = CollectionUtils.batch(createUserReqList, 50);
            for (int i = 0; i < batchQywxEmployeeList.size(); i++) {
                List<QywxEmployee> currentBatchQywxEmployeeList = batchQywxEmployeeList.get(i);
                weChatEiaMiddlewareEmployeeService.insertQywxEmployeeList(currentBatchQywxEmployeeList);
                List<SupportCreateEmployeeReqDTO> currentBatchCreateEmployeeReqList = batchSupportCreateEmployeeReqList.get(i);
                List<SupportEmployeeInsertDTO> employeeInsertList = Lists.newArrayList();
                currentBatchCreateEmployeeReqList.forEach(req -> employeeInsertList.addAll(req.getEmployeeList()));
                SupportCreateEmployeeReqDTO supportCreateEmployeeReq = currentBatchCreateEmployeeReqList.get(0);
                supportCreateEmployeeReq.setEmployeeList(employeeInsertList);
                createUser(supportCreateEmployeeReq);
            }
        }
    }

    public void updateEmployee(WeChatUserUpdate userUpdate) {
        if (userUpdate != null) {
            List<QywxEmployee> qywxEmployeeList = userUpdate.getQywxEmployeeList();
            List<SupportUpdateEmployeeReqDTO> updateUserReqList = userUpdate.getUpdateUserReqList();
            List<List<QywxEmployee>> batchQywxEmployeeList = CollectionUtils.batch(qywxEmployeeList, 50);
            List<List<SupportUpdateEmployeeReqDTO>> batchSupportUpdateEmployeeReqList = CollectionUtils.batch(updateUserReqList, 50);
            for (int i = 0; i < batchQywxEmployeeList.size(); i++) {
                List<QywxEmployee> currentBatchQywxEmployeeList = batchQywxEmployeeList.get(i);
                weChatEiaMiddlewareEmployeeService.updateQywxEmployeeList(currentBatchQywxEmployeeList);
                List<SupportUpdateEmployeeReqDTO> currentBatchUpdateEmployeeReqList = batchSupportUpdateEmployeeReqList.get(i);
                List<SupportEmployeeUpdateDTO> employeeUpdateList = Lists.newArrayList();
                currentBatchUpdateEmployeeReqList.forEach(req -> employeeUpdateList.addAll(req.getEmployeeList()));
                SupportUpdateEmployeeReqDTO supportUpdateEmployeeReq = currentBatchUpdateEmployeeReqList.get(0);
                supportUpdateEmployeeReq.setEmployeeList(employeeUpdateList);
                updateUser(supportUpdateEmployeeReq);
            }
        }
    }

    public void deleteEmployee(WeChatUserDelete userDelete) {
        if (userDelete != null) {
            List<QywxEmployee> qywxEmployeeList = userDelete.getQywxEmployeeList();
            List<SupportDeleteEmployeeReqDTO> deleteUserReqList = userDelete.getDeleteUserReqList();
            List<List<QywxEmployee>> batchQywxEmployeeList = CollectionUtils.batch(qywxEmployeeList, 50);
            List<List<SupportDeleteEmployeeReqDTO>> batchSupportDeleteEmployeeReqList = CollectionUtils.batch(deleteUserReqList, 50);
            for (int i = 0; i < batchQywxEmployeeList.size(); i++) {
                List<QywxEmployee> currentBatchQywxEmployeeList = batchQywxEmployeeList.get(i);
                weChatEiaMiddlewareEmployeeService.updateQywxEmployeeList(currentBatchQywxEmployeeList);
                List<SupportDeleteEmployeeReqDTO> currentBatchDeleteEmployeeReqList = batchSupportDeleteEmployeeReqList.get(i);
                List<String> thirdEmployeeIdList = Lists.newArrayList();
                currentBatchDeleteEmployeeReqList.forEach(req -> thirdEmployeeIdList.addAll(req.getThirdEmployeeIds()));
                SupportDeleteEmployeeReqDTO supportDeleteEmployeeReq = currentBatchDeleteEmployeeReqList.get(0);
                supportDeleteEmployeeReq.setThirdEmployeeIds(thirdEmployeeIdList);
                deleteUser(supportDeleteEmployeeReq);
            }
        }
    }

    public Map<String, Object> groupUser(List<WechatUser> wechatUserList, List<QywxEmployee> fbEmployeeList, String corpId, String companyId) {
        fbEmployeeList = ObjectUtils.isEmpty(fbEmployeeList) ? Lists.newArrayList() : fbEmployeeList;
        Map<String, Object> groupUserMap = Maps.newHashMap();
        //原有微信用户id列表
        List<String> wechatSrcUserIdList = fbEmployeeList.stream().map(QywxEmployee::getUserId).collect(Collectors.toList());
        //现在微信用户id列表
        List<String> wechatCurrentOrgIdList = wechatUserList.stream().map(WechatUser::getUserId).collect(Collectors.toList());
        //要新增的员工列表
        List<WechatUser> addEmployeeList = wechatUserList.stream().filter(d -> !wechatSrcUserIdList.contains(d.getUserId())).collect(Collectors.toList());
        //要删除的部门列表
        List<QywxEmployee> deleteEmployeeList = fbEmployeeList.stream().filter(d -> !wechatCurrentOrgIdList.contains(d.getUserId())).collect(Collectors.toList());
        //要更新的人员
        List<QywxEmployee> updateEmployeeList = getUpdateEmployeeList(wechatUserList, fbEmployeeList, deleteEmployeeList);
        //管理员
        String superAdmin = superAdmin(companyId);
        //组织增加人员
        buildAddUser(groupUserMap, addEmployeeList, corpId, companyId, superAdmin);
        //组织更新人员
        buildUpdateUser(groupUserMap, updateEmployeeList, wechatUserList, corpId, companyId, superAdmin);
        //组织更新人员
        buildDeleteUser(groupUserMap, deleteEmployeeList, corpId, companyId, superAdmin);
        return groupUserMap;
    }

    private void buildDeleteUser(Map<String, Object> groupUserMap, List<QywxEmployee> deleteEmployeeList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(deleteEmployeeList)) {
            List<QywxEmployee> qywxEmployeeList = Lists.newArrayList();
            List<SupportDeleteEmployeeReqDTO> deleteEmployeeReqList = Lists.newArrayList();
            deleteEmployeeList.forEach(employee -> {
                QywxEmployee qywxEmployee = new QywxEmployee();
                BeanUtils.copyProperties(employee, qywxEmployee);
                qywxEmployee.setEnable(0);
                qywxEmployee.setUpdateTime(new Date());
                qywxEmployeeList.add(qywxEmployee);
                SupportDeleteEmployeeReqDTO deleteEmployeeReq = new SupportDeleteEmployeeReqDTO();
                deleteEmployeeReq.setCompanyId(companyId);
                deleteEmployeeReq.setThirdEmployeeIds(Lists.newArrayList(employee.getUserId()));
                deleteEmployeeReq.setOperatorId(superAdmin);
                deleteEmployeeReqList.add(deleteEmployeeReq);
            });
            WeChatUserDelete delete = new WeChatUserDelete();
            delete.setQywxEmployeeList(qywxEmployeeList);
            delete.setDeleteUserReqList(deleteEmployeeReqList);
            groupUserMap.put(WechatEiaPullOrgConstant.DELETE, delete);
        }
    }

    private void buildUpdateUser(Map<String, Object> groupUserMap, List<QywxEmployee> updateEmployeeList, List<WechatUser> wechatUserList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(updateEmployeeList)) {
            Map<String, WechatUser> wechatUserMap = wechatUserList.stream().collect(Collectors.toMap(WechatUser::getUserId, e -> e));
            List<QywxEmployee> qywxEmployeeList = Lists.newArrayList();
            List<SupportUpdateEmployeeReqDTO> updateEmployeeReqList = Lists.newArrayList();
            updateEmployeeList.forEach(employee -> {
                WechatUser wechatUser = wechatUserMap.get(employee.getUserId());
                QywxEmployee qywxEmployee = new QywxEmployee();
                BeanUtils.copyProperties(wechatUser, qywxEmployee);
                qywxEmployee.setEnable(1);
                qywxEmployee.setId(employee.getId());
                qywxEmployee.setCorpId(corpId);
                qywxEmployee.setExtattr(JsonUtils.toJson(wechatUser.getExtAttr()));
                qywxEmployee.setIsLeaderInDept(JsonUtils.toJson(wechatUser.getIsLeaderInDept()));
                qywxEmployee.setUpdateTime(new Date());
                qywxEmployeeList.add(qywxEmployee);
                SupportUpdateEmployeeReqDTO updateEmployeeReq = new SupportUpdateEmployeeReqDTO();
                updateEmployeeReq.setCompanyId(companyId);
                updateEmployeeReq.setOperatorId(superAdmin);
                SupportEmployeeUpdateDTO employeeUpdate = new SupportEmployeeUpdateDTO();
                employeeUpdate.setUpdateFlag(false);
                employeeUpdate.setName(wechatUser.getName());
                employeeUpdate.setPhone(wechatUser.getMobile());
                String thirdOrgUnitId = wechatUser.getDepartmentStr();
                employeeUpdate.setThirdOrgUnitId("1".equals(thirdOrgUnitId) ? corpId : thirdOrgUnitId);
                employeeUpdate.setThirdEmployeeId(wechatUser.getUserId());
                //新分贝权限
                String nFbPriv = wechatUser.getAttrValueByAttrName("分贝权限", "0");
                employeeUpdate.setRoleType(nFbPriv);
                employeeUpdate.setEmail(wechatUser.getEmail());
                //性别
                String gender = StringUtils.isBlank(wechatUser.getGender()) ? "0" : wechatUser.getGender();
                employeeUpdate.setGender(Integer.valueOf(gender));
                //身份证号
                String idCard = wechatUser.getAttrValueByAttrName("身份证号", null);
                CertDTO certDTO = null;
                if (!ObjectUtils.isEmpty(idCard)) {
                    certDTO = new CertDTO();
                    certDTO.setCertType(1);
                    certDTO.setCertNo(idCard);
                }
                employeeUpdate.setCertList(certDTO == null ? Lists.newArrayList() : Lists.newArrayList(certDTO));
                updateEmployeeReq.setEmployeeList(Lists.newArrayList(employeeUpdate));
                updateEmployeeReq.setOperatorId(superAdmin);
                updateEmployeeReqList.add(updateEmployeeReq);
            });
            WeChatUserUpdate userUpdate = new WeChatUserUpdate();
            userUpdate.setQywxEmployeeList(qywxEmployeeList);
            userUpdate.setUpdateUserReqList(updateEmployeeReqList);
            groupUserMap.put(WechatEiaPullOrgConstant.UPDATE, userUpdate);
        }
    }


    private void buildAddUser(Map<String, Object> groupUserMap, List<WechatUser> addEmployeeList, String corpId, String companyId, String superAdmin) {
        if (!ObjectUtils.isEmpty(addEmployeeList)) {
            List<QywxEmployee> qywxEmployeeList = Lists.newArrayList();
            List<SupportCreateEmployeeReqDTO> createEmployeeReqList = Lists.newArrayList();
            addEmployeeList.forEach(weChatUser -> {
                QywxEmployee qywxEmployee = new QywxEmployee();
                BeanUtils.copyProperties(weChatUser, qywxEmployee);
                qywxEmployee.setEnable(1);
                qywxEmployee.setCorpId(corpId);
                qywxEmployee.setExtattr(JsonUtils.toJson(weChatUser.getExtAttr()));
                qywxEmployee.setIsLeaderInDept(JsonUtils.toJson(weChatUser.getIsLeaderInDept()));
                qywxEmployee.setCreateTime(new Date());
                qywxEmployee.setGender(weChatUser.getGender());
                qywxEmployeeList.add(qywxEmployee);
                SupportCreateEmployeeReqDTO employeeReq = new SupportCreateEmployeeReqDTO();
                employeeReq.setCompanyId(companyId);
                SupportEmployeeInsertDTO employeeInsert = new SupportEmployeeInsertDTO();
                employeeInsert.setName(weChatUser.getName());
                employeeInsert.setPhone(weChatUser.getMobile());
                String thirdOrgUnitId = weChatUser.getDepartmentStr();
                employeeInsert.setThirdOrgUnitId("1".equals(thirdOrgUnitId) ? corpId : thirdOrgUnitId);
                employeeInsert.setThirdEmployeeId(weChatUser.getUserId());
                employeeInsert.setRole(3);
                //新分贝权限
                String nFbPriv = weChatUser.getAttrValueByAttrName("分贝权限", "0");
                employeeInsert.setRoleType(nFbPriv);
                employeeInsert.setEmail(weChatUser.getEmail());
                //性别
                String gender = StringUtils.isBlank(weChatUser.getGender()) ? "0" : weChatUser.getGender();
                employeeInsert.setGender(Integer.valueOf(gender));
                //身份证号
                String idCard = weChatUser.getAttrValueByAttrName("身份证号", null);
                CertDTO certDTO = null;
                if (!ObjectUtils.isEmpty(idCard)) {
                    certDTO = new CertDTO();
                    certDTO.setCertNo(idCard);
                    certDTO.setCertType(1);
                    employeeInsert.setCertList(Lists.newArrayList(certDTO));
                }
                employeeInsert.setCertList(certDTO == null ? Lists.newArrayList() : Lists.newArrayList(certDTO));
                employeeReq.setEmployeeList(Lists.newArrayList(employeeInsert));
                employeeReq.setOperatorId(superAdmin);
                createEmployeeReqList.add(employeeReq);
            });
            WeChatUserAdd userAdd = new WeChatUserAdd();
            userAdd.setQywxEmployeeList(qywxEmployeeList);
            userAdd.setCreateUserReqList(createEmployeeReqList);
            groupUserMap.put(WechatEiaPullOrgConstant.INSERT, userAdd);
        }
    }

    private List<QywxEmployee> getUpdateEmployeeList(List<WechatUser> wechatUserList, List<QywxEmployee> fbEmployeeList, List<QywxEmployee> deleteEmployeeList) {
        Map<String, WechatUser> wechatDepartmentMap = wechatUserList.stream().collect(Collectors.toMap(WechatUser::getUserId, d -> d));
        //要删除的id列表
        List<String> deleteOrgIdList = deleteEmployeeList.stream().map(QywxEmployee::getUserId).collect(Collectors.toList());
        return fbEmployeeList.stream().filter(e -> !deleteOrgIdList.contains(e.getUserId()))
                .filter(o -> {
                    WechatUser wechatUser = wechatDepartmentMap.get(o.getUserId());
                    boolean update = false;
                    if (!StringUtils.isBlank(wechatUser.getName()) && !wechatUser.getName().equals(o.getName())) {
                        update = true;
                    }
                    if (!wechatUser.getDepartmentStr().equals(o.getDepartment())) {
                        update = true;
                    }
                    // 电话为空的不更新，ISV取不到电话
                    if (!StringUtils.isBlank(wechatUser.getMobile()) && !wechatUser.getMobile().equals(o.getMobile())) {
                        update = true;
                    }
                    Map extattr = JsonUtils.toObj(o.getExtattr(), Map.class);
                    List<Map> attrList = (List) extattr.get("attrs");
                    //新分贝权限
                    String nFbPriv = wechatUser.getAttrValueByAttrName("分贝权限", "0");
                    //旧分贝权限
                    String ofbPriv = getAttrValueByAttrName(attrList, "分贝权限", "0");
                    if (!nFbPriv.equals(ofbPriv)) {
                        update = true;
                    }
                    //旧身份证号
                    String oIdCard = getAttrValueByAttrName(attrList, "身份证号", "");
                    //新身份证号
                    String nIdCard = wechatUser.getAttrValueByAttrName("身份证号", "");
                    if (!oIdCard.equals(nIdCard)) {
                        update = true;
                    }
                    return update;
                })
                .collect(Collectors.toList());
    }

    private String getAttrValueByAttrName(List<Map> attrList, String attrName, String defaultValue) {
        String value = ObjectUtils.isEmpty(attrList) ? null :
                (String) attrList.stream()
                        .filter(a -> attrName.equals(a.get("name"))).findFirst()
                        .orElse(Maps.newHashMap())
                        .get("value");
        return ObjectUtils.isEmpty(value) ? defaultValue : value;
    }

    public List<WechatUser> getWechatUserList(String wechatToken, String deptId) {
        WechatUserListRespDTO wechatUserListResp = weChatEiaEmployeeService.getAllUserByDepId(wechatToken, deptId, "1");
        //检查微信人员
        checkWechatUserListResp(wechatUserListResp);
        return wechatUserListResp.getUserList();
    }

    public List<WechatUser> getWechatUserListByDeptList(String wechatToken, List<WechatDepartmentListRespDTO.WechatDepartment> wechatDepartmentList) {
        //按部门获取人员
        List<WechatUser> usersList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(wechatDepartmentList)) {
            for (WechatDepartmentListRespDTO.WechatDepartment wechatDepartment : wechatDepartmentList) {
                Long id = wechatDepartment.getId();
                WechatUserListRespDTO wechatUserListResp = weChatEiaEmployeeService.getAllUserByDepId(wechatToken, StringUtils.obj2str(id), "0");
                if (!ObjectUtils.isEmpty(wechatUserListResp) && !ObjectUtils.isEmpty(wechatUserListResp.getUserList())) {
                    usersList.addAll(wechatUserListResp.getUserList());
                }
            }
        }
        //去重
        List<WechatUserListRespDTO.WechatUser> distinctList = usersList
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getUserId()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(WechatUserListRespDTO.WechatUser::getUserId)).collect(Collectors.toList());
        return distinctList;
    }

    private void checkWechatUserListResp(WechatUserListRespDTO wechatUserListResp) {
        if (wechatUserListResp == null || Optional.ofNullable(wechatUserListResp.getErrCode()).orElse(-1) != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_EMPLOYEE_IS_NULL));
        }
    }

    public Object checkEmployee(String wechatToken, String companyId) {
        List<EmployeeBaseInfo> fbEmployeeList = listFbEmployee(companyId);
        List<String> thirdUserIdList = fbEmployeeList.stream().filter(e -> !ObjectUtils.isEmpty(e.getThirdEmployeeId())).map(EmployeeBaseInfo::getThirdEmployeeId).collect(Collectors.toList());
        List<WechatUser> wechatUserList = getWechatUserList(wechatToken, "1");
        List<WechatUser> unSyncUserList = wechatUserList.stream().filter(e -> !thirdUserIdList.contains(e.getUserId())).collect(Collectors.toList());
        Map<String, Object> resultMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(unSyncUserList)) {
            resultMap.put("error_count", unSyncUserList.size());
            String errorUserIds = String.join(",", unSyncUserList.stream().map(u -> "'" + u.getUserId() + "'").collect(Collectors.toList()));
            resultMap.put("error_user_ids", "(" + errorUserIds + ")");
            String errorMsg = "以下人员未同步到分贝通:" + String.join(",", unSyncUserList.stream().map(e -> e.getUserId() + ":" + e.getName() + ":" + (ObjectUtils.isEmpty(e.getMobile()) ? null : e.getMobile())).collect(Collectors.toList()));
            resultMap.put("error_msg", errorMsg);
        }
        return resultMap;
    }

    public QywxEmployee getQywxEmployeeByMobile(String mobile) {
        return weChatEiaMiddlewareEmployeeService.getQywxEmployeeByMobile(mobile);
    }

    @Data
    public static class WeChatUserAdd {

        private List<QywxEmployee> qywxEmployeeList;

        private List<SupportCreateEmployeeReqDTO> createUserReqList;
    }

    @Data
    public static class WeChatUserUpdate {

        private List<QywxEmployee> qywxEmployeeList;

        private List<SupportUpdateEmployeeReqDTO> updateUserReqList;
    }

    @Data
    public static class WeChatUserDelete {

        private List<QywxEmployee> qywxEmployeeList;

        private List<SupportDeleteEmployeeReqDTO> deleteUserReqList;
    }
}
