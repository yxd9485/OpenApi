package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.support.company.dto.CompanySuperAdmin;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportBindOrgUnitReqDTO;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatUserListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WechatEiaPullOrgConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatOrganizationService;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * isv全量同步
 * Created by lizhen on 2020/3/24.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvPullThirdOrgService {

    @Autowired
    private WeChatIsvOrganizationService weChatIsvOrganizationService;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WeChatIsvSyncThirdOrgService weChatIsvSyncThirdOrgService;

    public void pullThirdOrgByCompanyId(String companyId) {
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        pullThirdOrg(weChatIsvCompany.getCorpId());

    }

    /**
     * 全量拉取组织人员数据
     *
     * @param corpId
     */
    public void pullThirdOrgBak(String corpId) {
        String lockKey = "sync_org_employee:" + corpId;
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 30 * 60 * 1000L);
        if (lockTime > 0) {
            try {
                log.info("开始同步全量组织人员,corpId={}", corpId);
                long start = System.currentTimeMillis();
                WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
                String companyId = weChatIsvCompany.getCompanyId();
                //绑定部门
                log.info("开始同步全量组织人员,绑定部门,corpId={}", corpId);
                bindDepartment(corpId, companyId);
                //同步部门结果
                log.info("开始同步全量组织人员,同步部门结果,corpId={}", corpId);
                Map<String, Object> departmentMap = weChatIsvOrganizationService.syncWechatOrgUnit(corpId, companyId);
                //同步人员结果
                log.info("开始同步全量组织人员,同步人员结果,corpId={}", corpId);
                Map<String, Object> userMap = weChatIsvEmployeeService.syncWechatUser(corpId, companyId);
                //先删除人员
                log.info("开始同步全量组织人员,删除人员,corpId={}", corpId);
                deleteUser(userMap);
                //更新或增加部门
                log.info("开始同步全量组织人员,更新部门,corpId={}", corpId);
                upsertDepartment(companyId, departmentMap);
                //更新或增加人员
                log.info("开始同步全量组织人员,更新人员,corpId={}", corpId);
                upsertUser(userMap);
                //删除部门
                log.info("开始同步全量组织人员,删除部门,corpId={}", corpId);
                deleteDepartment(departmentMap);
                //绑定授权负责人
                //bindSuperAdmin(companyId);
                long end = System.currentTimeMillis();
                log.info("企业微信isv组织人员同步完成，用时{}分钟{}秒...", (end - start) / 60000, ((end - start) % 60000) / 1000);
            } catch (Exception e) {
                log.error("同步组织机构人员失败", e);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        }

    }


    private CompanySuperAdmin superAdmin(String companyId) {
        return superAdminUtils.companySuperAdmin(companyId);
    }

    private void upsertUser(Map<String, Object> userMap) {
        //需要新建的人员
        WeChatEmployeeService.WeChatUserAdd userAdd = (WeChatEmployeeService.WeChatUserAdd) userMap.get(WechatEiaPullOrgConstant.INSERT);
        //增加人员
        weChatIsvEmployeeService.createEmployee(userAdd);
        //需要更新的人员
        WeChatEmployeeService.WeChatUserUpdate userUpdate = (WeChatEmployeeService.WeChatUserUpdate) userMap.get(WechatEiaPullOrgConstant.UPDATE);
        //更新人员
        weChatIsvEmployeeService.updateEmployee(userUpdate);
    }

    private void upsertDepartment(String companyId, Map<String, Object> departmentMap) {
        //需要新建的部门
        List<WeChatOrganizationService.WechatOrgUnitAdd> addOrgList = (List<WeChatOrganizationService.WechatOrgUnitAdd>) departmentMap.get(WechatEiaPullOrgConstant.INSERT);
        weChatIsvOrganizationService.addOrgUnit(companyId, addOrgList);
        //需要更新的部门
        List<WeChatOrganizationService.WechatOrgUnitUpdate> updateOrgList = (List<WeChatOrganizationService.WechatOrgUnitUpdate>) departmentMap.get(WechatEiaPullOrgConstant.UPDATE);
        weChatIsvOrganizationService.updateOrgUnit(companyId, updateOrgList);
    }

    private void deleteDepartment(Map<String, Object> departmentMap) {
        //需要删除的部门C
        List<WeChatOrganizationService.WechatOrgUnitDelete> deleteOrgList = (List<WeChatOrganizationService.WechatOrgUnitDelete>) departmentMap.get(WechatEiaPullOrgConstant.DELETE);
        //删除部门
        weChatIsvOrganizationService.deleteOrgUnit(deleteOrgList);
    }

    private void deleteUser(Map<String, Object> userMap) {
        //需要删除的人员
        WeChatEmployeeService.WeChatUserDelete userDelete = (WeChatEmployeeService.WeChatUserDelete) userMap.get(WechatEiaPullOrgConstant.DELETE);
        //删除人员
        weChatIsvEmployeeService.deleteEmployee(userDelete);
    }


    private void bindDepartment(String corpId, String companyId) {
        String superAdmin = weChatIsvEmployeeService.superAdmin(companyId);
        SupportBindOrgUnitReqDTO supportBindOrgUnitReqDTO = new SupportBindOrgUnitReqDTO();
        supportBindOrgUnitReqDTO.setThirdOrgId(corpId);
        supportBindOrgUnitReqDTO.setCompanyId(companyId);
        supportBindOrgUnitReqDTO.setOrgId(companyId);
        supportBindOrgUnitReqDTO.setType(1);
        supportBindOrgUnitReqDTO.setOperatorId(superAdmin);
        weChatIsvOrganizationService.bind(supportBindOrgUnitReqDTO);
    }

    /**
     * 全量拉取组织人员数据
     *
     * @param corpId
     */
    public void pullThirdOrg(String corpId) {
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, corpId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                log.info("开始同步全量组织人员,corpId={}", corpId);
                long start = System.currentTimeMillis();
                WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
                String companyId = weChatIsvCompany.getCompanyId();
                //同步部门结果
                log.info("开始同步全量组织人员,同步部门结果,corpId={}", corpId);
                List<WechatDepartmentListRespDTO.WechatDepartment> wechatDepartmentList = weChatIsvOrganizationService.getWechatDepartmentList(corpId, "", true);
                //同步人员结果
                log.info("开始同步全量组织人员,同步人员结果,corpId={}", corpId);
                List<WechatUserListRespDTO.WechatUser> wechatUsers = weChatIsvEmployeeService.listAllUser(corpId);
                //转换部门
                List<OpenThirdOrgUnitDTO> departmentList = new ArrayList<>();
                for (WechatDepartmentListRespDTO.WechatDepartment wechatDepartment : wechatDepartmentList) {
                    OpenThirdOrgUnitDTO openThirdOrgUnitDTO = new OpenThirdOrgUnitDTO();
                    openThirdOrgUnitDTO.setCompanyId(companyId);
                    openThirdOrgUnitDTO.setThirdOrgUnitFullName(wechatDepartment.getThirdOrgUnitFullName());
                    openThirdOrgUnitDTO.setThirdOrgUnitName(wechatDepartment.getName());
                    openThirdOrgUnitDTO.setThirdOrgUnitParentId(StringUtils.obj2str(wechatDepartment.getParentId()));
                    openThirdOrgUnitDTO.setThirdOrgUnitId(StringUtils.obj2str(wechatDepartment.getId()));
                    if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitId())) {
                        openThirdOrgUnitDTO.setThirdOrgUnitId(corpId);
                    }
                    if ("1".equals(openThirdOrgUnitDTO.getThirdOrgUnitParentId())) {
                        openThirdOrgUnitDTO.setThirdOrgUnitParentId(corpId);
                    }
                    departmentList.add(openThirdOrgUnitDTO);
                }
                //转换人员
                List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
                for (WechatUserListRespDTO.WechatUser wechatUser : wechatUsers) {
                    OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                    openThirdEmployeeDTO.setCompanyId(companyId);
                    openThirdEmployeeDTO.setThirdDepartmentId(wechatUser.getDepartmentStr());
                    openThirdEmployeeDTO.setThirdEmployeeId(wechatUser.getUserId());
                    openThirdEmployeeDTO.setThirdEmployeeName(wechatUser.getName());
                    openThirdEmployeeDTO.setThirdEmployeePhone(wechatUser.getMobile());
                    openThirdEmployeeDTO.setThirdEmployeeEmail(wechatUser.getEmail());
                    if (!StringUtils.isBlank(wechatUser.getGender())) {
                        openThirdEmployeeDTO.setThirdEmployeeGender(Integer.valueOf(wechatUser.getGender()));
                    }
                    // 1=已激活，2=已禁用，4=未激活，5=退出企业。
                    if (1 == wechatUser.getStatus() || 2 == wechatUser.getStatus()) {
                        openThirdEmployeeDTO.setStatus(wechatUser.getStatus());
                    }
                    // 未激活算正常状态
                    if (4 == wechatUser.getStatus()) {
                        openThirdEmployeeDTO.setStatus(1);
                    }
                    // 退出企业丢弃删除
                    if (5 == wechatUser.getStatus()) {
                        continue;
                    }
                    if ("1".equals(openThirdEmployeeDTO.getThirdDepartmentId()) || "0".equals(openThirdEmployeeDTO.getThirdDepartmentId())) {
                        openThirdEmployeeDTO.setThirdDepartmentId(corpId);
                    }
                    employeeList.add(openThirdEmployeeDTO);
                }
                //同步
                weChatIsvSyncThirdOrgService.syncThird(OpenType.WECHAT_ISV.getType(), companyId, departmentList, employeeList);
                long end = System.currentTimeMillis();
                log.info("企业微信isv组织人员同步完成，用时{}分钟{}秒...", (end - start) / 60000, ((end - start) % 60000) / 1000);
            } catch (Exception e) {
                log.error("同步组织机构人员失败", e);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        }

    }
}
