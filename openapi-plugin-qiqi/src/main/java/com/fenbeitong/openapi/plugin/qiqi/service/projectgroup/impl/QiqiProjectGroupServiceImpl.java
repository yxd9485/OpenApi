package com.fenbeitong.openapi.plugin.qiqi.service.projectgroup.impl;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiProjectGroupReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.qiqi.service.projectgroup.IQiqiProjectGroupService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.service.FullDataSynchronizer;
import com.fenbeitong.openapi.plugin.support.project.dto.AddProjectGroupReqDTO;

import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.exception.ArgumentException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * @ClassName QiqiProjectGroupServiceImpl
 * @Description 企企同步项目分组数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
@Service
@Slf4j
public class QiqiProjectGroupServiceImpl implements IQiqiProjectGroupService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    QiqiCommonReqServiceImpl qiqiCommonReqService;
    @Resource(name = "projectGroupFullDataSynchronizer")
    FullDataSynchronizer projectGroupSynchronizer;

    @Override
    @Async
    public void syncQiqiProjectGroup(String companyId) throws Exception {
        log.info("【qiqi】 syncQiqiProjectGroup, 开始同步项目分组,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.PROJECT_GROUP_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncProjectGroup(companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【qiqi】 syncQiqiProjectGroup, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }
    }

    /**
     * 全量拉取项目分组数据并存到中间表
     *
     * @param companyId
     * @throws Exception
     */
    public void syncProjectGroup(String companyId) throws Exception {
        //1.全量拉取项目分组数据
        List<QiqiProjectGroupReqDTO> projectGroupInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.PROJECT_CATEGORY.getCode(), QiqiProjectGroupReqDTO.class, "level = 0 and id is not null order by level", null);
        if (CollectionUtils.isBlank(projectGroupInfos)) {
            log.info("【qiqi】 syncProjectGroup, 查询三方项目分组数据为空");
            return;
        }
        //2.字段转换
        List<AddProjectGroupReqDTO> groupDTOList = Lists.newArrayList();
        for (QiqiProjectGroupReqDTO groupDTO : projectGroupInfos) {
            AddProjectGroupReqDTO projectGroupDTO = AddProjectGroupReqDTO.builder().thirdGroupId(groupDTO.getId()).groupName(groupDTO.getName()).groupDesc(groupDTO.getDescrtption()).build();
            groupDTOList.add(projectGroupDTO);
        }
        log.info("【qiqi】 syncProjectGroup,参数groupDTOList:{}", JsonUtils.toJson(groupDTOList));
        //3.同步数据
        projectGroupSynchronizer.sync(OpenType.QIQI, companyId, groupDTOList);
    }
}
