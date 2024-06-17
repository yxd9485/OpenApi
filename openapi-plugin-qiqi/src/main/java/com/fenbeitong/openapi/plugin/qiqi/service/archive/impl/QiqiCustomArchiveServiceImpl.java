package com.fenbeitong.openapi.plugin.qiqi.service.archive.impl;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseCode;
import com.fenbeitong.openapi.plugin.qiqi.common.exception.OpenApiQiqiException;
import com.fenbeitong.openapi.plugin.qiqi.constant.ObjectTypeEnum;
import com.fenbeitong.openapi.plugin.qiqi.constant.QiqiSyncConstant;
import com.fenbeitong.openapi.plugin.qiqi.constant.StateEnum;
import com.fenbeitong.openapi.plugin.qiqi.dto.QiqiCustomArchiveReqDTO;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.qiqi.service.AbstractQiqiCommonService;
import com.fenbeitong.openapi.plugin.qiqi.service.archive.IQiqiCustomArchiveService;
import com.fenbeitong.openapi.plugin.qiqi.service.common.QiqiCommonReqServiceImpl;
import com.fenbeitong.openapi.plugin.support.archive.entity.OpenThirdCustomArchiveProject;
import com.fenbeitong.openapi.plugin.support.archive.service.OpenThirdCustomArchiveProjectService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.exception.ArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * @ClassName QiqiCustomArchiveServiceImpl
 * @Description 企企同步自定义档案数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/18
 **/
@Service
@Slf4j
public class QiqiCustomArchiveServiceImpl extends AbstractQiqiCommonService implements IQiqiCustomArchiveService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    QiqiCommonReqServiceImpl qiqiCommonReqService;
    @Autowired
    OpenThirdCustomArchiveProjectService archiveProjectService;

    @Override
    @Async
    public void syncQiqiCustomArchive(String companyId) throws Exception {
        log.info("【qiqi】 syncQiqiCustomArchive, 开始同步自定义档案,companyId={}", companyId);
        String lockKey = MessageFormat.format(RedisKeyConstant.CUSTOM_ARCHIVE_SYNC_REDIS_KEY, companyId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, RedisKeyConstant.SYNC_ORG_EMPLOYEE_LOCK_TIME);
        if (lockTime > 0) {
            try {
                syncCustomArchive(companyId);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("【qiqi】 syncQiqiCustomArchive, 未获取到锁，companyId={}", companyId);
            throw new ArgumentException("未获取到锁");
        }

    }

    /**
     * 全量拉取自定义档案数据并存到中间表
     *
     * @param companyId
     * @throws Exception
     */
    public void syncCustomArchive(String companyId) throws Exception {
        //1.全量拉取自定义档案数据
        List<QiqiCustomArchiveReqDTO> customArchiveInfos = qiqiCommonReqService.buildQiqiReq(companyId, ObjectTypeEnum.BUDGET_ACCOUNT.getCode(), QiqiCustomArchiveReqDTO.class, "id is not null and accountType.id = 'BudgetAccountType.expense' order by level", null);
        if (CollectionUtils.isBlank(customArchiveInfos)) {
            log.info("【qiqi】 syncCustomArchive, 查询三方自定义档案数据为空");
            return;
        }
        //2.字段转换
        List<OpenThirdCustomArchiveProject> archiveProjectList = customArchiveConvert(customArchiveInfos, companyId);
        //3.同步数据
        archiveProjectService.syncAllCustomArchiveProject(companyId, OpenType.QIQI.getType(), archiveProjectList);
    }

    @Override
    public List<OpenThirdCustomArchiveProject> customArchiveConvert(List<QiqiCustomArchiveReqDTO> customArchiveInfos, String companyId) {

        QiqiCorpInfo corpInfo = getCorpInfo(companyId);
        //计算当日零点时间戳
        Long currentTimestamps = System.currentTimeMillis();
        Long oneDayTimestamps = Long.valueOf(60 * 60 * 24 * 1000);
        long todayTimestamps = currentTimestamps - (currentTimestamps + 60 * 60 * 8 * 1000) % oneDayTimestamps;

        //字段转换
        List<OpenThirdCustomArchiveProject> archiveProjectList = Lists.newArrayList();
        for (QiqiCustomArchiveReqDTO customArchiveInfo : customArchiveInfos) {
            OpenThirdCustomArchiveProject customArchiveProject = new OpenThirdCustomArchiveProject();
            customArchiveProject.setThirdArchiveId(QiqiSyncConstant.CUSTOM_ARCHIVE_ID_PRE + companyId);
            customArchiveProject.setArchiveName(corpInfo.getCompanyName() + QiqiSyncConstant.ARCHIVE_NAME_SUFFIX);
            customArchiveProject.setCode(customArchiveInfo.getCode());
            customArchiveProject.setName(customArchiveInfo.getName());
            customArchiveProject.setThirdId(customArchiveInfo.getId());
            customArchiveProject.setCompanyId(companyId);
            customArchiveProject.setOpenType(OpenType.QIQI.getType());
            customArchiveProject.setUsableRange(QiqiSyncConstant.USE_RANGE_ALL);
            if (customArchiveInfo.getDisabledTime() != null) {
                //如果停用日期大于当前日期，则为启用
                long timeDifference = customArchiveInfo.getDisabledTime() - todayTimestamps;
                customArchiveProject.setState(timeDifference > 0 ? StateEnum.STATE_ENABLE.getCode() : StateEnum.STATE_DISABBLE.getCode());
            } else {
                //停用日期为空时默认启用
                customArchiveProject.setState(StateEnum.STATE_ENABLE.getCode());
            }
            if (StringUtils.isNotBlank(customArchiveInfo.getParentId())) {
                customArchiveProject.setThirdParentId(customArchiveInfo.getParentId());
            }
            archiveProjectList.add(customArchiveProject);
        }
        log.info("【qiqi】 customArchiveConvert, 参数archiveProjectList={}", JSONObject.toJSONString(archiveProjectList));
        return archiveProjectList;
    }
}
