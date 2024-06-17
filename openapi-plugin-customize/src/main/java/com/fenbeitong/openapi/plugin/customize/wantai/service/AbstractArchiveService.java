package com.fenbeitong.openapi.plugin.customize.wantai.service;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.archive.integration.api.data.dto.ArchiveOriginalDataReqDTO;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.ArchiveTaskStatusType;
import com.fenbeitong.openapi.plugin.customize.wantai.constant.WanTaiArchiveConstant;
import com.fenbeitong.openapi.plugin.customize.wantai.dao.OpenArchiveFiscalPeriodDao;
import com.fenbeitong.openapi.plugin.customize.wantai.dao.OpenArchiveTaskDao;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ArchiveDataJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.FilingJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.entity.OpenArchiveFiscalPeriod;
import com.fenbeitong.openapi.plugin.customize.wantai.entity.OpenArchiveTask;
import com.fenbeitong.openapi.plugin.etl.service.IEtlStrategyService;
import com.fenbeitong.openapi.plugin.support.earchive.service.IEArchiveService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.ExceptionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
abstract class AbstractArchiveService {

    @Autowired
    private OpenArchiveFiscalPeriodDao archiveFiscalPeriodDao;

    @Autowired
    private OpenArchiveTaskDao archiveTaskDao;

    @Autowired
    private IEtlStrategyService etlStrategyService;

    @Autowired
    private IEArchiveService archiveService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 归档指令
     */
    @Async
    public void filingUpLock(FilingJobReqDTO filingJobReqDTO) {
        String lockKey = filingJobReqDTO.getLockKey();
        lockKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY,
            MessageFormat.format(WanTaiArchiveConstant.ARCHIVE_FILING_DATA_REDIS_KEY, lockKey));
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 3600 * 1000L);
        if (lockTime > 0) {
            try {
                filingUp(filingJobReqDTO);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁");
        }
    }

    public void filingUp(FilingJobReqDTO filingJobReqDTO) {
        List<FilingJobReqDTO.BookInfo> bookInfos = filingJobReqDTO.getBookInfo();
        List<String> thirdArchiveTypes = filingJobReqDTO.getThirdArchiveType();
        Map<String, String> archiveTypeMapping = filingJobReqDTO.getArchiveTypeMapping();
        String sysCode = getSysCode();
        for (FilingJobReqDTO.BookInfo bookInfo : bookInfos) {
            String companyId = bookInfo.getCompanyId();
            String orgCode = bookInfo.getOrgCode();
            List<String> bookCodes = bookInfo.getBookCode();
            for (String bookCode : bookCodes) {
                for (int i = 0; i <= filingJobReqDTO.getFilingMonth(); i++) {
                    for (String thirdArchiveType : thirdArchiveTypes) {
                        String fiscalPeriod = afterMonthToNowDate(-i);
                        String endTime = DateUtils.toSimpleStr(DateUtils.now());
                        String taskId = RandomUtils.bsonId();
                        //查询归档断点
                        String startTime = checkPoine(companyId, fiscalPeriod, sysCode, thirdArchiveType);
                        //初始化归档task
                        OpenArchiveTask openArchiveTask =
                            OpenArchiveTask.builder().id(RandomUtils.bsonId()).taskId(taskId).companyId(
                                companyId).fiscalPeriod(
                                fiscalPeriod).status(ArchiveTaskStatusType.INIT.getKey()).startTime(
                                DateUtils.toDate(startTime)).endTime(DateUtils.toDate(endTime)).thirdArchiveType(
                                thirdArchiveType).archiveType(archiveTypeMapping.get(thirdArchiveType)).orgCode(
                                orgCode).sysCode(sysCode).bookCode(bookCode).build();
                        archiveTaskDao.save(openArchiveTask);
                        //归档
                        try {
                            callMakedoc(filingJobReqDTO, openArchiveTask);
                            openArchiveTask.setStatus(ArchiveTaskStatusType.FILING.getKey());
                            //存储断点
                            archiveFiscalPeriodDao.createOrUpdateArchiveFiscal(companyId, orgCode, fiscalPeriod,
                                endTime, sysCode, thirdArchiveType);
                        } catch (Exception e) {
                            log.info("归档失败", e);
                            openArchiveTask.setStatus(ArchiveTaskStatusType.ERROR.getKey());
                            openArchiveTask.setExecuteResultContent(ExceptionUtils.getStackTraceAsString(e));
                        }
                        archiveTaskDao.updateById(openArchiveTask);
                    }
                }
            }
        }
    }

    @Async
    public void syncArchiveLock(ArchiveDataJobReqDTO archiveDataJobReqDTO) {
        String lockKey = archiveDataJobReqDTO.getLockKey();
        lockKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY,
            MessageFormat.format(WanTaiArchiveConstant.ARCHIVE_GET_DATA_REDIS_KEY, lockKey));
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 7200 * 1000L);
        if (lockTime > 0) {
            try {
                syncArchive(archiveDataJobReqDTO);
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        } else {
            log.info("未获取到锁");
        }
    }

    public void syncArchive(ArchiveDataJobReqDTO archiveDataJobReqDTO) {
        List<OpenArchiveTask> openArchiveTasks =
            archiveTaskDao.listByStatus(ArchiveTaskStatusType.FILING_COMPLETE.getKey(),
                getSysCode());
        if (ObjectUtils.isEmpty(openArchiveTasks)) {
            log.info("无归档完成任务");
            return;
        }
        for (OpenArchiveTask openArchiveTask : openArchiveTasks) {
            try {
                syncArchiveTask(archiveDataJobReqDTO, openArchiveTask);
            } catch (Exception e) {
                openArchiveTask.setStatus(ArchiveTaskStatusType.ERROR.getKey());
                openArchiveTask.setExecuteResultContent(ExceptionUtils.getStackTraceAsString(e));
            }
            archiveTaskDao.updateById(openArchiveTask);

        }
    }

    /**
     * 处理归档完成任务
     *
     * @param archiveDataJobReqDTO
     * @param openArchiveTask
     */
    private void syncArchiveTask(ArchiveDataJobReqDTO archiveDataJobReqDTO,
        OpenArchiveTask openArchiveTask) {
        //取数
        List<Map<String, Object>> data = callGetData(archiveDataJobReqDTO, openArchiveTask);
        if (!ObjectUtils.isEmpty(data)) {
            //etl转换
            List<Map<String, Object>> transfer =
                etlStrategyService.transfer(archiveDataJobReqDTO.getEtlConfigId(),
                    data, openArchiveTask, archiveDataJobReqDTO);
            List<ArchiveOriginalDataReqDTO> originalDataReqDTOList =
                JsonUtils.toObj(JsonUtils.toJson(transfer),
                    new TypeReference<List<ArchiveOriginalDataReqDTO>>() {
                    });
            List<List<ArchiveOriginalDataReqDTO>> batchList = CollectionUtils.batch(originalDataReqDTOList, 50);
            for (List<ArchiveOriginalDataReqDTO> subBatch : batchList) {
                archiveService.batchSave(subBatch);
            }
            openArchiveTask.setStatus(ArchiveTaskStatusType.PROCESS_COMPLETE.getKey());
        }
    }


    public static String afterMonthToNowDate(Integer month) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
        return df.format(calendar.getTime());
    }

    /**
     * 调用归档
     *
     * @param filingJobReqDTO
     * @param openArchiveTask
     * @return
     */
    abstract void callMakedoc(FilingJobReqDTO filingJobReqDTO, OpenArchiveTask openArchiveTask);


    /**
     * 获取档案数据
     *
     * @param archiveDataJobReqDTO
     * @param openArchiveTask
     * @return
     */
    abstract List<Map<String, Object>> callGetData(ArchiveDataJobReqDTO archiveDataJobReqDTO,
        OpenArchiveTask openArchiveTask);


    /**
     * 查询企业账期断点
     * 上次结束时间为断点，不存在的则为账期第1天
     *
     * @param companyId
     * @param fiscalPeriod
     * @return
     */
    private String checkPoine(String companyId, String fiscalPeriod, String sysCode, String thirdArchiveType) {
        String startTime = fiscalPeriod + "-01 00:00:00";
        OpenArchiveFiscalPeriod openArchiveFiscalPeriod =
            archiveFiscalPeriodDao.getByFiscalPeriod(companyId, fiscalPeriod, sysCode, thirdArchiveType);
        if (openArchiveFiscalPeriod != null) {
            startTime = DateUtils.toSimpleStr(openArchiveFiscalPeriod.getLastSyncTime());
        }
        return startTime;
    }


    protected String getSysCode() {
        return WanTaiArchiveConstant.SYS_CODE_DEFAULT;
    }

    /**
     * 回调
     *
     * @param taskId
     */
    public void callback(String taskId) {
        OpenArchiveTask openArchiveTask = archiveTaskDao.getByTaskId(taskId);
        if (openArchiveTask != null && (openArchiveTask.getStatus().equals(ArchiveTaskStatusType.INIT.getKey())
            || openArchiveTask.getStatus().equals(ArchiveTaskStatusType.FILING.getKey()))) {
            openArchiveTask.setStatus(ArchiveTaskStatusType.FILING_COMPLETE.getKey());
            archiveTaskDao.updateById(openArchiveTask);
        }
    }


}
