package com.fenbeitong.openapi.plugin.customize.yuntianlifei.service.impl;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.AesUtils;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto.YunTianJobConfigDto;
import com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto.YunTianProjectDto;
import com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto.YunTianUsersWithDeptsDto;
import com.fenbeitong.openapi.plugin.customize.yuntianlifei.service.YunTianOrgService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdOrgUnitDTO;
import com.fenbeitong.openapi.plugin.support.init.service.DepartmentUtilService;
import com.fenbeitong.openapi.plugin.support.init.service.OpenSyncThirdOrgService;
import com.fenbeitong.openapi.plugin.support.project.dto.MemberNewEntity;
import com.fenbeitong.openapi.plugin.support.project.dto.OpenThirdProject;
import com.fenbeitong.openapi.plugin.support.project.service.OpenBaseProjectService;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectServiceV2;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: YunTianLiFeiOrgImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/4/25 4:23 下午
 */

@ServiceAspect
@Service
@Slf4j
public class YunTianOrgServiceImpl implements YunTianOrgService {

    @Autowired
    private OpenSyncThirdOrgService openSyncThirdOrgService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    DepartmentUtilService departmentUtilService;
    @Autowired
    OpenProjectServiceV2 openProjectServiceV2;
    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Autowired
    private OpenBaseProjectService openBaseProjectService;

    @Override
    @Async
    public void orgSync(YunTianJobConfigDto configDto) {
        log.info("云天厉飞开始同步组织机构人员,configDto={}", JsonUtils.toJson(configDto));
        String lockKey = MessageFormat.format(RedisKeyConstant.ORG_EMPLOYEE_SYNC_REDIS_KEY, configDto.getCompanyId());
        Long lockTime =
            RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                String resultDataAes = RestHttpUtils.get(configDto.getUrl(), new HashMap<>());
                String resultData = AesUtils.decrypt(resultDataAes, configDto.getSecret());
                YunTianUsersWithDeptsDto usersWithDeptsDto =
                    JsonUtils.toObj(resultData, YunTianUsersWithDeptsDto.class);
                List<OpenThirdOrgUnitDTO> openThirdOrgUnitDTOList = new ArrayList<>();
                if (ObjectUtils.isEmpty(usersWithDeptsDto)) {
                    throw new OpenApiCustomizeException(500, "云天厉飞组织架构数据转换错误");
                }
                List<OpenThirdOrgUnitDTO> finalOpenThirdOrgUnitDTOList = openThirdOrgUnitDTOList;
                usersWithDeptsDto.getData().getDeptList().forEach(deptListBean -> {
                    if ("1".equals(StringUtils.obj2str(deptListBean.getStatus()))) {
                        finalOpenThirdOrgUnitDTOList.add(OpenThirdOrgUnitDTO.builder()
                            .companyId(configDto.getCompanyId())
                            .thirdOrgUnitId(deptListBean.getId())
                            .thirdOrgUnitParentId(deptListBean.getParentId())
                            .thirdOrgUnitName(deptListBean.getName())
                            .build());
                    }
                });
                List<OpenThirdEmployeeDTO> openThirdEmployeeDTOList = new ArrayList<>();
                List<OpenThirdEmployeeDTO> finalOpenThirdEmployeeDTOList = openThirdEmployeeDTOList;
                usersWithDeptsDto.getData().getUserList().forEach(userListBean -> {
                    OpenThirdEmployeeDTO openThirdEmployeeDTO = new OpenThirdEmployeeDTO();
                    openThirdEmployeeDTO.setCompanyId(configDto.getCompanyId());
                    openThirdEmployeeDTO.setThirdEmployeeId(userListBean.getUserId());
                    openThirdEmployeeDTO.setEmployeeNumber(userListBean.getIdCode());
                    openThirdEmployeeDTO.setThirdEmployeeName(userListBean.getName());
                    openThirdEmployeeDTO.setThirdDepartmentId(userListBean.getDeptId());
                    openThirdEmployeeDTO.setThirdEmployeePhone(userListBean.getPhone());
                    openThirdEmployeeDTO.setThirdEmployeeEmail(userListBean.getEmail());
                    openThirdEmployeeDTO.setThirdEmployeeGender("1".equals(userListBean.getSex()) ? 2 : 1);
                    openThirdEmployeeDTO.setThirdEmployeeIdCard(userListBean.getIdCard());
                    if (!StringUtils.isBlank(userListBean.getBankAccount()) && !StringUtils.isBlank(
                        userListBean.getBankName())) {
                        Map<String, Object> map = Maps.newHashMap();
                        map.put("bankAccount", userListBean.getBankAccount());
                        map.put("bankName", userListBean.getBankName());
                        openThirdEmployeeDTO.setExtAttr(map);
                    }
                    finalOpenThirdEmployeeDTOList.add(openThirdEmployeeDTO);
                });
                // 部门排序
                openThirdOrgUnitDTOList =
                    departmentUtilService.deparmentSort(openThirdOrgUnitDTOList, configDto.getRootId(),
                        configDto.getCompanyName());
                // 根据有效部门过滤人员
                openThirdEmployeeDTOList =
                    departmentUtilService.getValidEmployee(openThirdEmployeeDTOList, openThirdOrgUnitDTOList);
                openSyncThirdOrgService.syncThird(OpenType.OPEN_API.getType(), configDto.getCompanyId(),
                    openThirdOrgUnitDTOList, openThirdEmployeeDTOList);
            } catch (Exception e) {
                log.error("", e);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        }
    }

    @Override
    @Async
    public void projectSync(YunTianJobConfigDto configDto) {
        log.info("云天厉飞开始同步项目,configDto={}", JsonUtils.toJson(configDto));
        String resultDataAes = RestHttpUtils.get(configDto.getUrl(), new HashMap<>());
        String resultData = null;
        try {
            resultData = AesUtils.decrypt(resultDataAes, configDto.getSecret());
        } catch (Exception e) {
            log.info("云天励飞aes解密失败", e);
            return;
        }
        String thirdEmployeeId = superAdminUtils.companySuperAdmin(configDto.getCompanyId()).getThirdEmployeeId();
        YunTianProjectDto yunTianProjectDto = JsonUtils.toObj(resultData, YunTianProjectDto.class);
        if (ObjectUtils.isEmpty(yunTianProjectDto)) {
            throw new OpenApiCustomizeException(500, "云天厉飞项目数据转换错误");
        }
        List<OpenThirdProject> openThirdProjectList = Lists.newArrayList();
        yunTianProjectDto.getData().forEach(dataBean -> {
            OpenThirdProject openThirdProject = new OpenThirdProject();
            openThirdProject.setCompanyId(configDto.getCompanyId());
            openThirdProject.setUserId(thirdEmployeeId);
            openThirdProject.setThirdId(dataBean.getProjectNo());
            openThirdProject.setCode(dataBean.getProjectNo());
            openThirdProject.setName(dataBean.getName());
            openThirdProject.setUsableRange(configDto.getUsableRange());
            openThirdProject.setExpiredType(1);
            openThirdProject.setState(("0").equals(StringUtils.obj2str(dataBean.getStatus())) ? 1 : 0);
            List<MemberNewEntity> manager = new ArrayList<>();
            manager.add(MemberNewEntity.builder().isManager(true).memberId(dataBean.getPmId()).build());
            openThirdProject.setParsedManagers(manager);
            List<MemberNewEntity> member = new ArrayList<>();
            dataBean.getMembers().forEach(t -> {
                member.add(MemberNewEntity.builder().memberId(t.getId()).build());
            });
            openThirdProject.setParsedMembers(member);
            openThirdProjectList.add(openThirdProject);
        });
        openBaseProjectService.syncAllProject(configDto.getCompanyId(), OpenType.UNKNOW.getType(), openThirdProjectList,
            configDto.isForceUpdate());

    }
}
