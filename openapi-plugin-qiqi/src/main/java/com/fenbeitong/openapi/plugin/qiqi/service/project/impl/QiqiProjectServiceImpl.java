package com.fenbeitong.openapi.plugin.qiqi.service.project.impl;

import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.constant.QiqiSyncConstant;
import com.fenbeitong.openapi.plugin.qiqi.constant.UseRangeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.*;
import com.fenbeitong.openapi.plugin.qiqi.service.AbstractQiqiCommonService;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.project.IQiqiProjectService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.project.dto.MemberNewEntity;
import com.fenbeitong.openapi.plugin.support.project.dto.OpenThirdProject;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.project.service.OpenBaseProjectService;
import com.fenbeitong.openapi.plugin.support.util.SuperAdminUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.exception.ArgumentException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QiqiProjectServiceImpl
 * @Description 企企同步项目数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
@Service
@Slf4j
public class QiqiProjectServiceImpl extends AbstractQiqiCommonService implements IQiqiProjectService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private QiqiCommonReqServiceImpl qiqiCommonReqService;
    @Autowired
    private OpenBaseProjectService openBaseProjectService;
    @Autowired
    private SuperAdminUtils superAdminUtils;

    @Override
    @Async
    public void syncQiqiProject(String companyId) throws Exception {
        log.info("【qiqi】 syncQiqiProject, 开始同步项目,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.PROJECT_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncProject(companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【qiqi】 syncQiqiProject, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }

    /**
     * 全量拉取项目数据并存到中间表
     *
     * @param companyId
     * @throws Exception
     */
    public void syncProject(String companyId) throws Exception {

        //1.全量拉取项目数据
        List<QiqiProjectReqDTO> projectInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.PROJECT.getCode(), QiqiProjectReqDTO.class, "id is not null and isChangeBill = false", getTreeParam());
        if (CollectionUtils.isBlank(projectInfos)) {
            log.info("【qiqi】 syncProject, 查询三方项目数据为空");
            return;
        }
        //获取当前时间戳
        String currentStr = StringUtil.obj2str(System.currentTimeMillis());
        Long currentTime = Long.valueOf(currentStr.substring(0, currentStr.length() - 3));
        //全量拉取在职人员
        List<QiqiEmployeeReqDTO> userInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.USER.getCode(), QiqiEmployeeReqDTO.class, "id is not null and (disabledTime is null or disabledTime>to_timestamp(" + currentTime + ")) and (systemDisabledTime is  null or systemDisabledTime>to_timestamp(" + currentTime + ")) and statusId='UserStatus.incumbent' and name !='小企'", null);
        if (CollectionUtils.isBlank(userInfos)) {
            log.info("【qiqi】 syncProject, 查询三方在职人员数据为空");
            return;
        }
        //2.字段转换
        List<OpenThirdProject> projectDTOList = projectConvert(companyId, projectInfos, userInfos);

        //3.同步数据
        openBaseProjectService.syncAllProject(companyId, OpenType.QIQI.getType(), projectDTOList, true);

    }

    public List<OpenThirdProject> projectConvert(String companyId, List<QiqiProjectReqDTO> projectInfos, List<QiqiEmployeeReqDTO> userInfos) throws Exception {
        //type 为 2 ，需要传三方人员id
        String thirdEmployeeId = superAdminUtils.superAdminThirdEmployeeId(companyId);
        if (StringUtils.isEmpty(thirdEmployeeId)) {
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }
        //字段转换
        List<OpenThirdProject> projectDTOList = Lists.newArrayList();
        for (QiqiProjectReqDTO qiqiProjectDTO : projectInfos) {
            OpenThirdProject projectDTO = new OpenThirdProject();
            projectDTO.setUserId(thirdEmployeeId);
            projectDTO.setThirdId(qiqiProjectDTO.getId());
            projectDTO.setCode(qiqiProjectDTO.getCode());
            projectDTO.setName(qiqiProjectDTO.getName());
            projectDTO.setRemark(qiqiProjectDTO.getDescrtption());
            projectDTO.setState(QiqiSyncConstant.BILL_STATUS_EFFECTIVE.equals(qiqiProjectDTO.getBillStatus()) ? 1 : 0);
            projectDTO.setExpiredType(1);
            projectDTO.setOpenType(OpenType.QIQI.getType());
            if (!ObjectUtils.isEmpty(qiqiProjectDTO.getPlanStartDate())) {
                projectDTO.setStartTime(new Date(qiqiProjectDTO.getPlanStartDate()));
            }
            if (!ObjectUtils.isEmpty(qiqiProjectDTO.getPlanEndDate())) {
                projectDTO.setEndTime(new Date(qiqiProjectDTO.getPlanEndDate()));
            }
            projectDTO.setCompanyId(companyId);
            projectDTO.setParsedThirdGroupIds(Lists.newArrayList(qiqiProjectDTO.getCategoryId()));
            //项目负责人，企企只能选一个负责人
            if (!StringUtils.isEmpty(qiqiProjectDTO.getOwnerUserId())) {
                projectDTO.setParsedManagers(Lists.newArrayList(MemberNewEntity.builder().memberId(qiqiProjectDTO.getOwnerUserId()).isManager(true).build()));
            }
            //项目成员取企企项目成员中在职的人员
            List<String> userIdList = getProjectMember(qiqiProjectDTO, userInfos);
            if (CollectionUtils.isNotBlank(userIdList)) {
                List<MemberNewEntity> members = Lists.newArrayList();
                for (String s : userIdList) {
                    members.add(MemberNewEntity.builder().memberId(s).build());
                }
                projectDTO.setParsedMembers(members);
            }
            if (CollectionUtils.isNotBlank(projectDTO.getParsedManagers()) || CollectionUtils.isNotBlank(projectDTO.getParsedMembers())) {
                projectDTO.setUsableRange(UseRangeEnum.LIMIT_PROJECT_MEMBER.getCode());
            } else {
                projectDTO.setUsableRange(UseRangeEnum.UNLIMITED.getCode());
            }
            projectDTOList.add(projectDTO);
        }
        log.info("projectDTOList:{}", JsonUtils.toJson(projectDTOList));
        return projectDTOList;
    }

    @Override
    public SupportUcThirdProjectReqDTO projectConvertForAdd(String companyId, QiqiProjectReqDTO qiqiProjectDTO) throws Exception {

        //获取当前时间戳
        String currentStr = StringUtil.obj2str(System.currentTimeMillis());
        Long currentTime = Long.valueOf(currentStr.substring(0, currentStr.length() - 3));
        //全量拉取在职人员
        List<QiqiEmployeeReqDTO> userInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.USER.getCode(), QiqiEmployeeReqDTO.class, "id is not null and (disabledTime is null or disabledTime>to_timestamp(" + currentTime + ")) and (systemDisabledTime is  null or systemDisabledTime>to_timestamp(" + currentTime + ")) and statusId='UserStatus.incumbent' and name !='小企'", null);
        if (CollectionUtils.isBlank(userInfos)) {
            log.info("【qiqi】 syncProject, 查询三方在职人员数据为空");
            return null;
        }
        SupportUcThirdProjectReqDTO projectDTO = new SupportUcThirdProjectReqDTO();

        //type 为 2 ，需要传三方人员id
        String thirdEmployeeId = superAdminUtils.superAdminThirdEmployeeId(companyId);
        if (StringUtils.isEmpty(thirdEmployeeId)) {
            throw new OpenApiQiqiException(QiqiResponseCode.DATA_NOT_EXIST);
        }

        projectDTO.setUserId(thirdEmployeeId);
        projectDTO.setThirdCostId(qiqiProjectDTO.getId());
        projectDTO.setCode(qiqiProjectDTO.getCode());
        projectDTO.setName(qiqiProjectDTO.getName());
        projectDTO.setDescription(qiqiProjectDTO.getDescrtption());
        projectDTO.setState(QiqiSyncConstant.BILL_STATUS_EFFECTIVE.equals(qiqiProjectDTO.getBillStatus()) ? 1 : 0);
        projectDTO.setExpiredState(1);

        //时间处理
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!ObjectUtils.isEmpty(qiqiProjectDTO.getPlanStartDate())) {
            LocalDateTime beginLocateDate = Instant.ofEpochMilli(qiqiProjectDTO.getPlanStartDate()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
            String beginDate = beginLocateDate.format(dtf);
            projectDTO.setBeginDate(beginDate);
        }
        if (!ObjectUtils.isEmpty(qiqiProjectDTO.getPlanEndDate())) {
            LocalDateTime endLocateDate = Instant.ofEpochMilli(qiqiProjectDTO.getPlanEndDate()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
            String endDate = endLocateDate.format(dtf);
            projectDTO.setEndDate(endDate);
        }

        projectDTO.setCompanyId(companyId);

        projectDTO.setThirdGroupList(Lists.newArrayList(qiqiProjectDTO.getCategoryId()));
        if (!StringUtils.isEmpty(qiqiProjectDTO.getOwnerUserId())) {
            projectDTO.setManager(Lists.newArrayList(MemberNewEntity.builder().memberId(qiqiProjectDTO.getOwnerUserId()).isManager(true).build()));
        }

        //项目成员取企企项目成员中在职的人员
        List<String> userIdList = getProjectMember(qiqiProjectDTO, userInfos);
        if (CollectionUtils.isNotBlank(userIdList)) {
            List<MemberNewEntity> members = Lists.newArrayList();
            for (String s : userIdList) {
                members.add(MemberNewEntity.builder().memberId(s).build());
            }
            projectDTO.setMember(members);
        }
        if (CollectionUtils.isNotBlank(projectDTO.getManager()) || CollectionUtils.isNotBlank(projectDTO.getMember())) {
            projectDTO.setUsableRange(UseRangeEnum.LIMIT_PROJECT_MEMBER.getCode());
        } else {
            projectDTO.setUsableRange(UseRangeEnum.UNLIMITED.getCode());
        }
        return projectDTO;
    }

    /**
     * 项目成员取企企项目成员中在职的人员:得到项目团队的人员，到企企人员表查询人员信息，过滤出在职的人员集合
     *
     * @param qiqiProjectDTO
     * @param userInfos
     * @return
     * @throws Exception
     */
    private List<String> getProjectMember(QiqiProjectReqDTO qiqiProjectDTO, List<QiqiEmployeeReqDTO> userInfos) {
        List<String> userIdList = Lists.newArrayList();
        //项目成员集合
        List<QiqiProjectMemberDTO> memberDTOList = qiqiProjectDTO.getMembersObject();
        //项目成员不为空，就过滤出在职的人员
        if (CollectionUtils.isNotBlank(memberDTOList)) {
            List<String> userIds = memberDTOList.stream().map(QiqiProjectMemberDTO::getUserId).collect(Collectors.toList());
            List<String> qiqiUserIds = userInfos.stream().map(QiqiEmployeeReqDTO::getId).collect(Collectors.toList());
            return userIds.stream().filter(userId -> qiqiUserIds.contains(userId)).collect(Collectors.toList());
        }
        return userIdList;
    }

    @Override
    public List<QiqiCommonReqDetailDTO> getTreeParam() {
        List<QiqiCommonReqDetailDTO> qiqiCommonReqDetailList = Lists.newArrayList();
        //树形参数封装
        QiqiCommonReqDetailDTO commonReqDetail = new QiqiCommonReqDetailDTO();
        Field[] declaredFields = QiqiProjectMemberDTO.class.getDeclaredFields();
        String[] fieldArray = Arrays.stream(declaredFields).map(f -> f.getName()).collect(Collectors.toList()).toArray(new String[]{});
        commonReqDetail.setFieldName("membersObject");
        commonReqDetail.setFields(fieldArray);
        qiqiCommonReqDetailList.add(commonReqDetail);
        return qiqiCommonReqDetailList;
    }

}
